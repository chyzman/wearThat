package com.chyzman.wearthat.mixin;

import com.chyzman.wearthat.pond.ModelPartDuck;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

@Mixin(ModelPart.class)
public abstract class ModelPartMixin implements ModelPartDuck {
    @Unique
    public Consumer<MatrixStack> transform = null;

    @Override
    public void wearThat$setTransform(Consumer<MatrixStack> consumer) {
        this.transform = consumer;
    }

    @WrapOperation(
        method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/ModelPart;rotate(Lnet/minecraft/client/util/math/MatrixStack;)V"
        )
    )
    private void applyTransform(
        ModelPart instance,
        MatrixStack matrices,
        Operation<Void> original
    ) {
        original.call(instance, matrices);
        if (this.transform != null) {
            matrices.scale(1/16f, 1/16f, 1/16f);
            this.transform.accept(matrices);
            matrices.scale(16, 16, 16);
        }
        this.transform = null;
    }
}
