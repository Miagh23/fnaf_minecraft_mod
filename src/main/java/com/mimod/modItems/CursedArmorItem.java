package com.mimod.modItems;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

public class CursedArmorItem extends ArmorItem {
    public CursedArmorItem(Holder<ArmorMaterial> material, Type type, Properties props) {
        super(material, type, props);
    }

    public static boolean isBound(ItemStack stack) {
        return stack.getItem() instanceof CursedArmorItem;
    }
}
