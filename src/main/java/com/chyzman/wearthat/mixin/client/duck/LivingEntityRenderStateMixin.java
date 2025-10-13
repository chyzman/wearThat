package com.chyzman.wearthat.mixin.client.duck;

import com.chyzman.wearthat.pond.LivingEntityRenderStateDuck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.player.SkinTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderState.class)
public abstract class LivingEntityRenderStateMixin implements LivingEntityRenderStateDuck {

    @Unique
    private final ItemRenderState chestItemRenderState = new ItemRenderState();

    @Override
    public ItemRenderState wearThat$getChestItemRenderState() {
        return chestItemRenderState;
    }

    @Unique
    private final ItemRenderState legsItemRenderState = new ItemRenderState();

    @Override
    public ItemRenderState wearThat$getLegsItemRenderState() {
        return legsItemRenderState;
    }

    @Unique
    private final ItemRenderState feetItemRenderState = new ItemRenderState();

    @Override
    public ItemRenderState wearThat$getFeetItemRenderState() {
        return feetItemRenderState;
    }

    @Unique
    private boolean isEnchanted = false;

    @Override
    public void wearThat$setEnchanted(boolean enchanted) {
        this.isEnchanted = enchanted;
    }

    @Override
    public boolean wearThat$isEnchanted() {
        return isEnchanted;
    }

    @Unique
    private int color = -1;

    @Override
    public void wearThat$setColor(int color) {
        this.color = color;
    }

    @Override
    public int wearThat$getColor() {
        return color;
    }

    @Unique
    private boolean isEmissive = false;

    @Override
    public void wearThat$setEmissive(boolean emissive) {
        this.isEmissive = emissive;
    }

    @Override
    public boolean wearThat$isEmissive() {
        return isEmissive;
    }
}
