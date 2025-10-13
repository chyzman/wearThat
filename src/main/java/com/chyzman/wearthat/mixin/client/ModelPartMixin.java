package com.chyzman.wearthat.mixin.client;

import com.chyzman.wearthat.pond.ModelPartDuck;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(ModelPart.class)
public abstract class ModelPartMixin {

    @WrapWithCondition(
        method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/ModelPart;applyTransform(Lnet/minecraft/client/util/math/MatrixStack;)V"
        )
    )
    private boolean modifySkin(
        ModelPart instance, MatrixStack matrices
    ) {
        return !((ModelPartDuck) (Object) instance).wearThat$isTransformingForbidden();
    }
}
