package com.fuat.celestialrealm.client;

import com.fuat.celestialrealm.CelestialRealmMod;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.entity.EntityRenderers;

public final class CelestialRealmClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRenderers.register(CelestialRealmMod.NEBULA_WISP, NebulaWispRenderer::new);
        EntityRenderers.register(CelestialRealmMod.VOID_CRAWLER, VoidCrawlerRenderer::new);
    }
}
