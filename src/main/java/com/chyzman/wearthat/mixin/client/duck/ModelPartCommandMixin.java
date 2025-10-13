package com.chyzman.wearthat.mixin.client.duck;

import com.chyzman.wearthat.pond.ModelPartDuck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin({ModelPart.class, OrderedRenderCommandQueueImpl.ModelPartCommand.class})
public abstract class ModelPartCommandMixin implements ModelPartDuck {

    @Unique
    private boolean transformingForbidden = false;

    @Override
    public void wearThat$setTransformingForbidden(boolean forbidden) {
        this.transformingForbidden = forbidden;
    }

    @Override
    public boolean wearThat$isTransformingForbidden() {
        return this.transformingForbidden;
    }
}
