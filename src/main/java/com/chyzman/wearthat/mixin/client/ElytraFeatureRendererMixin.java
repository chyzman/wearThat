package com.chyzman.wearthat.mixin.client;

import com.chyzman.wearthat.ThatUtil;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(ElytraFeatureRenderer.class)
public abstract class ElytraFeatureRendererMixin<S extends BipedEntityRenderState, M extends EntityModel<S>> {

    @WrapMethod(
        method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/BipedEntityRenderState;FF)V"
    )
    private void renderAllElytras(
        MatrixStack matrixStack,
        OrderedRenderCommandQueue orderedRenderCommandQueue,
        int i,
        S bipedEntityRenderState,
        float f,
        float g,
        Operation<Void> original,
        @Share("renderSlot") LocalRef<EquipmentSlot> renderSlot
    ) {
        if (ThatUtil.currentBipedModel != null) {
            renderSlot.set(EquipmentSlot.HEAD);
            original.call(matrixStack, orderedRenderCommandQueue, i, bipedEntityRenderState, f, g);
            renderSlot.set(EquipmentSlot.LEGS);
            original.call(matrixStack, orderedRenderCommandQueue, i, bipedEntityRenderState, f, g);
            renderSlot.set(EquipmentSlot.FEET);
            original.call(matrixStack, orderedRenderCommandQueue, i, bipedEntityRenderState, f, g);
            renderSlot.set(null);
        }
        original.call(matrixStack, orderedRenderCommandQueue, i, bipedEntityRenderState, f, g);
    }

    @ModifyExpressionValue(
        method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/BipedEntityRenderState;FF)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/render/entity/state/BipedEntityRenderState;equippedChestStack:Lnet/minecraft/item/ItemStack;"
        )
    )
    private ItemStack getCorrectElytra(
        ItemStack original,
        @Local(argsOnly = true) BipedEntityRenderState renderState,
        @Share("renderSlot") LocalRef<EquipmentSlot> renderSlot
    ) {
        if (ThatUtil.currentBipedModel == null || renderSlot.get() == null) return original;
        return switch (renderSlot.get()) {
            case HEAD -> renderState.equippedHeadStack;
            case LEGS -> renderState.equippedLegsStack;
            case FEET -> renderState.equippedFeetStack;
            default -> original;
        };
    }

    @WrapOperation(
        method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/BipedEntityRenderState;FF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/equipment/EquipmentRenderer;render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/util/Identifier;II)V"
        )
    )
    private void renderWearThatElytra(
        EquipmentRenderer instance,
        EquipmentModel.LayerType layerType,
        RegistryKey<EquipmentAsset> assetKey,
        Model<? super S> model,
        Object object,
        ItemStack itemStack,
        MatrixStack matrices,
        OrderedRenderCommandQueue queue,
        int light,
        @Nullable Identifier sprite,
        int outlineColor,
        int i,
        Operation<Void> original,
        @Local(argsOnly = true) BipedEntityRenderState renderState,
        @Share("renderSlot") LocalRef<EquipmentSlot> renderSlot
    ) {
        ThatUtil.currentSlot = renderSlot.get();
        original.call(instance, layerType, assetKey, model, object, itemStack, matrices, queue, light, sprite, outlineColor, i);
        ThatUtil.currentSlot = null;
    }

}
