package com.chyzman.wearthat.mixin.client;

import com.chyzman.wearthat.ThatUtil;
import com.chyzman.wearthat.pond.LivingEntityRenderStateDuck;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.RenderCommandQueue;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(EquipmentRenderer.class)
public abstract class EquipmentRendererMixin {

    @WrapOperation(
        method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/util/Identifier;II)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/command/RenderCommandQueue;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IIILnet/minecraft/client/texture/Sprite;ILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V"
        )
    )
    private <S> void renderWearThatArmor(
        RenderCommandQueue instance,
        Model<? super S> model,
        S object,
        MatrixStack matrices,
        RenderLayer renderLayer,
        int light,
        int overlay,
        int tintColor,
        @Nullable Sprite sprite,
        int outlineColor,
        @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand,
        Operation<Void> original,
        @Local(argsOnly = true) ItemStack itemStack,
        @Local(ordinal = 4) LocalIntRef batchIndexRef,
        @Local(argsOnly = true) OrderedRenderCommandQueue orderedRenderCommandQueue
    ) {
        var equippableComponent = itemStack.get(DataComponentTypes.EQUIPPABLE);
        var entityModel = ThatUtil.currentBipedModel;
        if (
            !(object instanceof BipedEntityRenderState) ||
            equippableComponent == null ||
            entityModel == null ||
            ThatUtil.currentSlot == null ||
            equippableComponent.slot() == ThatUtil.currentSlot
        ) {
            original.call(instance, model, object, matrices, renderLayer, light, overlay, tintColor, sprite, outlineColor, crumblingOverlayCommand);
            return;
        }
        var queue = orderedRenderCommandQueue.getBatchingQueue(batchIndexRef.get());
        ThatUtil.forbidTransforming = true;
        matrices.push();
        entityModel.getRootPart().applyTransform(matrices);
        if (model instanceof BipedEntityModel<?> armorModel) {
            switch (ThatUtil.currentSlot) {
                case HEAD -> {
                    entityModel.head.applyTransform(matrices);
                    switch (equippableComponent.slot()) {
                        case CHEST -> {
                            //body
                            matrices.translate(0, -20 / 16f, 0);
                            queue.submitModelPart(armorModel.body, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);

                            //right pauldron
                            matrices.push();
                            matrices.translate(-5 / 16f, 2 / 16f, 0);
                            matrices.scale(0.999f, 0.999f, 0.999f);
                            queue.submitModelPart(armorModel.rightArm, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                            matrices.pop();

                            //left pauldron
                            matrices.push();
                            matrices.translate(5 / 16f, 2 / 16f, 0);
                            matrices.scale(0.999f, 0.999f, 0.999f);
                            queue.submitModelPart(armorModel.leftArm, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                            matrices.pop();
                        }
                        case LEGS -> {
                            //body
                            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                            queue.submitModelPart(armorModel.body, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);

                            //right leg
                            matrices.translate(2 / 16f, 12 / 16f, 0);
                            queue.submitModelPart(armorModel.rightLeg, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);

                            //left leg
                            matrices.translate(-4 / 16f, 0, 0);
                            queue.submitModelPart(armorModel.leftLeg, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                        }
                        case FEET -> {
                            matrices.translate(-2.9 / 16f, -20 / 16f, 0);
                            queue.submitModelPart(armorModel.rightLeg, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                            matrices.translate(5.8 / 16f, 0, 0);
                            queue.submitModelPart(armorModel.leftLeg, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                        }
                    }
                }
                case CHEST -> {
                    var arm = ((LivingEntityRenderStateDuck) object).armmersive$getRenderingArm();
                    switch (equippableComponent.slot()) {
                        case HEAD -> {
                            if (arm != null) break;
                            entityModel.body.applyTransform(matrices);
                            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
                            matrices.scale(1.25f, 1.25f, 1.25f);
                            matrices.translate(0, 4 / 16f, 4 / 16f);
                            queue.submitModelPart(armorModel.head, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                        }
                        case LEGS -> {
                            //middle part
                            if (arm == null) {
                                matrices.push();
                                entityModel.body.applyTransform(matrices);
                                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
                                matrices.translate(0, -12 / 16f, 0);
                                queue.submitModelPart(armorModel.body, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                                matrices.pop();
                            }

                            //right leg
                            if (arm != Arm.LEFT) {
                                matrices.push();
                                entityModel.rightArm.applyTransform(matrices);
                                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
                                matrices.translate(0, -2 / 16f, -1 / 16f);
                                queue.submitModelPart(armorModel.rightLeg, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                                matrices.pop();
                            }

                            //left leg
                            if (arm != Arm.RIGHT) {
                                matrices.push();
                                entityModel.leftArm.applyTransform(matrices);
                                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90));
                                matrices.translate(0, -2 / 16f, -1 / 16f);
                                queue.submitModelPart(armorModel.leftLeg, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                                matrices.pop();
                            }
                        }
                        case FEET -> {
                            //right boot
                            if (arm != Arm.LEFT) {
                                matrices.push();
                                entityModel.rightArm.applyTransform(matrices);
                                matrices.translate(-1 / 16f, -2 / 16f, 0);
                                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
                                queue.submitModelPart(armorModel.rightLeg, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                                matrices.pop();
                            }

                            //left boot
                            if (arm != Arm.RIGHT) {
                                matrices.push();
                                entityModel.leftArm.applyTransform(matrices);
                                matrices.translate(1 / 16f, -2 / 16f, 0);
                                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90));
                                queue.submitModelPart(armorModel.leftLeg, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                                matrices.pop();
                            }
                        }
                    }
                }
                case LEGS -> {
                    switch (equippableComponent.slot()) {
                        case HEAD -> {
                            entityModel.body.applyTransform(matrices);
                            matrices.translate(0, 18 / 16f, 0);
                            queue.submitModelPart(armorModel.head, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                        }
                        case CHEST -> {
                            //body
                            matrices.push();
                            entityModel.body.applyTransform(matrices);
                            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
                            matrices.translate(0, -12 / 16f, 0);
                            queue.submitModelPart(armorModel.body, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                            matrices.pop();

                            //right pauldron
                            matrices.push();
                            entityModel.rightLeg.applyTransform(matrices);
                            matrices.translate(1 / 16f, 2 / 16f, 0);
                            queue.submitModelPart(armorModel.rightArm, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                            matrices.pop();

                            //left pauldron
                            matrices.push();
                            entityModel.leftLeg.applyTransform(matrices);
                            matrices.translate(-1 / 16f, 2 / 16f, 0);
                            queue.submitModelPart(armorModel.leftArm, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                            matrices.pop();
                        }
                        case FEET -> {
                            //right boot
                            matrices.push();
                            entityModel.rightLeg.applyTransform(matrices);
                            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                            matrices.translate(0, -12 / 16f, 0);
                            queue.submitModelPart(armorModel.rightLeg, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                            matrices.pop();

                            //left boot
                            matrices.push();
                            entityModel.leftLeg.applyTransform(matrices);
                            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                            matrices.translate(0, -12 / 16f, 0);
                            queue.submitModelPart(armorModel.leftLeg, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                            matrices.pop();
                        }
                    }
                }
                case FEET -> {
                    switch (equippableComponent.slot()) {
                        case HEAD -> {
                            matrices.push();
                            entityModel.leftLeg.applyTransform(matrices);
                            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
                            matrices.translate(0, -6 / 16f, 0);
                            matrices.scale(0.75f, 0.75f, 0.75f);
                            queue.submitModelPart(armorModel.head, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                            matrices.pop();
                        }
                        case CHEST -> {
                            //right sleeve
                            matrices.push();
                            entityModel.rightLeg.applyTransform(matrices);
                            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                            matrices.translate(1 / 16f, -10 / 16f, 0);
                            queue.submitModelPart(armorModel.rightArm, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                            matrices.pop();

                            //left sleeve
                            matrices.push();
                            entityModel.leftLeg.applyTransform(matrices);
                            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                            matrices.translate(-1 / 16f, -10 / 16f, 0);
                            queue.submitModelPart(armorModel.leftArm, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                            matrices.pop();
                        }
                        case LEGS -> {
                            //right leg
                            matrices.push();
                            entityModel.rightLeg.applyTransform(matrices);
                            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
                            matrices.translate(0, -12 / 16f, 0);
                            queue.submitModelPart(armorModel.rightLeg, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                            matrices.pop();

                            //left leg
                            matrices.push();
                            entityModel.leftLeg.applyTransform(matrices);
                            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
                            matrices.translate(0, -12 / 16f, 0);
                            queue.submitModelPart(armorModel.leftLeg, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                            matrices.pop();
                        }
                    }
                }
            }
        } else if (model instanceof ElytraEntityModel elytraModel) {
            switch (ThatUtil.currentSlot) {
                case HEAD -> {
                    matrices.translate(0, 0, -2/16f);
                    entityModel.head.applyTransform(matrices);
                    matrices.translate(0, -7 / 16f, 3/16f);
                    matrices.scale(1/2f, 1/2f, 1/2f);
                    original.call(instance, model, object, matrices, renderLayer, light, overlay, tintColor, sprite, outlineColor, crumblingOverlayCommand);
                }
                case LEGS -> {
                    //right leg
                    matrices.push();
                    entityModel.body.applyTransform(matrices);
                    matrices.translate(0, 8 / 16f, 0);
                    matrices.scale(1/2f, 1/2f, 1/2f);
                    original.call(instance, model, object, matrices, renderLayer, light, overlay, tintColor, sprite, outlineColor, crumblingOverlayCommand);
                    matrices.pop();
                }
                case FEET -> {
                    //right boot
                    matrices.push();
                    entityModel.rightLeg.applyTransform(matrices);
                    matrices.translate(0, 6 / 16f, 0);
                    matrices.scale(1/3f, 1/3f, 1/3f);
                    original.call(instance, model, object, matrices, renderLayer, light, overlay, tintColor, sprite, outlineColor, crumblingOverlayCommand);
                    matrices.pop();

                    //left boot
                    matrices.push();
                    entityModel.leftLeg.applyTransform(matrices);
                    matrices.translate(0, 6 / 16f, 0);
                    matrices.scale(1/3f, 1/3f, 1/3f);
                    original.call(instance, model, object, matrices, renderLayer, light, overlay, tintColor, sprite, outlineColor, crumblingOverlayCommand);
                    matrices.pop();
                }
            }
        }
        matrices.pop();
        ThatUtil.forbidTransforming = false;
    }
}
