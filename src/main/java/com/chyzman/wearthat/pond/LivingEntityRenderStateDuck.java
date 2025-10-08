package com.chyzman.wearthat.pond;

import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;

import java.util.function.Consumer;

public interface LivingEntityRenderStateDuck {
    ItemRenderState wearThat$getChestItemRenderState();
    ItemRenderState wearThat$getLegsItemRenderState();
    ItemRenderState wearThat$getFeetItemRenderState();

    void wearThat$setEnchanted(boolean enchanted);
    boolean wearThat$isEnchanted();

    void wearThat$setColor(int color);
    int wearThat$getColor();
}
