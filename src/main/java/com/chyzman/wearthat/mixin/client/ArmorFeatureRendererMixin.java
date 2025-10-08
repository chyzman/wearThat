package com.chyzman.wearthat.mixin.client;

import com.chyzman.wearthat.ThatUtil;
import com.chyzman.wearthat.pond.LivingEntityRenderStateDuck;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<S extends BipedEntityRenderState, M extends BipedEntityModel<S>, A extends BipedEntityModel<S>> extends FeatureRenderer<S, M> {

    @Shadow
    protected abstract A getModel(S state, EquipmentSlot slot);

    public ArmorFeatureRendererMixin(FeatureRendererContext<S, M> context) {
        super(context);
    }

    @ModifyExpressionValue(
        method = "hasModel(Lnet/minecraft/component/type/EquippableComponent;Lnet/minecraft/entity/EquipmentSlot;)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/component/type/EquippableComponent;slot()Lnet/minecraft/entity/EquipmentSlot;"
        )
    )
    private static EquipmentSlot allArmorHasModels(
        EquipmentSlot original,
        @Local(argsOnly = true) EquipmentSlot slot
    ) {
        if (!original.getType().equals(EquipmentSlot.Type.HUMANOID_ARMOR)) return original;
        return slot;
    }

    @Inject(
        method = "renderArmor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;hasModel(Lnet/minecraft/component/type/EquippableComponent;Lnet/minecraft/entity/EquipmentSlot;)Z"
        )
    )
    private void modifySlot(
        CallbackInfo ci,
        @Local() EquippableComponent component,
        @Local(argsOnly = true) LocalRef<EquipmentSlot> slotRef,
        @Share("originalSlot") LocalRef<@Nullable EquipmentSlot> originalSlot
    ) {
        var slot = slotRef.get();
        originalSlot.set(slot != component.slot() ? slot : null);
        slotRef.set(component.slot());
    }

    @SuppressWarnings("unchecked")
    @ModifyExpressionValue(
        method = "renderArmor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;get(Lnet/minecraft/component/ComponentType;)Ljava/lang/Object;"
        )
    )
    private Object renderWearThatItems(
        Object original,
        @Cancellable CallbackInfo ci,
        @Local(argsOnly = true) MatrixStack matrices,
        @Local(argsOnly = true) OrderedRenderCommandQueue queue,
        @Local(argsOnly = true) EquipmentSlot slot,
        @Local(argsOnly = true) int light,
        @Local(argsOnly = true) BipedEntityRenderState renderState
    ) {
        if (original instanceof EquippableComponent component && component.assetId().isPresent()) return original;
        var armorModel = (BipedEntityModel<BipedEntityRenderState>) getModel((S) renderState, slot);
        var contextModel = (BipedEntityModel<BipedEntityRenderState>) this.getContextModel();
        var outlineColor = renderState.outlineColor;
        ci.cancel();

        switch (slot) {
            case CHEST -> {
                var state = ((LivingEntityRenderStateDuck) renderState).wearThat$getChestItemRenderState();
                //torso
                if (armorModel.body.visible) {
                    matrices.push();
                    contextModel.body.applyTransform(matrices);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                    matrices.translate(0, -1 / 4f, 0);
                    matrices.scale(1.001f, 1.001f, 1.001f);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                    state.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, outlineColor);
                    matrices.translate(0, -1 / 4f, 0);
                    matrices.scale(1.001f, 1.001f, 1.001f);
                    state.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, outlineColor);
                    matrices.pop();
                }

                //right arm
                if (armorModel.rightArm.visible) {
                    matrices.push();
                    contextModel.rightArm.applyTransform(matrices);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                    matrices.scale(2 / 3f, 2 / 3f, 2 / 3f);
                    matrices.translate(-1 / 12f, 0, 0);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                    state.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, outlineColor);
                    matrices.scale(0.999f, 0.999f, 0.999f);
                    matrices.translate(0, -1 / 2f, 0);
                    state.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, outlineColor);
                    matrices.pop();
                }

                //left arm
                if (armorModel.leftArm.visible) {
                    matrices.push();
                    contextModel.leftArm.applyTransform(matrices);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                    matrices.scale(2 / 3f, 2 / 3f, 2 / 3f);
                    matrices.translate(1 / 12f, 0, 0);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                    state.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, outlineColor);
                    matrices.scale(0.999f, 0.999f, 0.999f);
                    matrices.translate(0, -1 / 2f, 0);
                    state.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, outlineColor);
                    matrices.pop();
                }
            }

            case LEGS -> {
                var state = ((LivingEntityRenderStateDuck) renderState).wearThat$getLegsItemRenderState();
                //right leg
                if (armorModel.rightLeg.visible) {
                    matrices.push();
                    contextModel.rightLeg.applyTransform(matrices);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                    matrices.scale(2 / 3f, 2 / 3f, 2 / 3f);
                    matrices.translate(0, -1 / 6f, 0);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                    state.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, outlineColor);
                    matrices.scale(1.001f, 1.001f, 1.001f);
                    matrices.translate(0, -1 / 3f, 0);
                    state.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, outlineColor);
                    matrices.pop();
                }

                //left leg
                if (armorModel.leftLeg.visible) {
                    matrices.push();
                    contextModel.leftLeg.applyTransform(matrices);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                    matrices.scale(2 / 3f, 2 / 3f, 2 / 3f);
                    matrices.translate(0, -1 / 6f, 0);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                    state.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, outlineColor);
                    matrices.scale(1.001f, 1.001f, 1.001f);
                    matrices.translate(0, -1 / 3f, 0);
                    state.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, outlineColor);
                    matrices.pop();
                }
            }
            case FEET -> {
                var state = ((LivingEntityRenderStateDuck) renderState).wearThat$getFeetItemRenderState();
                //right foot
                if (armorModel.leftLeg.visible) {
                    matrices.push();
                    contextModel.rightLeg.applyTransform(matrices);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                    matrices.scale(0.75f, 0.75f, 0.75f);
                    matrices.translate(0, -0.8f, 0);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                    state.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, outlineColor);
                    matrices.pop();
                }

                //left foot
                if (armorModel.rightLeg.visible) {
                    matrices.push();
                    contextModel.leftLeg.applyTransform(matrices);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                    matrices.scale(0.75f, 0.75f, 0.75f);
                    matrices.translate(0, -0.8f, 0);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                    state.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, outlineColor);
                    matrices.pop();
                }
            }
        }
        return armorModel;
    }

    @SuppressWarnings("unchecked")
    @WrapOperation(
        method = "renderArmor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/equipment/EquipmentRenderer;render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;II)V"
        )
    )
    private void setFlags(
        EquipmentRenderer instance,
        EquipmentModel.LayerType layerType,
        RegistryKey<EquipmentAsset> assetKey,
        Model<?> model,
        Object object,
        ItemStack itemStack,
        MatrixStack matrixStack,
        OrderedRenderCommandQueue orderedRenderCommandQueue,
        int light,
        int outlineColor,
        Operation<Void> original,
        @Share("originalSlot") LocalRef<@Nullable EquipmentSlot> originalSlot
    ) {
        ThatUtil.currentSlot = originalSlot.get();
        ThatUtil.currentBipedModel = (BipedEntityModel<BipedEntityRenderState>) this.getContextModel();
        model.resetTransforms();
        original.call(instance, layerType, assetKey, model, object, itemStack, matrixStack, orderedRenderCommandQueue, light, outlineColor);
        ThatUtil.currentSlot = null;
        ThatUtil.currentBipedModel = null;
    }
}
