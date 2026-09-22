package com.fuat.celestialrealm.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MagmaCubeEntityRenderer;
import net.minecraft.client.render.entity.state.SlimeEntityRenderState;
import net.minecraft.util.Identifier;

public final class VoidCrawlerRenderer extends MagmaCubeEntityRenderer {
    private static final Identifier TEXTURE = Identifier.of("minecraft", "textures/entity/slime/magmacube.png");

    public VoidCrawlerRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(SlimeEntityRenderState state) {
        return TEXTURE;
    }
}
