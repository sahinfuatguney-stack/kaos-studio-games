package com.kaosstudio.naturaldisasters;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class NaturalDisasters implements ModInitializer {
    public static final String MOD_ID = "naturaldisasters";
    private static final Random RANDOM = Random.create();
    private static int ticksUntilDisaster = 20 * 90;
    private static String activeDisaster = "";
    private static int activeTicks = 0;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> registerCommands(dispatcher));
        ServerTickEvents.END_SERVER_TICK.register(NaturalDisasters::tick);
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("afets")
            .then(CommandManager.literal("baslat")
                .then(CommandManager.literal("deprem").executes(ctx -> start(ctx.getSource(), "deprem")))
                .then(CommandManager.literal("hortum").executes(ctx -> start(ctx.getSource(), "hortum")))
                .then(CommandManager.literal("volkan").executes(ctx -> start(ctx.getSource(), "volkan")))
                .then(CommandManager.literal("sel").executes(ctx -> start(ctx.getSource(), "sel")))
                .then(CommandManager.literal("firtina").executes(ctx -> start(ctx.getSource(), "firtina")))
                .then(CommandManager.literal("meteor").executes(ctx -> start(ctx.getSource(), "meteor"))))
            .then(CommandManager.literal("durdur").executes(ctx -> stop(ctx.getSource())))
            .then(CommandManager.literal("durum").executes(ctx -> status(ctx.getSource()))));
    }

    private static int start(ServerCommandSource source, String disaster) {
        activeDisaster = disaster;
        activeTicks = 20 * 20;
        source.sendFeedback(() -> Text.literal("§c[DOĞAL AFET] §f" + disaster + " başladı!"), true);
        return 1;
    }

    private static int stop(ServerCommandSource source) {
        activeDisaster = "";
        activeTicks = 0;
        source.sendFeedback(() -> Text.literal("§a[DOĞAL AFET] §fAktif afet durduruldu."), true);
        return 1;
    }

    private static int status(ServerCommandSource source) {
        String text = activeDisaster.isEmpty() ? "aktif afet yok" : "aktif: " + activeDisaster;
        source.sendFeedback(() -> Text.literal("§e[DOĞAL AFET] §f" + text), false);
        return 1;
    }

    private static void tick(MinecraftServer server) {
        if (activeDisaster.isEmpty()) {
            ticksUntilDisaster--;
            if (ticksUntilDisaster <= 0) {
                String[] disasters = {"deprem", "hortum", "volkan", "sel", "firtina", "meteor"};
                activeDisaster = disasters[RANDOM.nextInt(disasters.length)];
                activeTicks = 20 * 20;
                ticksUntilDisaster = 20 * (120 + RANDOM.nextInt(180));
                broadcast(server, "§c[DOĞAL AFET] §fYaklaşan afet: " + activeDisaster + "!");
            }
            return;
        }

        activeTicks--;
        if (activeTicks <= 0) {
            broadcast(server, "§a[DOĞAL AFET] §f" + activeDisaster + " sona erdi.");
            activeDisaster = "";
            return;
        }

        if (activeTicks % 10 != 0) return;

        for (ServerWorld world : server.getWorlds()) {
            switch (activeDisaster) {
                case "deprem" -> earthquake(world);
                case "hortum" -> tornado(world);
                case "volkan" -> volcano(world);
                case "sel" -> flood(world);
                case "firtina" -> storm(world);
                case "meteor" -> meteor(world);
            }
        }
    }

    private static void earthquake(ServerWorld world) {
        world.getPlayers().forEach(player -> {
            BlockPos p = player.getBlockPos();
            world.spawnParticles(ParticleTypes.CLOUD, p.getX(), p.getY() + 1, p.getZ(), 8, 1.5, 0.2, 1.5, 0.02);
            if (RANDOM.nextInt(8) == 0) {
                BlockPos crack = p.add(RANDOM.nextInt(7) - 3, -1, RANDOM.nextInt(7) - 3);
                if (world.getBlockState(crack).isOf(Blocks.DIRT) || world.getBlockState(crack).isOf(Blocks.GRASS_BLOCK)) {
                    world.setBlockState(crack, Blocks.COARSE_DIRT.getDefaultState());
                }
            }
        });
    }

    private static void tornado(ServerWorld world) {
        world.getPlayers().forEach(player -> {
            BlockPos p = player.getBlockPos();
            world.spawnParticles(ParticleTypes.CLOUD, p.getX(), p.getY() + 2, p.getZ(), 20, 2, 3, 2, 0.05);
            if (RANDOM.nextInt(5) == 0) player.addVelocity((RANDOM.nextDouble() - 0.5) * 0.4, 0.25, (RANDOM.nextDouble() - 0.5) * 0.4);
        });
    }

    private static void volcano(ServerWorld world) {
        world.getPlayers().forEach(player -> {
            if (RANDOM.nextInt(4) == 0) {
                BlockPos p = player.getBlockPos().add(RANDOM.nextInt(11) - 5, 0, RANDOM.nextInt(11) - 5);
                world.setBlockState(p, Blocks.LAVA.getDefaultState());
                world.spawnParticles(ParticleTypes.LAVA, p.getX() + .5, p.getY() + 1, p.getZ() + .5, 12, .5, .5, .5, .05);
            }
        });
    }

    private static void flood(ServerWorld world) {
        world.getPlayers().forEach(player -> {
            BlockPos p = player.getBlockPos();
            for (int i = 0; i < 4; i++) {
                BlockPos water = p.add(RANDOM.nextInt(9) - 4, -1, RANDOM.nextInt(9) - 4);
                if (world.getBlockState(water).isAir()) world.setBlockState(water, Blocks.WATER.getDefaultState());
            }
        });
    }

    private static void storm(ServerWorld world) {
        world.setWeather(0, 200, true, true);
        if (RANDOM.nextInt(8) == 0) {
            world.getPlayers().forEach(player -> world.spawnEntity(new net.minecraft.entity.LightningEntity(world, player.getX(), player.getY(), player.getZ())));
        }
    }

    private static void meteor(ServerWorld world) {
        world.getPlayers().forEach(player -> {
            BlockPos p = player.getBlockPos().add(RANDOM.nextInt(17) - 8, 0, RANDOM.nextInt(17) - 8);
            world.spawnParticles(ParticleTypes.FLAME, p.getX() + .5, p.getY() + 12, p.getZ() + .5, 30, 1, 2, 1, .05);
            if (world.getBlockState(p).isSolidBlock(world, p) && RANDOM.nextInt(3) == 0) {
                world.breakBlock(p, false);
            }
        });
    }

    private static void broadcast(MinecraftServer server, String message) {
        server.getPlayerManager().broadcast(Text.literal(message), false);
    }
}
