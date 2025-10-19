package com.chyzman.wearthat.mixin.compat.accessories;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.menu.AccessoriesBasedSlot;
import io.wispforest.accessories.impl.core.ExpandedContainer;
import io.wispforest.accessories.menu.AccessoriesInternalSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AccessoriesInternalSlot.class)
public abstract class AccessoriesInternalSlotMixin extends AccessoriesBasedSlot {

    @Unique
    private boolean flag = false;

    public AccessoriesInternalSlotMixin(AccessoriesContainer accessoriesContainer, ExpandedContainer container, int slot, int x, int y) {
        super(accessoriesContainer, container, slot, x, y);
    }

    @ModifyExpressionValue(
        method = "canInsert",
        at = @At(
            value = "INVOKE",
            target = "Lio/wispforest/accessories/api/slot/SlotPredicateRegistry;getPredicateResults(Ljava/util/Set;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lio/wispforest/accessories/api/slot/SlotType;ILnet/minecraft/item/ItemStack;)Z"
        )
    )
    private boolean allowAnythingAsCosmeticAccessories(
        boolean original,
        @Local(argsOnly = true) ItemStack stack
    ) {
        flag &= original;
        return true;
    }

    @Override
    public int getMaxItemCount(ItemStack stack) {
        flag = true;
        this.canInsert(stack);
        var wearThatApplied = flag;
        flag = false;
        if (wearThatApplied) return super.getMaxItemCount(stack);
        return 1;
    }
}
