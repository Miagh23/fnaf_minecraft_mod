package com.mimod.mixin;

import com.mimod.modItems.CursedArmorItem;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(method = "has", at = @At("HEAD"), cancellable = true)
    private static void mimod$bindCursedArmor(ItemStack stack, DataComponentType<?> type,
                                              CallbackInfoReturnable<Boolean> cir) {
        if (type == EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE
                && CursedArmorItem.isBound(stack)) {
            cir.setReturnValue(true);
        }
    }
}