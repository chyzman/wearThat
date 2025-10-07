package com.chyzman.wearthat.mixin;

import com.chyzman.wearthat.pond.LivingEntityRenderStateDuck;
import com.chyzman.wearthat.pond.ModelPartDuck;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Debug(export = true)
@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<S extends BipedEntityRenderState, M extends BipedEntityModel<S>, A extends BipedEntityModel<S>> extends FeatureRenderer<S, M> {

    @Shadow
    protected abstract A getModel(S state, EquipmentSlot slot);

    @Shadow
    protected abstract void setVisible(A bipedModel, EquipmentSlot slot);

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

    @SuppressWarnings("unchecked")
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
        @Local(argsOnly = true) int light,
        @Local(argsOnly = true) LocalRef<BipedEntityModel<BipedEntityRenderState>> armorModelRef
    ) {
        var returned = original.call(stack, componentType);
        var modelIsEmpty = false;
        if (this.renderState == null) return returned;
        if (returned instanceof EquippableComponent component) {
            armorModelRef.set((BipedEntityModel<BipedEntityRenderState>) getModel(this.renderState, component.slot()));
            modelIsEmpty = component.assetId().isEmpty();
        }
        if (returned == null || modelIsEmpty) {
            var armorModel = armorModelRef.get();
            setVisible((A) armorModel, slot);
            var contextModel = this.getContextModel();
            matrices.push();
            contextModel.getRootPart().applyTransform(matrices);
            switch (slot) {
                case CHEST -> {
                    var state = ((LivingEntityRenderStateDuck) this.renderState).wearThat$getChestItemRenderState();
                    //torso
                    if (armorModel.body.visible) {
                        matrices.push();
                        contextModel.body.applyTransform(matrices);
                        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                        matrices.translate(0, -1 / 4f, 0);
                        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                        state.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
                        matrices.scale(1.01f, 1.01f, 1.01f);
                        matrices.translate(0, -1 / 4f, 0);
                        state.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
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
                        state.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
                        matrices.scale(0.99f, 0.99f, 0.99f);
                        matrices.translate(0, -1 / 2f, 0);
                        state.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
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
                        state.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
                        matrices.scale(0.99f, 0.99f, 0.99f);
                        matrices.translate(0, -1 / 2f, 0);
                        state.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
                        matrices.pop();
                    }
                }

                case LEGS -> {
                    var state = ((LivingEntityRenderStateDuck) this.renderState).wearThat$getLegsItemRenderState();
                    //right leg
                    matrices.push();
                    contextModel.rightLeg.applyTransform(matrices);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                    matrices.scale(2 / 3f, 2 / 3f, 2 / 3f);
                    matrices.translate(0, -1 / 6f, 0);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                    state.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
                    matrices.scale(1.01f, 1.01f, 1.01f);
                    matrices.translate(0, -1 / 3f, 0);
                    state.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
                    matrices.pop();

                    //left leg
                    matrices.push();
                    contextModel.leftLeg.applyTransform(matrices);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                    matrices.scale(2 / 3f, 2 / 3f, 2 / 3f);
                    matrices.translate(0, -1 / 6f, 0);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                    state.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
                    matrices.scale(1.01f, 1.01f, 1.01f);
                    matrices.translate(0, -1 / 3f, 0);
                    state.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
                    matrices.pop();
                }
                case FEET -> {
                    var state = ((LivingEntityRenderStateDuck) this.renderState).wearThat$getFeetItemRenderState();
                    //right foot
                    matrices.push();
                    contextModel.rightLeg.applyTransform(matrices);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                    matrices.scale(0.75f, 0.75f, 0.75f);
                    matrices.translate(0, -0.8f, 0);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                    state.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
                    matrices.pop();

                    //left foot
                    matrices.push();
                    contextModel.leftLeg.applyTransform(matrices);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                    matrices.scale(0.75f, 0.75f, 0.75f);
                    matrices.translate(0, -0.8f, 0);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                    state.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
                    matrices.pop();
                }
            }
            matrices.pop();
        }
        return returned;
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

    @WrapOperation(
        method = "renderArmor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/component/type/EquippableComponent;assetId()Ljava/util/Optional;"
        )
    )
    private static Optional<Identifier> dontExplodeWhenGrabbingAssetId(
        EquippableComponent instance,
        Operation<Optional<Identifier>> original,
        @Cancellable() CallbackInfo ci
    ) {
        if (instance.assetId().isEmpty()) ci.cancel();
        return original.call(instance);
    }

    @WrapOperation(
        method = "renderArmor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;setVisible(Lnet/minecraft/client/render/entity/model/BipedEntityModel;Lnet/minecraft/entity/EquipmentSlot;)V"
        )
    )
    private void applyPostVisibility(
        ArmorFeatureRenderer<S, M, A> instance,
        A bipedModel,
        EquipmentSlot slot,
        Operation<Void> original,
        @Share("postVisibility") LocalRef<@Nullable Runnable> postVisibilityCallback
    ) {
        original.call(instance, bipedModel, slot);
        var postVisibility = postVisibilityCallback.get();
        if (postVisibility != null) {
            postVisibility.run();
            postVisibilityCallback.set(null);
        }
    }

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
        @Share("originalSlot") LocalRef<@Nullable EquipmentSlot> originalSlot
    ) {
        var slot = slotRef.get();
        originalSlot.set(slot != component.slot() ? slot : null);
        slotRef.set(component.slot());
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
        @Share("originalSlot") LocalRef<@Nullable EquipmentSlot> originalSlotRef,
        @Share("postVisibility") LocalRef<@Nullable Runnable> postVisibilityCallback
    ) {
        armorModel.getRootPart().resetTransform();
        armorModel.getParts().forEach(modelPart -> ((ModelPartDuck) (Object) modelPart).wearThat$setTransform(null));
        original.call(contextModel, armorModel);
        var originalSlot = originalSlotRef.get();
        if (originalSlot == null) return;
        armorModel.resetTransforms();
        switch (originalSlot) {
            case HEAD -> {
                armorModel.getRootPart().copyTransform(contextModel.getHead());
                switch (slot) {
                    case CHEST -> {
                        armorModel.body.moveOrigin(new Vector3f(0, -20, 0));
                        armorModel.rightArm.moveOrigin(new Vector3f(-1, -20, 0));
                        armorModel.rightArm.scale(new Vector3f(0.001f, 0.001f, 0.001f));
                        armorModel.leftArm.moveOrigin(new Vector3f(1, -20, 0));
                        armorModel.leftArm.scale(new Vector3f(0.001f, 0.001f, 0.001f));
                    }
                    case LEGS -> {
                        armorModel.getRootPart().rotate(RotationAxis.POSITIVE_Z.rotationDegrees(180));
                    }
                    case FEET -> {
                        armorModel.rightLeg.moveOrigin(new Vector3f(-1, -32, 0));
                        armorModel.leftLeg.moveOrigin(new Vector3f(1, -32, 0));
                    }
                }
            }
            case CHEST -> {
                switch (slot) {
                    case HEAD -> {
                        armorModel.head.copyTransform(contextModel.body);
                        ((ModelPartDuck) (Object) armorModel.head).wearThat$setTransform(matrices -> {
                            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
                            matrices.scale(1.25f, 1.25f, 1.25f);
                            matrices.translate(0, 4, 4);
                        });
                    }
                    case LEGS -> {
                        //middle part
                        armorModel.body.copyTransform(contextModel.body);
                        ((ModelPartDuck) (Object) armorModel.body).wearThat$setTransform(matrices -> {
                            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
                            matrices.translate(0, -12, 0);
                        });

                        //right leg
                        armorModel.rightLeg.copyTransform(contextModel.rightArm);
                        armorModel.rightLeg.moveOrigin(new Vector3f(-1f, 0.1f, 0));

                        //left leg
                        armorModel.leftLeg.copyTransform(contextModel.leftArm);
                        armorModel.leftLeg.moveOrigin(new Vector3f(1f, 0.1f, 0));
                    }
                    case FEET -> {
                        //right boot
                        armorModel.rightLeg.copyTransform(contextModel.rightArm);
                        ((ModelPartDuck) (Object) armorModel.rightLeg).wearThat$setTransform(matrices -> {
                            matrices.translate(-1, -1, 0);
                            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
                        });

                        //left boot
                        armorModel.leftLeg.copyTransform(contextModel.leftArm);
                        ((ModelPartDuck) (Object) armorModel.leftLeg).wearThat$setTransform(matrices -> {
                            matrices.translate(1, -1, 0);
                            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90));
                        });
                    }
                }
            }
            case LEGS -> {
                switch (slot) {
                    case HEAD -> {
                        armorModel.head.copyTransform(contextModel.body);
                        ((ModelPartDuck) (Object) armorModel.head).wearThat$setTransform(matrices -> {
                            matrices.translate(0, 18, 0);
                        });
                    }
                    case CHEST -> {
                        //middle part
                        armorModel.body.copyTransform(contextModel.body);
                        ((ModelPartDuck) (Object) armorModel.body).wearThat$setTransform(matrices -> {
                            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
                            matrices.translate(0, -12, 0);
                        });

                        //right sleeve
                        armorModel.rightArm.copyTransform(contextModel.rightLeg);

                        //left sleeve
                        armorModel.leftArm.copyTransform(contextModel.leftLeg);
                    }
                    case FEET -> {
                        //right boot
                        armorModel.rightLeg.copyTransform(contextModel.rightLeg);
                        ((ModelPartDuck) (Object) armorModel.rightLeg).wearThat$setTransform(matrices -> {
                            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                            matrices.translate(0, -10, 0);
                        });

                        //left boot
                        armorModel.leftLeg.copyTransform(contextModel.leftLeg);
                        ((ModelPartDuck) (Object) armorModel.leftLeg).wearThat$setTransform(matrices -> {
                            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                            matrices.translate(0, -10, 0);
                        });
                    }
                }
            }
            case FEET -> {
                switch (slot) {
                    case HEAD -> {
                        armorModel.head.copyTransform(contextModel.leftLeg);
                        ((ModelPartDuck) (Object) armorModel.head).wearThat$setTransform(matrices -> {
                            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
                            matrices.scale(0.75f, 0.75f, 0.75f);
                            matrices.translate(0, -7, 0);
                        });
                    }
                    case CHEST -> {
                        //middle part
                        postVisibilityCallback.set(() -> armorModel.body.visible = false);

                        //right sleeve
                        armorModel.rightArm.copyTransform(contextModel.rightLeg);
                        ((ModelPartDuck) (Object) armorModel.rightArm).wearThat$setTransform(matrices -> {
                            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                            matrices.translate(1, -9, 0);
                        });

                        //left sleeve
                        armorModel.leftArm.copyTransform(contextModel.leftLeg);
                        ((ModelPartDuck) (Object) armorModel.leftArm).wearThat$setTransform(matrices -> {
                            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                            matrices.translate(-1, -9, 0);
                        });
                    }
                    case LEGS -> {
                        //middle part
                        postVisibilityCallback.set(() -> armorModel.body.visible = false);

                        //right leg
                        armorModel.rightLeg.copyTransform(contextModel.rightLeg);
                        ((ModelPartDuck) (Object) armorModel.rightLeg).wearThat$setTransform(matrices -> {
                            matrices.translate(0, 3, 0);
                            matrices.scale(1.001f, 1.001f, 1.001f);
                        });

                        //left leg
                        armorModel.leftLeg.copyTransform(contextModel.leftLeg);
                        ((ModelPartDuck) (Object) armorModel.leftLeg).wearThat$setTransform(matrices -> {
                            matrices.translate(0, 3, 0);
                            matrices.scale(1.001f, 1.001f, 1.001f);
                        });
                    }
                }
            }
        }
        originalSlotRef.set(null);
    }
}
