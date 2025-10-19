package com.chyzman.wearthat.mixin.client.duck;

import com.chyzman.wearthat.pond.LivingEntityRenderStateDuck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;
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
    @Nullable
    private Integer color = -1;

    @Override
    public void wearThat$setColor(@Nullable Integer color) {
        this.color = color;
    }

    @Override
    @Nullable
    public Integer wearThat$getColor() {
        return color;
    }

    @Override
    public void wearThat$mergeColor(int color) {
        this.color = wearThat$applyColor(color);
    }

    @Override
    public int wearThat$applyColor(int color) {
        if (this.color == null) return color;
        return ColorHelper.average(color, this.color);
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
