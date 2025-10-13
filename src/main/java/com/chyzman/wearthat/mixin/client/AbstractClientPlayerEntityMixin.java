package com.chyzman.wearthat.mixin.client;

import com.chyzman.wearthat.pond.SkinTexturesDuck;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerLikeEntity;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.item.PlayerHeadItem;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity implements ClientPlayerLikeEntity {
    public AbstractClientPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @ModifyReturnValue(
        method = "getSkin",
        at = @At("RETURN")
    )
    private SkinTextures modifySkin(SkinTextures original) {
        var client = MinecraftClient.getInstance();
        @Nullable SkinTextures possibleOverride = null;
        for (var value : EquipmentSlot.values()) {
            if (value.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) continue;
            var stack = getEquippedStack(value);
            if (!(stack.getItem() instanceof PlayerHeadItem)) {
                possibleOverride = null;
                break;
            }

            var profile = stack.get(DataComponentTypes.PROFILE);
            var textures = profile != null ? client.getPlayerSkinCache().get(profile).getTextures() : DefaultSkinHelper.getSteve();

            if (textures == null || possibleOverride != null && textures != possibleOverride) {
                possibleOverride = null;
                break;
            }

            possibleOverride = textures;
        }
        if (possibleOverride != null) {
            ((SkinTexturesDuck) (Object) possibleOverride).wearThat$setOverridden(true);
            return possibleOverride;
        }
        return original;
    }

}
