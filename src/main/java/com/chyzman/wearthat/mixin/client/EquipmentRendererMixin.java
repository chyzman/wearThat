package com.chyzman.wearthat.mixin.client;

import com.chyzman.wearthat.ThatUtil;
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
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
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
            !(model instanceof BipedEntityModel<?> armorModel) ||
            equippableComponent == null || //TODO: this should trigger the original wearthat item on body thingy
            entityModel == null ||
            ThatUtil.currentSlot == null ||
            equippableComponent.slot() == ThatUtil.currentSlot
        ) {
            original.call(instance, model, object, matrices, renderLayer, light, overlay, tintColor, sprite, outlineColor, crumblingOverlayCommand);
            return;
        }
        var queue = orderedRenderCommandQueue.getBatchingQueue(batchIndexRef.get());
        matrices.push();
        entityModel.getRootPart().applyTransform(matrices);
        switch (ThatUtil.currentSlot) {
            case HEAD -> {
                entityModel.head.applyTransform(matrices);
                switch (equippableComponent.slot()) {
                    case CHEST -> {
                        matrices.translate(0, -20 / 16f, 0);
                        queue.submitModelPart(armorModel.body, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                        matrices.scale(1.001f, 1.001f, 1.001f);
                        queue.submitModelPart(armorModel.rightArm, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                        queue.submitModelPart(armorModel.leftArm, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                    }
                    case LEGS -> {
                        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                        queue.submitModelPart(armorModel.body, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                        queue.submitModelPart(armorModel.rightLeg, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                        queue.submitModelPart(armorModel.leftLeg, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                    }
                    case FEET -> {
                        matrices.translate(-1/16f, -32 / 16f, 0);
                        queue.submitModelPart(armorModel.rightLeg, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                        matrices.translate(2/16f, 0, 0);
                        queue.submitModelPart(armorModel.leftLeg, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                    }
                }
            }
            case CHEST -> {
                switch (equippableComponent.slot()) {
                    case HEAD -> {
                        entityModel.body.applyTransform(matrices);
                        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
                        matrices.scale(1.25f, 1.25f, 1.25f);
                        matrices.translate(0, 4/16f, 4/16f);
                        queue.submitModelPart(armorModel.head, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                    }
                    case LEGS -> {
                        //middle part
                        matrices.push();
                        entityModel.body.applyTransform(matrices);
                        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
                        matrices.translate(0, -12/16f, 0);
                        queue.submitModelPart(armorModel.body, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                        matrices.pop();

                        //right leg
                        matrices.push();
                        entityModel.rightArm.applyTransform(matrices);
                        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
                        matrices.translate(2/16f, -14/16f, -1/16f);
                        queue.submitModelPart(armorModel.rightLeg, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                        matrices.pop();

                        //left leg
                        matrices.push();
                        entityModel.leftArm.applyTransform(matrices);
                        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90));
                        matrices.translate(-2/16f, -14/16f, -1/16f);
                        queue.submitModelPart(armorModel.leftLeg, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                        matrices.pop();
                    }
                    case FEET -> {
                        //right boot
                        matrices.push();
                        entityModel.rightArm.applyTransform(matrices);
                        matrices.translate(1/16f, -13/16f, 0);
                        queue.submitModelPart(armorModel.rightLeg, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                        matrices.pop();

                        //left boot
                        matrices.push();
                        entityModel.leftArm.applyTransform(matrices);
                        matrices.translate(-1/16f, -13/16f, 0);
                        queue.submitModelPart(armorModel.leftLeg, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                        matrices.pop();
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
                        //middle part
                        matrices.push();
                        entityModel.body.applyTransform(matrices);
                        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
                        matrices.translate(0, -12/16f, 0);
                        queue.submitModelPart(armorModel.body, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                        matrices.pop();

                        //right sleeve
                        matrices.push();
                        entityModel.rightLeg.applyTransform(matrices);
                        matrices.translate(5/16f, 0, 0);
                        queue.submitModelPart(armorModel.rightArm, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                        matrices.pop();

                        //left sleeve
                        matrices.push();
                        entityModel.leftLeg.applyTransform(matrices);
                        matrices.translate(-5/16f, 0, 0);
                        queue.submitModelPart(armorModel.leftArm, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                        matrices.pop();
                    }
                    case FEET -> {
                        //right boot
                        matrices.push();
                        entityModel.rightLeg.applyTransform(matrices);
                        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                        matrices.translate(2/16f, -24/16f, 0);
                        queue.submitModelPart(armorModel.rightLeg, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                        matrices.pop();

                        //left boot
                        matrices.push();
                        entityModel.leftLeg.applyTransform(matrices);
                        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                        matrices.translate(-2/16f, -24/16f, 0);
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
                        matrices.translate(0, -6/16f, 0);
                        matrices.scale(0.75f, 0.75f, 0.75f);
                        queue.submitModelPart(armorModel.head, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                        matrices.pop();
                    }
                    case CHEST -> {
                        //right sleeve
                        matrices.push();
                        entityModel.rightLeg.applyTransform(matrices);
                        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                        matrices.translate(6/16f, -12/16f, 0);
                        queue.submitModelPart(armorModel.rightArm, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                        matrices.pop();

                        //left sleeve
                        matrices.push();
                        entityModel.leftLeg.applyTransform(matrices);
                        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                        matrices.translate(-6/16f, -12/16f, 0);
                        queue.submitModelPart(armorModel.leftArm, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                        matrices.pop();
                    }
                    case LEGS -> {
                        //right leg
                        matrices.push();
                        entityModel.rightLeg.applyTransform(matrices);
                        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
                        matrices.translate(2/16f, -24/16f, 0);
                        queue.submitModelPart(armorModel.rightLeg, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                        matrices.pop();

                        //left leg
                        matrices.push();
                        entityModel.leftLeg.applyTransform(matrices);
                        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
                        matrices.translate(-2/16f, -24/16f, 0);
                        queue.submitModelPart(armorModel.leftLeg, matrices, renderLayer, light, overlay, sprite, tintColor, crumblingOverlayCommand);
                        matrices.pop();
                    }
                }
            }
        }
        matrices.pop();
    }
}
