package com.chyzman.wearthat.mixin.client;

import com.chyzman.wearthat.ThatUtil;
import com.chyzman.wearthat.pond.LivingEntityRenderStateDuck;
import com.chyzman.wearthat.pond.SkinTexturesDuck;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.network.ClientPlayerLikeEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.GlowInkSacItem;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {

    @Shadow @Final protected ItemModelManager itemModelResolver;

    @Shadow protected M model;

    @WrapMethod(
        method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V"
    )
    public void wearThat$passModelToFeatureRenderers(
        LivingEntityRenderState renderState,
        MatrixStack matrices,
        OrderedRenderCommandQueue queue,
        CameraRenderState cameraRenderState,
        Operation<Void> original
    ) {
        if (model instanceof BipedEntityModel<?> bipedModel) ThatUtil.currentBipedModel = (BipedEntityModel<BipedEntityRenderState>) bipedModel;
        original.call(renderState, matrices, queue, cameraRenderState);
        ThatUtil.currentBipedModel = null;
    }

    @WrapOperation(
        method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IIILnet/minecraft/client/texture/Sprite;ILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V"
        )
    )
    public void applyEasterEggs(
        OrderedRenderCommandQueue instance,
        Model<?> model,
        Object o,
        MatrixStack matrixStack,
        RenderLayer renderLayer,
        int light,
        int overlay,
        int tintColor,
        Sprite sprite,
        int outlineColor,
        ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand,
        Operation<Void> original
    ) {
        var duck = (LivingEntityRenderStateDuck) o;
        original.call(
            instance,
            model,
            o,
            matrixStack,
            renderLayer,
            duck.wearThat$isEmissive() ? LightmapTextureManager.MAX_LIGHT_COORDINATE : light,
            overlay,
            tintColor != -1 ? duck.wearThat$getColor() != -1 ? ColorHelper.average(tintColor, duck.wearThat$getColor()) : tintColor : duck.wearThat$getColor(),
            sprite,
            outlineColor,
            crumblingOverlayCommand
        );
        if (duck.wearThat$isEnchanted())
            original.call(
                instance,
                model,
                o,
                matrixStack,
                RenderLayer.getEntityGlint(),
                light,
                overlay,
                tintColor,
                sprite,
                outlineColor,
                crumblingOverlayCommand
            );
    }

    @ModifyExpressionValue(
        method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;getEquippedStack(Lnet/minecraft/entity/EquipmentSlot;)Lnet/minecraft/item/ItemStack;"
        )
    )
    public ItemStack updateWearThatItemRenderStates(
        ItemStack headStack,
        @Local(argsOnly = true) LivingEntity entity,
        @Local(argsOnly = true) LivingEntityRenderState renderState
    ) {
        var duck = (LivingEntityRenderStateDuck) renderState;

        duck.wearThat$setEnchanted(false);
        duck.wearThat$setColor(-1);
        duck.wearThat$setEmissive(false);

        for (var slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) continue;
            if (slot.getType() == EquipmentSlot.Type.HAND) continue;
            var stack = entity.getEquippedStack(slot);
            if (ArmorFeatureRenderer.hasModel(stack, slot)) continue;
            shouldRenderWithWearThat(stack, duck, entity);
        }

        updateWearThatItemRenderState(entity, renderState, EquipmentSlot.CHEST, duck.wearThat$getChestItemRenderState());
        updateWearThatItemRenderState(entity, renderState, EquipmentSlot.LEGS, duck.wearThat$getLegsItemRenderState());
        updateWearThatItemRenderState(entity, renderState, EquipmentSlot.FEET, duck.wearThat$getFeetItemRenderState());

        var headOverridden = shouldRenderWithWearThat(headStack, (LivingEntityRenderStateDuck) renderState, entity);
        return headOverridden ? headStack : ItemStack.EMPTY;
    }

    @Unique
    private void updateWearThatItemRenderState(LivingEntity entity, LivingEntityRenderState entityRenderState, EquipmentSlot slot, ItemRenderState itemRenderState) {
        var stack = entity.getEquippedStack(slot);
        if (!ArmorFeatureRenderer.hasModel(stack, slot) && shouldRenderWithWearThat(stack, (LivingEntityRenderStateDuck) entityRenderState, entity)) {
            itemModelResolver.updateForLivingEntity(itemRenderState, stack, ItemDisplayContext.FIXED, entity);
        } else {
            itemRenderState.clear();
        }
    }

    @Unique
    private static boolean shouldRenderWithWearThat(ItemStack stack, LivingEntityRenderStateDuck duck, LivingEntity entity) {
        var canceled = entity instanceof ClientPlayerLikeEntity playerLike && ((SkinTexturesDuck) (Object) playerLike.getSkin()).wearThat$isOverridden();
        if (stack.contains(DataComponentTypes.STORED_ENCHANTMENTS)) {
            duck.wearThat$setEnchanted(true);
            canceled = true;
        }
        if (stack.getItem() instanceof DyeItem dyeItem) {
            duck.wearThat$setColor(ColorHelper.average(duck.wearThat$getColor(), dyeItem.getColor().getEntityColor()));
            canceled = true;
        }
        if (stack.getItem() instanceof GlowInkSacItem) {
            duck.wearThat$setEmissive(true);
            canceled = true;
        }
        return !canceled;
    }
}
