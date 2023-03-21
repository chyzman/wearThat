package com.chyzman.wearthat.client;

import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.Vec3f;

import static net.minecraft.entity.LivingEntity.getPreferredEquipmentSlot;

public class ChestItemFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M>{

    private final HeldItemRenderer heldItemRenderer;

    public ChestItemFeatureRenderer(FeatureRendererContext<T, M> context, HeldItemRenderer heldItemRenderer) {
        super(context);
        this.heldItemRenderer = heldItemRenderer;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        ModelTransformation.Mode mode = ModelTransformation.Mode.FIXED;
        ItemStack chestStack = ((LivingEntity) entity).getEquippedStack(EquipmentSlot.CHEST);
        if (!chestStack.isEmpty()) {
            Item item = chestStack.getItem();
            if (!(getPreferredEquipmentSlot(chestStack).equals(EquipmentSlot.CHEST))) {
                matrices.push();
                ((PlayerEntityModel) this.getContextModel()).body.rotate(matrices);
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
                matrices.translate(0, -1 / 4f, 0);
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
                this.heldItemRenderer.renderItem((LivingEntity) entity, chestStack, mode, false, matrices, vertexConsumers, light);
                matrices.scale(1.01f, 1.01f, 1.01f);
                matrices.translate(0, -1 / 4f, 0);
                this.heldItemRenderer.renderItem((LivingEntity) entity, chestStack, mode, false, matrices, vertexConsumers, light);
                matrices.pop();
                matrices.push();
                ((PlayerEntityModel) this.getContextModel()).rightArm.rotate(matrices);
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
                matrices.scale(2/3f, 2/3f, 2/3f);
                matrices.translate(-1/12f, 0, 0);
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
                this.heldItemRenderer.renderItem((LivingEntity) entity, chestStack, mode, false, matrices, vertexConsumers, light);
                matrices.scale(0.99f, 0.99f, 0.99f);
                matrices.translate(0, -1/2f, 0);
                this.heldItemRenderer.renderItem((LivingEntity) entity, chestStack, mode, false, matrices, vertexConsumers, light);
                matrices.pop();
                matrices.push();
                ((PlayerEntityModel) this.getContextModel()).leftArm.rotate(matrices);
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
                matrices.scale(2/3f, 2/3f, 2/3f);
                matrices.translate(1/12f, 0, 0);
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
                this.heldItemRenderer.renderItem((LivingEntity) entity, chestStack, mode, false, matrices, vertexConsumers, light);
                matrices.scale(0.99f, 0.99f, 0.99f);
                matrices.translate(0, -1/2f, 0);
                this.heldItemRenderer.renderItem((LivingEntity) entity, chestStack, mode, false, matrices, vertexConsumers, light);
                matrices.pop();
            }
        }
        ItemStack legsStack = ((LivingEntity) entity).getEquippedStack(EquipmentSlot.LEGS);
        if (!legsStack.isEmpty()) {
            Item item = legsStack.getItem();
            if (!(getPreferredEquipmentSlot(legsStack).equals(EquipmentSlot.LEGS))) {
                matrices.push();
                ((PlayerEntityModel) this.getContextModel()).rightLeg.rotate(matrices);
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
                matrices.scale(2/3f, 2/3f, 2/3f);
                matrices.translate(0, -1/6f, 0);
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
                this.heldItemRenderer.renderItem((LivingEntity) entity, legsStack, mode, false, matrices, vertexConsumers, light);
                matrices.scale(1.01f, 1.01f, 1.01f);
                matrices.translate(0, -1/3f, 0);
                this.heldItemRenderer.renderItem((LivingEntity) entity, legsStack, mode, false, matrices, vertexConsumers, light);
                matrices.pop();
                matrices.push();
                ((PlayerEntityModel) this.getContextModel()).leftLeg.rotate(matrices);
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
                matrices.scale(2/3f, 2/3f, 2/3f);
                matrices.translate(0, -1/6f, 0);
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
                this.heldItemRenderer.renderItem((LivingEntity) entity, legsStack, mode, false, matrices, vertexConsumers, light);
                matrices.scale(1.01f, 1.01f, 1.01f);
                matrices.translate(0, -1/3f, 0);
                this.heldItemRenderer.renderItem((LivingEntity) entity, legsStack, mode, false, matrices, vertexConsumers, light);
                matrices.pop();
            }
        }
        ItemStack feetStack = ((LivingEntity) entity).getEquippedStack(EquipmentSlot.FEET);
        if (!feetStack.isEmpty()) {
            Item item = feetStack.getItem();
            if (!(getPreferredEquipmentSlot(feetStack).equals(EquipmentSlot.FEET))) {
                matrices.push();
                ((PlayerEntityModel) this.getContextModel()).rightLeg.rotate(matrices);
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
                matrices.scale(0.75f, 0.75f, 0.75f);
                matrices.translate(0, -0.8f, 0);
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
                this.heldItemRenderer.renderItem((LivingEntity) entity, feetStack, mode, false, matrices, vertexConsumers, light);
                matrices.pop();
                matrices.push();
                ((PlayerEntityModel) this.getContextModel()).leftLeg.rotate(matrices);
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
                matrices.scale(0.75f, 0.75f, 0.75f);
                matrices.translate(0, -0.8f, 0);
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
                this.heldItemRenderer.renderItem((LivingEntity) entity, feetStack, mode, false, matrices, vertexConsumers, light);
                matrices.pop();
            }
        }
    }
}