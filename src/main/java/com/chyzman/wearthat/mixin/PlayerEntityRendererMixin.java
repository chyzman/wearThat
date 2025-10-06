package com.chyzman.wearthat.mixin;

import com.chyzman.wearthat.ThatUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import static net.minecraft.entity.EquipmentSlot.*;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityRenderState, PlayerEntityModel> {

    @Shadow public abstract PlayerEntityRenderState createRenderState();

    @Shadow public abstract void updateRenderState(
        AbstractClientPlayerEntity abstractClientPlayerEntity,
        PlayerEntityRenderState playerEntityRenderState,
        float f
    );

    @Unique private ArmorFeatureRenderer<PlayerEntityRenderState, PlayerEntityModel, PlayerEntityModel> armorFeatureRenderer;

    public PlayerEntityRendererMixin(
        EntityRendererFactory.Context ctx,
        PlayerEntityModel model,
        float shadowRadius
    ) {
        super(ctx, model, shadowRadius);
    }

    @WrapOperation(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;addFeature(Lnet/minecraft/client/render/entity/feature/FeatureRenderer;)Z"
        )
    )
    private boolean grabArmorFeatureRenderer(
        PlayerEntityRenderer instance,
        FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> featureRenderer,
        Operation<Boolean> original
    ) {
        if (featureRenderer instanceof ArmorFeatureRenderer<?, ?, ?> armor)
            this.armorFeatureRenderer = (ArmorFeatureRenderer<PlayerEntityRenderState, PlayerEntityModel, PlayerEntityModel>) armor;
        return original.call(instance, featureRenderer);
    }

    @WrapOperation(
        method = "renderArm",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"
        )
    )
    private void renderArmorInFirstPerson(
        ModelPart instance,
        MatrixStack matrices,
        VertexConsumer vertices,
        int light,
        int overlay,
        Operation<Void> original,
        @Local(argsOnly = true) VertexConsumerProvider vertexConsumers
    ) {
        var client = MinecraftClient.getInstance();
        var player = client.player;
        if (player == null) return;
        if (!client.player.isInvisible()) original.call(instance, matrices, vertices, light, overlay);
        if (this.armorFeatureRenderer == null) return;
        var tickDelta = client.getRenderTickCounter().getTickDelta(true);
        var chestStack = player.getEquippedStack(CHEST);
        if (!chestStack.isEmpty()) {
            ThatUtil.renderingArm = instance == this.model.leftArm ? Arm.LEFT : Arm.RIGHT;
            var renderState = this.createRenderState();
            this.updateRenderState(player, renderState, tickDelta);
            armorFeatureRenderer.render(matrices, vertexConsumers, light, renderState, renderState.yawDegrees, renderState.pitch);
            ThatUtil.renderingArm = null;
        }
    }
}
