package com.chyzman.wearthat.mixin.client.duck;

import com.chyzman.wearthat.pond.SkinTexturesDuck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.player.SkinTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(SkinTextures.class)
public abstract class SkinTexturesMixin implements SkinTexturesDuck {

    @Unique
    private boolean overridden = false;

    @Override
    public void wearThat$setOverridden(boolean overridden) {
        this.overridden = overridden;
    }

    @Override
    public boolean wearThat$isOverridden() {
        return this.overridden;
    }
}
