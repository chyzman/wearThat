package com.chyzman.wearthat.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.ArmorSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorSlot.class)
public abstract class ArmorSlotMixin {

    @Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
    public void allowItemEquipping(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
