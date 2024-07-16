package com.chyzman.wearthat.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class ChestItemFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {

    private final HeldItemRenderer heldItemRenderer;

    public ChestItemFeatureRenderer(FeatureRendererContext<T, M> context, HeldItemRenderer heldItemRenderer) {
        super(context);
        this.heldItemRenderer = heldItemRenderer;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        ModelTransformationMode mode = ModelTransformationMode.FIXED;
        ItemStack chestStack = (entity).getEquippedStack(EquipmentSlot.CHEST);
        if (!chestStack.isEmpty()) {
            if (!(entity.getPreferredEquipmentSlot(chestStack).equals(EquipmentSlot.CHEST))) {
                matrices.push();
                ((PlayerEntityModel<?>) this.getContextModel()).body.rotate(matrices);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                matrices.translate(0, -1 / 4f, 0);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                this.heldItemRenderer.renderItem(entity, chestStack, mode, false, matrices, vertexConsumers, light);
                matrices.scale(1.01f, 1.01f, 1.01f);
                matrices.translate(0, -1 / 4f, 0);
                this.heldItemRenderer.renderItem(entity, chestStack, mode, false, matrices, vertexConsumers, light);
                matrices.pop();
                matrices.push();
                ((PlayerEntityModel<?>) this.getContextModel()).rightArm.rotate(matrices);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                matrices.scale(2 / 3f, 2 / 3f, 2 / 3f);
                matrices.translate(-1 / 12f, 0, 0);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                this.heldItemRenderer.renderItem(entity, chestStack, mode, false, matrices, vertexConsumers, light);
                matrices.scale(0.99f, 0.99f, 0.99f);
                matrices.translate(0, -1 / 2f, 0);
                this.heldItemRenderer.renderItem(entity, chestStack, mode, false, matrices, vertexConsumers, light);
                matrices.pop();
                matrices.push();
                ((PlayerEntityModel<?>) this.getContextModel()).leftArm.rotate(matrices);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                matrices.scale(2 / 3f, 2 / 3f, 2 / 3f);
                matrices.translate(1 / 12f, 0, 0);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                this.heldItemRenderer.renderItem(entity, chestStack, mode, false, matrices, vertexConsumers, light);
                matrices.scale(0.99f, 0.99f, 0.99f);
                matrices.translate(0, -1 / 2f, 0);
                this.heldItemRenderer.renderItem(entity, chestStack, mode, false, matrices, vertexConsumers, light);
                matrices.pop();
            }
        }
        ItemStack legsStack = (entity).getEquippedStack(EquipmentSlot.LEGS);
        if (!legsStack.isEmpty()) {
            if (!(entity.getPreferredEquipmentSlot(legsStack).equals(EquipmentSlot.LEGS))) {
                matrices.push();
                ((PlayerEntityModel<?>) this.getContextModel()).rightLeg.rotate(matrices);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                matrices.scale(2 / 3f, 2 / 3f, 2 / 3f);
                matrices.translate(0, -1 / 6f, 0);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                this.heldItemRenderer.renderItem(entity, legsStack, mode, false, matrices, vertexConsumers, light);
                matrices.scale(1.01f, 1.01f, 1.01f);
                matrices.translate(0, -1 / 3f, 0);
                this.heldItemRenderer.renderItem(entity, legsStack, mode, false, matrices, vertexConsumers, light);
                matrices.pop();
                matrices.push();
                ((PlayerEntityModel<?>) this.getContextModel()).leftLeg.rotate(matrices);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                matrices.scale(2 / 3f, 2 / 3f, 2 / 3f);
                matrices.translate(0, -1 / 6f, 0);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                this.heldItemRenderer.renderItem(entity, legsStack, mode, false, matrices, vertexConsumers, light);
                matrices.scale(1.01f, 1.01f, 1.01f);
                matrices.translate(0, -1 / 3f, 0);
                this.heldItemRenderer.renderItem(entity, legsStack, mode, false, matrices, vertexConsumers, light);
                matrices.pop();
            }
        }
        ItemStack feetStack = (entity).getEquippedStack(EquipmentSlot.FEET);
        if (!feetStack.isEmpty()) {
            if (!(entity.getPreferredEquipmentSlot(feetStack).equals(EquipmentSlot.FEET))) {
                matrices.push();
                ((PlayerEntityModel<?>) this.getContextModel()).rightLeg.rotate(matrices);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                matrices.scale(0.75f, 0.75f, 0.75f);
                matrices.translate(0, -0.8f, 0);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                this.heldItemRenderer.renderItem(entity, feetStack, mode, false, matrices, vertexConsumers, light);
                matrices.pop();
                matrices.push();
                ((PlayerEntityModel<?>) this.getContextModel()).leftLeg.rotate(matrices);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                matrices.scale(0.75f, 0.75f, 0.75f);
                matrices.translate(0, -0.8f, 0);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                this.heldItemRenderer.renderItem(entity, feetStack, mode, false, matrices, vertexConsumers, light);
                matrices.pop();
            }
        }
    }
}
