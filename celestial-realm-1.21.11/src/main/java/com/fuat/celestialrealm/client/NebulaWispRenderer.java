package com.fuat.celestialrealm.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.SlimeEntityRenderer;
import net.minecraft.client.render.entity.state.SlimeEntityRenderState;
import net.minecraft.util.Identifier;

public final class NebulaWispRenderer extends SlimeEntityRenderer {
    private static final Identifier TEXTURE = Identifier.of("minecraft", "textures/entity/slime/slime.png");

    public NebulaWispRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(SlimeEntityRenderState state) {
        return TEXTURE;
    }
}
