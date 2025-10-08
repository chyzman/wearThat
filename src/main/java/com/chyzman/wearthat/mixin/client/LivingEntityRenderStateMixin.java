package com.chyzman.wearthat.mixin.client;

import com.chyzman.wearthat.pond.LivingEntityRenderStateDuck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderState.class)
public abstract class LivingEntityRenderStateMixin implements LivingEntityRenderStateDuck {

    @Unique
    private final ItemRenderState chestItemRenderState = new ItemRenderState();

    @Unique
    private final ItemRenderState legsItemRenderState = new ItemRenderState();

    @Unique
    private final ItemRenderState feetItemRenderState = new ItemRenderState();

    @Unique
    private boolean isEnchanted = false;

    @Unique
    private int color = -1;

    @Override
    public ItemRenderState wearThat$getChestItemRenderState() {
        return chestItemRenderState;
    }

    @Override
    public ItemRenderState wearThat$getLegsItemRenderState() {
        return legsItemRenderState;
    }

    @Override
    public ItemRenderState wearThat$getFeetItemRenderState() {
        return feetItemRenderState;
    }

    @Override
    public void wearThat$setEnchanted(boolean enchanted) {
        this.isEnchanted = enchanted;
    }

    @Override
    public boolean wearThat$isEnchanted() {
        return isEnchanted;
    }

    @Override
    public void wearThat$setColor(int color) {
        this.color = color;
    }

    @Override
    public int wearThat$getColor() {
        return color;
    }
}
