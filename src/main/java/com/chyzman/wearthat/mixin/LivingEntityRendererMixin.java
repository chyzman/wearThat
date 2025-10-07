package com.chyzman.wearthat.mixin;

import com.chyzman.wearthat.pond.LivingEntityRenderStateDuck;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.screen.slot.ArmorSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {

    @Shadow @Final protected ItemModelManager itemModelResolver;

    @Inject(
        method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;hasModel(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EquipmentSlot;)Z"
        )
    )
    public void updateWearThatItemRenderStates(
        T livingEntity,
        S livingEntityRenderState,
        float f,
        CallbackInfo ci
    ) {
        var duck = (LivingEntityRenderStateDuck) livingEntityRenderState;

        //update chest
        var chestStack = livingEntity.getEquippedStack(EquipmentSlot.CHEST);
        if (!ArmorFeatureRenderer.hasModel(chestStack, EquipmentSlot.CHEST)) {
            itemModelResolver.updateForLivingEntity(duck.wearThat$getChestItemRenderState(), chestStack, ModelTransformationMode.FIXED, false, livingEntity);
        } else {
            duck.wearThat$getChestItemRenderState().clear();
        }

        //update legs
        var legsStack = livingEntity.getEquippedStack(EquipmentSlot.LEGS);
        if (!ArmorFeatureRenderer.hasModel(legsStack, EquipmentSlot.LEGS)) {
            itemModelResolver.updateForLivingEntity(duck.wearThat$getLegsItemRenderState(), legsStack, ModelTransformationMode.FIXED, false, livingEntity);
        } else {
            duck.wearThat$getLegsItemRenderState().clear();
        }

        //update feet
        var feetStack = livingEntity.getEquippedStack(EquipmentSlot.FEET);
        if (!ArmorFeatureRenderer.hasModel(feetStack, EquipmentSlot.FEET)) {
            itemModelResolver.updateForLivingEntity(duck.wearThat$getFeetItemRenderState(), feetStack, ModelTransformationMode.FIXED, false, livingEntity);
        } else {
            duck.wearThat$getFeetItemRenderState().clear();
        }
    }
}
