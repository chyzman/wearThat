package com.chyzman.wearthat.mixin;

import com.chyzman.wearthat.pond.ModelPartDuck;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ModelPart.class)
public abstract class ModelPartMixin implements ModelPartDuck {
    @Unique
    private Consumer<MatrixStack> transform = null;

    @Override
    public void wearThat$setTransform(Consumer<MatrixStack> consumer) {
        this.transform = consumer;
    }

    @Inject(
        method = "applyTransform",
        at = @At("RETURN")
    )
    private void applyTransform(MatrixStack matrices, CallbackInfo ci) {
        if (this.transform != null) {
            matrices.scale(1/16f, 1/16f, 1/16f);
            this.transform.accept(matrices);
            matrices.scale(16, 16, 16);
        }
    }
}
