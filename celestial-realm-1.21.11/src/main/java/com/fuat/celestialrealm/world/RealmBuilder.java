package com.fuat.celestialrealm.world;

import com.fuat.celestialrealm.CelestialRealmMod;
import net.minecraft.block.BlockState;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class RealmBuilder {
    private static boolean built = false;

    private RealmBuilder() {}

    public static void applyLowGravity(ServerWorld world) {
        for (PlayerEntity player : world.getPlayers()) {
            Vec3d v = player.getVelocity();
            if (!player.isOnGround() && v.y < 0.0D) {
                player.setVelocity(v.x, Math.max(v.y * 0.55D, -0.7D), v.z);
            }
        }
    }

    public static void buildStarterRealm(ServerWorld world) {
        if (built) return;
        built = true;

        buildIsland(world, new BlockPos(0, 20, 0), 12);
        buildIsland(world, new BlockPos(34, 34, -18), 8);
        buildIsland(world, new BlockPos(-32, 42, 24), 7);

        buildShrine(world, new BlockPos(0, 31, 0));
        buildTree(world, new BlockPos(-7, 32, -4), 7);
        buildTree(world, new BlockPos(6, 32, 5), 6);
        buildTree(world, new BlockPos(29, 43, -18), 6);

        SlimeEntity wisp = CelestialRealmMod.NEBULA_WISP.spawn(world, new BlockPos(4, 33, 2), SpawnReason.COMMAND);
        if (wisp != null) {
            wisp.setCustomName(Text.translatable("entity.celestial_realm.nebula_wisp"));
            wisp.setCustomNameVisible(true);
            wisp.setSize(1, true);
        }

        MagmaCubeEntity crawler = CelestialRealmMod.VOID_CRAWLER.spawn(world, new BlockPos(-3, 32, -3), SpawnReason.COMMAND);
        if (crawler != null) {
            crawler.setCustomName(Text.translatable("entity.celestial_realm.void_crawler"));
            crawler.setCustomNameVisible(true);
            crawler.setSize(2, true);
        }
    }

    private static void buildIsland(ServerWorld world, BlockPos center, int radius) {
        BlockState stone = CelestialRealmMod.DARK_MATTER_STONE.getDefaultState();
        BlockState crystal = CelestialRealmMod.CRYSTALLIZED_NEBULA.getDefaultState();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist > radius) continue;
                int depth = Math.max(1, (int) Math.ceil((radius - dist) * 0.45));
                for (int dy = -depth; dy <= 0; dy++) {
                    world.setBlockState(center.add(dx, dy, dz), dy == 0 ? crystal : stone, 3);
                }
            }
        }
    }

    private static void buildTree(ServerWorld world, BlockPos base, int height) {
        BlockState wood = CelestialRealmMod.NEBULA_WOOD.getDefaultState();
        BlockState leaves = CelestialRealmMod.NEBULA_LEAVES.getDefaultState();

        for (int y = 0; y < height; y++) {
            world.setBlockState(base.up(y), wood, 3);
        }

        int top = base.getY() + height - 1;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = -2; dy <= 2; dy++) {
                    double d = (dx * dx * 0.85) + (dz * dz * 0.85) + (dy * dy * 1.15);
                    if (d <= 6.5) {
                        world.setBlockState(new BlockPos(base.getX() + dx, top + dy, base.getZ() + dz), leaves, 3);
                    }
                }
            }
        }
        world.setBlockState(new BlockPos(base.getX(), top + 2, base.getZ()), CelestialRealmMod.CRYSTALLIZED_NEBULA.getDefaultState(), 3);
    }

    private static void buildShrine(ServerWorld world, BlockPos center) {
        BlockState stone = CelestialRealmMod.DARK_MATTER_STONE.getDefaultState();
        BlockState crystal = CelestialRealmMod.CRYSTALLIZED_NEBULA.getDefaultState();

        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                if (Math.abs(dx) == 4 || Math.abs(dz) == 4 || (Math.abs(dx) <= 1 && Math.abs(dz) <= 1)) {
                    world.setBlockState(center.add(dx, 0, dz), stone, 3);
                }
            }
        }

        for (int dx : new int[]{-3, 3}) {
            for (int dz : new int[]{-3, 3}) {
                for (int y = 1; y <= 5; y++) {
                    world.setBlockState(center.add(dx, y, dz), stone, 3);
                }
                world.setBlockState(center.add(dx, 6, dz), crystal, 3);
            }
        }

        for (int y = 1; y <= 4; y++) {
            world.setBlockState(center.add(0, y, 0), crystal, 3);
        }
        world.setBlockState(center.add(0, 5, 0), CelestialRealmMod.NEBULA_LEAVES.getDefaultState(), 3);
    }
}
