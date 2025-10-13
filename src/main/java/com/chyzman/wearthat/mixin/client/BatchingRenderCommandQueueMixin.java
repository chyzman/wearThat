package com.chyzman.wearthat.mixin.client;

import com.chyzman.wearthat.ThatUtil;
import com.chyzman.wearthat.pond.ModelPartDuck;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.BatchingRenderCommandQueue;
import net.minecraft.client.render.command.ModelPartCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(BatchingRenderCommandQueue.class)
public abstract class BatchingRenderCommandQueueMixin {

    @WrapOperation(
        method = "submitModelPart",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/command/ModelPartCommandRenderer$Commands;add(Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/command/OrderedRenderCommandQueueImpl$ModelPartCommand;)V"
        )
    )
    private void modifySkin(
        ModelPartCommandRenderer.Commands instance,
        RenderLayer renderLayer,
        OrderedRenderCommandQueueImpl.ModelPartCommand command,
        Operation<Void> original
    ) {
        if (ThatUtil.forbidTransforming) ((ModelPartDuck) (Object) command).wearThat$setTransformingForbidden(true);
        original.call(instance, renderLayer, command);
    }
}
