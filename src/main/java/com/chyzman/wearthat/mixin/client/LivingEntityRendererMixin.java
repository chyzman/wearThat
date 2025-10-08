package com.chyzman.wearthat.mixin.client;

import com.chyzman.wearthat.pond.LivingEntityRenderStateDuck;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import static net.minecraft.entity.EquipmentSlot.*;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

    @Shadow @Final protected ItemModelManager itemModelResolver;

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
        var color = tintColor != -1
            ? duck.wearThat$getColor() != -1
            ? ColorHelper.average(tintColor, duck.wearThat$getColor())
            : tintColor
            : duck.wearThat$getColor();
        original.call(instance, model, o, matrixStack, renderLayer, light, overlay, color, sprite, outlineColor, crumblingOverlayCommand);
        if (duck.wearThat$isEnchanted())
            original.call(instance, model, o, matrixStack, RenderLayer.getEntityGlint(), light, overlay, tintColor, sprite, outlineColor, crumblingOverlayCommand);
    }

    @ModifyExpressionValue(
        method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;hasModel(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EquipmentSlot;)Z"
        )
    )
    public boolean updateWearThatItemRenderStates(
        boolean original,
        @Local() ItemStack headStack,
        @Local(argsOnly = true) LivingEntity entity,
        @Local(argsOnly = true) LivingEntityRenderState renderState
    ) {
        var duck = (LivingEntityRenderStateDuck) renderState;

        duck.wearThat$setEnchanted(false);
        duck.wearThat$setColor(-1);

        updateWearThatItemRenderState(entity, renderState, CHEST, duck.wearThat$getChestItemRenderState());
        updateWearThatItemRenderState(entity, renderState, LEGS, duck.wearThat$getLegsItemRenderState());
        updateWearThatItemRenderState(entity, renderState, FEET, duck.wearThat$getFeetItemRenderState());

        return original || !shouldRenderWithWearThat(headStack, duck);
    }

    @Unique
    private void updateWearThatItemRenderState(LivingEntity entity, LivingEntityRenderState entityRenderState, EquipmentSlot slot, ItemRenderState itemRenderState) {
        var stack = entity.getEquippedStack(slot);
        if (!ArmorFeatureRenderer.hasModel(stack, slot) && shouldRenderWithWearThat(stack, (LivingEntityRenderStateDuck) entityRenderState)) {
            itemModelResolver.updateForLivingEntity(itemRenderState, stack, ItemDisplayContext.FIXED, entity);
        } else {
            itemRenderState.clear();
        }
    }

    @Unique
    private static boolean shouldRenderWithWearThat(ItemStack stack, LivingEntityRenderStateDuck duck) {
        var canceled = false;
        if (stack.contains(DataComponentTypes.STORED_ENCHANTMENTS)) {
            duck.wearThat$setEnchanted(true);
            canceled = true;
        }
        if (stack.getItem() instanceof DyeItem dyeItem) {
            duck.wearThat$setColor(ColorHelper.average(duck.wearThat$getColor(), dyeItem.getColor().getEntityColor()));
            canceled = true;
        }
        return !canceled;
    }
}
