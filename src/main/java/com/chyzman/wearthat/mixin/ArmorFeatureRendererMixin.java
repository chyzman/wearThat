package com.chyzman.wearthat.mixin;

import com.chyzman.wearthat.ThatUtil;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.chyzman.wearthat.ThatUtil.applyFunny;
import static net.minecraft.entity.EquipmentSlot.*;

@Debug(export = true)
@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<S extends BipedEntityRenderState, M extends BipedEntityModel<S>, A extends BipedEntityModel<S>> extends FeatureRenderer<S, M> {

    @Shadow protected abstract A getModel(S state, EquipmentSlot slot);

    @Shadow @Final private A innerModel;
    @Shadow @Final private A outerModel;
    @Unique
    @Nullable
    private S renderState;

    public ArmorFeatureRendererMixin(FeatureRendererContext<S, M> context) {
        super(context);
    }

    @WrapMethod(
        method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/BipedEntityRenderState;FF)V"
    )
    private void captureRenderState(
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        S state,
        float limbAngle,
        float limbDistance,
        Operation<S> original
    ) {
        this.renderState = state;
        original.call(matrices, vertexConsumers, light, state, limbAngle, limbDistance);
        this.renderState = null;
    }

    @WrapOperation(
        method = "renderArmor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;get(Lnet/minecraft/component/ComponentType;)Ljava/lang/Object;",
            ordinal = 0
        )
    )
    private Object renderWearThatedItems(
        ItemStack stack,
        ComponentType<?> componentType,
        Operation<Object> original,
        @Local(argsOnly = true) MatrixStack matrices,
        @Local(argsOnly = true) VertexConsumerProvider vertexConsumers,
        @Local(argsOnly = true) EquipmentSlot slot,
        @Local(argsOnly = true) int light
    ) {
        var returned = original.call(stack, componentType);
        if (returned == null) {
            var itemRenderer = MinecraftClient.getInstance().getItemRenderer();
            var mode = ModelTransformationMode.FIXED;
            var model = itemRenderer.getModel(stack, null, null, 0);
            var contextModel = this.getContextModel();
            matrices.push();
            contextModel.getRootPart().rotate(matrices);
            //@formatter:off
            switch (slot) {
                case CHEST -> {
                    //torso
                    if (ThatUtil.renderingArm == null) {
                        matrices.push();
                        contextModel.body.rotate(matrices);
                        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                        matrices.translate(0, -1 / 4f, 0);
                        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                        itemRenderer.renderItem(stack, mode, true, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, model);
                        matrices.scale(1.01f, 1.01f, 1.01f);
                        matrices.translate(0, -1 / 4f, 0);
                        itemRenderer.renderItem(stack, mode, true, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, model);
                        matrices.pop();
                    }

                    //right arm
                    if (!Arm.LEFT.equals(ThatUtil.renderingArm)) {
                        matrices.push();
                        contextModel.rightArm.rotate(matrices);
                        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                        matrices.scale(2 / 3f, 2 / 3f, 2 / 3f);
                        matrices.translate(-1 / 12f, 0, 0);
                        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                        itemRenderer.renderItem(stack, mode, true, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, model);
                        matrices.scale(0.99f, 0.99f, 0.99f);
                        matrices.translate(0, -1 / 2f, 0);
                        itemRenderer.renderItem(stack, mode, true, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, model);
                        matrices.pop();
                    }

                    //left arm
                    if (!Arm.RIGHT.equals(ThatUtil.renderingArm)) {
                        matrices.push();
                        contextModel.leftArm.rotate(matrices);
                        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                        matrices.scale(2 / 3f, 2 / 3f, 2 / 3f);
                        matrices.translate(1 / 12f, 0, 0);
                        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                        itemRenderer.renderItem(stack, mode, true, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, model);
                        matrices.scale(0.99f, 0.99f, 0.99f);
                        matrices.translate(0, -1 / 2f, 0);
                        itemRenderer.renderItem(stack, mode, true, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, model);
                        matrices.pop();
                    }
                }

                case LEGS -> {
                    //right leg
                    matrices.push();
                    contextModel.rightLeg.rotate(matrices);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                    matrices.scale(2 / 3f, 2 / 3f, 2 / 3f);
                    matrices.translate(0, -1 / 6f, 0);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                    itemRenderer.renderItem(stack, mode, true, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, model);
                    matrices.scale(1.01f, 1.01f, 1.01f);
                    matrices.translate(0, -1 / 3f, 0);
                    itemRenderer.renderItem(stack, mode, true, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, model);
                    matrices.pop();

                    //left leg
                    matrices.push();
                    contextModel.leftLeg.rotate(matrices);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                    matrices.scale(2 / 3f, 2 / 3f, 2 / 3f);
                    matrices.translate(0, -1 / 6f, 0);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                    itemRenderer.renderItem(stack, mode, true, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, model);
                    matrices.scale(1.01f, 1.01f, 1.01f);
                    matrices.translate(0, -1 / 3f, 0);
                    itemRenderer.renderItem(stack, mode, true, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, model);
                    matrices.pop();
                }
                case FEET -> {
                    //right foot
                    matrices.push();
                    contextModel.rightLeg.rotate(matrices);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                    matrices.scale(0.75f, 0.75f, 0.75f);
                    matrices.translate(0, -0.8f, 0);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                    itemRenderer.renderItem(stack, mode, true, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, model);
                    matrices.pop();

                    //left foot
                    matrices.push();
                    contextModel.leftLeg.rotate(matrices);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                    matrices.scale(0.75f, 0.75f, 0.75f);
                    matrices.translate(0, -0.8f, 0);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                    itemRenderer.renderItem(stack, mode, true, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, model);
                    matrices.pop();
                }
            }
            //@formatter:on
            matrices.pop();
        }
        return returned;
    }

    @WrapOperation(
        method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/BipedEntityRenderState;FF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;renderArmor(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EquipmentSlot;ILnet/minecraft/client/render/entity/model/BipedEntityModel;)V"
        )
    )
    private void onlyRenderChestInFirstPerson(
        ArmorFeatureRenderer<S, M, A> instance,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        ItemStack stack,
        EquipmentSlot slot,
        int light,
        A armorModel,
        Operation<Void> original
    ) {
        if (ThatUtil.renderingArm == null || slot == CHEST)
            original.call(instance, matrices, vertexConsumers, stack, slot, light, armorModel);
    }

    @Inject(
        method = "setVisible",
        at = @At("RETURN")
    )
    private void onlyRenderArmInFirstPerson(A bipedModel, EquipmentSlot slot, CallbackInfo ci) {
        if (ThatUtil.renderingArm == null) return;
        bipedModel.setVisible(false);
        if (ThatUtil.renderingArm == net.minecraft.util.Arm.RIGHT) {
            if (slot == CHEST) bipedModel.rightArm.visible = true;
            if (slot == LEGS || slot == FEET) bipedModel.rightLeg.visible = true;
        } else {
            if (slot == CHEST)bipedModel.leftArm.visible = true;
            if (slot == LEGS || slot == FEET) bipedModel.leftLeg.visible = true;
        }
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
        return slot;
    }

    @SuppressWarnings("unchecked")
    @ModifyExpressionValue(
        method = "renderArmor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;hasModel(Lnet/minecraft/component/type/EquippableComponent;Lnet/minecraft/entity/EquipmentSlot;)Z"
        )
    )
    private boolean modifySlot(
        boolean original,
        @Local() EquippableComponent component,
        @Local(argsOnly = true) LocalRef<EquipmentSlot> slotRef,
        @Local(argsOnly = true) LocalRef<BipedEntityModel<BipedEntityRenderState>> armorModelRef,
        @Share("originalSlot") LocalRef<@Nullable EquipmentSlot> originalSlot
    ) {
        var slot = slotRef.get();
        originalSlot.set(slot != component.slot() ? slot : null);
        slotRef.set(component.slot());
        if (this.renderState != null) armorModelRef.set((BipedEntityModel<BipedEntityRenderState>) getModel(this.renderState, component.slot()));
        return true;
    }

    @WrapOperation(
        method = "renderArmor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;copyTransforms(Lnet/minecraft/client/render/entity/model/BipedEntityModel;)V"
        )
    )
    private void applyFunnyTransforms(
        M contextModel,
        M armorModel,
        Operation<Void> original,
        @Local(argsOnly = true) EquipmentSlot slot,
        @Share("originalSlot") LocalRef<@Nullable EquipmentSlot> originalSlotRef
    ) {
        armorModel.getRootPart().resetTransform();
        armorModel.getParts().forEach(part -> part.hidden = false);
        original.call(contextModel, armorModel);
        var originalSlot = originalSlotRef.get();
        if (originalSlot == null) return;
        armorModel.resetTransforms();
        applyFunny(contextModel, armorModel, slot, originalSlot);
        originalSlotRef.set(null);
    }
}
