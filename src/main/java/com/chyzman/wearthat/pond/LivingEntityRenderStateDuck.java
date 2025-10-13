package com.chyzman.wearthat.pond;

import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.util.Arm;
import org.jetbrains.annotations.Nullable;

public interface LivingEntityRenderStateDuck {
    // we default the method that armmersive adds so that we don't need explicit dependencies :3
    default @Nullable Arm armmersive$getRenderingArm() {
        return null;
    }

    ItemRenderState wearThat$getChestItemRenderState();
    ItemRenderState wearThat$getLegsItemRenderState();
    ItemRenderState wearThat$getFeetItemRenderState();

    void wearThat$setEnchanted(boolean enchanted);
    boolean wearThat$isEnchanted();

    void wearThat$setColor(int color);
    int wearThat$getColor();

    void wearThat$setEmissive(boolean emissive);
    boolean wearThat$isEmissive();
}
