package com.chyzman.wearthat.mixin;

import com.chyzman.wearthat.pond.LivingEntityRenderStateDuck;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
public abstract class LivingEntityRenderStateMixin implements LivingEntityRenderStateDuck {

    @Unique
    private final ItemRenderState chestItemRenderState = new ItemRenderState();

    @Unique
    private final ItemRenderState legsItemRenderState = new ItemRenderState();

    @Unique
    private final ItemRenderState feetItemRenderState = new ItemRenderState();

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
}
