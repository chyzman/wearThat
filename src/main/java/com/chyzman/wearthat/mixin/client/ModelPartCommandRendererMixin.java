package com.chyzman.wearthat.mixin.client;

import com.chyzman.wearthat.ThatUtil;
import com.chyzman.wearthat.pond.ModelPartDuck;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.ModelPartCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(ModelPartCommandRenderer.class)
public abstract class ModelPartCommandRendererMixin {

    @WrapOperation(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"
        )
    )
    private void modifySkin(
        ModelPart instance,
        MatrixStack matrices,
        VertexConsumer vertices,
        int light,
        int overlay,
        int color,
        Operation<Void> original,
        @Local() OrderedRenderCommandQueueImpl.ModelPartCommand command
    ) {
        if (((ModelPartDuck) (Object) command).wearThat$isTransformingForbidden()) ((ModelPartDuck) (Object) instance).wearThat$setTransformingForbidden(true);
        original.call(instance, matrices, vertices, light, overlay, color);
        ((ModelPartDuck) (Object) instance).wearThat$setTransformingForbidden(false);
    }
}
