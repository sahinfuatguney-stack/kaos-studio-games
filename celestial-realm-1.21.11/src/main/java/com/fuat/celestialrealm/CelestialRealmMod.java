package com.fuat.celestialrealm;

import com.fuat.celestialrealm.world.RealmBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public final class CelestialRealmMod implements ModInitializer {
    public static final String MOD_ID = "celestial_realm";

    public static final Identifier NEBULA_ID = Identifier.of(MOD_ID, "crystallized_nebula_block");
    public static final Identifier MATTER_ID = Identifier.of(MOD_ID, "dark_matter_stone");
    public static final Identifier WOOD_ID = Identifier.of(MOD_ID, "nebula_wood");
    public static final Identifier LEAVES_ID = Identifier.of(MOD_ID, "nebula_leaves");

    public static final RegistryKey<Block> NEBULA_BLOCK_KEY = RegistryKey.of(RegistryKeys.BLOCK, NEBULA_ID);
    public static final RegistryKey<Block> MATTER_BLOCK_KEY = RegistryKey.of(RegistryKeys.BLOCK, MATTER_ID);
    public static final RegistryKey<Block> WOOD_BLOCK_KEY = RegistryKey.of(RegistryKeys.BLOCK, WOOD_ID);
    public static final RegistryKey<Block> LEAVES_BLOCK_KEY = RegistryKey.of(RegistryKeys.BLOCK, LEAVES_ID);

    public static final RegistryKey<Item> NEBULA_ITEM_KEY = RegistryKey.of(RegistryKeys.ITEM, NEBULA_ID);
    public static final RegistryKey<Item> MATTER_ITEM_KEY = RegistryKey.of(RegistryKeys.ITEM, MATTER_ID);
    public static final RegistryKey<Item> WOOD_ITEM_KEY = RegistryKey.of(RegistryKeys.ITEM, WOOD_ID);
    public static final RegistryKey<Item> LEAVES_ITEM_KEY = RegistryKey.of(RegistryKeys.ITEM, LEAVES_ID);

    public static final Block CRYSTALLIZED_NEBULA = Registry.register(
            Registries.BLOCK, NEBULA_ID,
            new Block(AbstractBlock.Settings.create().strength(3.5F, 6.0F).requiresTool().registryKey(NEBULA_BLOCK_KEY))
    );
    public static final Block DARK_MATTER_STONE = Registry.register(
            Registries.BLOCK, MATTER_ID,
            new Block(AbstractBlock.Settings.create().strength(5.0F, 9.0F).requiresTool().registryKey(MATTER_BLOCK_KEY))
    );
    public static final Block NEBULA_WOOD = Registry.register(
            Registries.BLOCK, WOOD_ID,
            new Block(AbstractBlock.Settings.create().strength(2.5F).registryKey(WOOD_BLOCK_KEY))
    );
    public static final Block NEBULA_LEAVES = Registry.register(
            Registries.BLOCK, LEAVES_ID,
            new Block(AbstractBlock.Settings.create().strength(0.25F).nonOpaque().registryKey(LEAVES_BLOCK_KEY))
    );

    static {
        Registry.register(Registries.ITEM, NEBULA_ID, new BlockItem(CRYSTALLIZED_NEBULA, new Item.Settings().registryKey(NEBULA_ITEM_KEY)));
        Registry.register(Registries.ITEM, MATTER_ID, new BlockItem(DARK_MATTER_STONE, new Item.Settings().registryKey(MATTER_ITEM_KEY)));
        Registry.register(Registries.ITEM, WOOD_ID, new BlockItem(NEBULA_WOOD, new Item.Settings().registryKey(WOOD_ITEM_KEY)));
        Registry.register(Registries.ITEM, LEAVES_ID, new BlockItem(NEBULA_LEAVES, new Item.Settings().registryKey(LEAVES_ITEM_KEY)));
    }

    public static final RegistryKey<World> CELESTIAL_WORLD = RegistryKey.of(
            RegistryKeys.WORLD, Identifier.of(MOD_ID, "celestial_realm")
    );

    public static final RegistryKey<net.minecraft.world.biome.Biome> CELESTIAL_BIOME_KEY = RegistryKey.of(
            RegistryKeys.BIOME, Identifier.of(MOD_ID, "celestial_biome")
    );

    public static final RegistryKey<EntityType<SlimeEntity>> NEBULA_WISP_KEY = RegistryKey.of(
            RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "nebula_wisp")
    );
    public static final RegistryKey<EntityType<MagmaCubeEntity>> VOID_CRAWLER_KEY = RegistryKey.of(
            RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "void_crawler")
    );

    public static final EntityType<SlimeEntity> NEBULA_WISP = Registry.register(
            Registries.ENTITY_TYPE,
            NEBULA_WISP_KEY.getValue(),
            EntityType.Builder.create(SlimeEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.9F, 0.9F)
                    .maxTrackingRange(8)
                    .build(NEBULA_WISP_KEY)
    );

    public static final EntityType<MagmaCubeEntity> VOID_CRAWLER = Registry.register(
            Registries.ENTITY_TYPE,
            VOID_CRAWLER_KEY.getValue(),
            EntityType.Builder.create(MagmaCubeEntity::new, SpawnGroup.MONSTER)
                    .dimensions(1.0F, 1.0F)
                    .maxTrackingRange(8)
                    .build(VOID_CRAWLER_KEY)
    );

    @Override
    public void onInitialize() {
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(CELESTIAL_BIOME_KEY), SpawnGroup.MONSTER, NEBULA_WISP, 45, 1, 2);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(CELESTIAL_BIOME_KEY), SpawnGroup.MONSTER, VOID_CRAWLER, 25, 1, 1);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("celestial").executes(context -> {
                ServerWorld world = context.getSource().getServer().getWorld(CELESTIAL_WORLD);
                var player = context.getSource().getPlayer();
                if (world == null || player == null) return 0;
                RealmBuilder.buildStarterRealm(world);
                player.teleport(world, 0.5, 30.0, 0.5, java.util.Set.of(), player.getYaw(), player.getPitch(), true);
                return 1;
            }));
        });

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.getRegistryKey().equals(CELESTIAL_WORLD)) {
                RealmBuilder.applyLowGravity(world);
            }
        });
    }
}
