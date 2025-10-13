package com.chyzman.wearthat.mixin.client;

import com.chyzman.wearthat.pond.LivingEntityRenderStateDuck;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.item.PlayerHeadItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Environment(EnvType.CLIENT)
@Mixin(
    value = PlayerEntityRenderer.class,
    priority = -1000 // ensure we inject before armmersive because we will cancel submitModelPart
)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityRenderState, PlayerEntityModel> {

    @Shadow public abstract PlayerEntityRenderState createRenderState();

    public PlayerEntityRendererMixin(
        EntityRendererFactory.Context ctx,
        PlayerEntityModel model,
        float shadowRadius
    ) {
        super(ctx, model, shadowRadius);
    }

    @WrapOperation(
        method = "renderArm",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitModelPart(Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IILnet/minecraft/client/texture/Sprite;)V"
        )
    )
    private void applyEasterEggsToFirstPerson(
        OrderedRenderCommandQueue instance,
        ModelPart modelPart,
        MatrixStack matrixStack,
        RenderLayer renderLayer,
        int light,
        int overlay,
        Sprite sprite,
        Operation<Void> original
    ) {
        var client = MinecraftClient.getInstance();
        var player = client.player;
        if (player == null) {
            original.call(instance, modelPart, matrixStack, renderLayer, light, overlay, sprite);
            return;
        }

        var tickDelta = client.getRenderTickCounter().getDynamicDeltaTicks();
        var renderState = this.createRenderState();
        this.updateRenderState(player, renderState, tickDelta);

        var duck = (LivingEntityRenderStateDuck) renderState;
        instance.submitModelPart(
            modelPart,
            matrixStack,
            renderLayer,
            duck.wearThat$isEmissive() ? LightmapTextureManager.MAX_LIGHT_COORDINATE : light,
            overlay,
            sprite,
            false,
            duck.wearThat$isEnchanted(),
            duck.wearThat$getColor(),
            null,
            0
        );
    }
}
