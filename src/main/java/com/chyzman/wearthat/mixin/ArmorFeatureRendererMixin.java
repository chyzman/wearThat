package com.chyzman.wearthat.mixin;

import com.chyzman.wearthat.WearThat;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends BipedEntityRenderState, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {

    @Shadow
    public static boolean hasModel(ItemStack stack, EquipmentSlot slot) {
        return false;
    }

    public ArmorFeatureRendererMixin(FeatureRendererContext<T, M> context) {
        super(context);
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
        if (!hasModel(stack, slot)) {
            var itemRenderer = MinecraftClient.getInstance().getItemRenderer();
            var mode = ModelTransformationMode.FIXED;
            var model = itemRenderer.getModel(stack, null, null, 0);
            var contextModel = this.getContextModel();
            matrices.push();
            contextModel.getRootPart().rotate(matrices);
            switch (slot) {
                case CHEST -> {
                    //torso
                    if (WearThat.renderingArm == null) {
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
                    if (!Arm.LEFT.equals(WearThat.renderingArm)) {
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
                    if (!Arm.RIGHT.equals(WearThat.renderingArm)) {
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
        ArmorFeatureRenderer<?, ?, ?> instance,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        ItemStack stack,
        EquipmentSlot slot,
        int light,
        A armorModel,
        Operation<Void> original
    ) {
        if (WearThat.renderingArm == null || slot == EquipmentSlot.CHEST)
            original.call(instance, matrices, vertexConsumers, stack, slot, light, armorModel);
    }

    @Inject(
        method = "setVisible",
        at = @At(
            value = "RETURN"
        )
    )
    private void onlyRenderChestInFirstPerson(
        A bipedModel,
        EquipmentSlot slot,
        CallbackInfo ci
    ) {
        if (WearThat.renderingArm == null || slot != EquipmentSlot.CHEST) return;
        bipedModel.body.visible = false;
        bipedModel.rightArm.visible = WearThat.renderingArm == net.minecraft.util.Arm.RIGHT;
        bipedModel.leftArm.visible = WearThat.renderingArm == net.minecraft.util.Arm.LEFT;
    }
}
