package com.mimod.modItems;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;

public class FoxyHook extends SwordItem {

    public FoxyHook(Item.Properties properties) {
        super(Tiers.IRON,
                properties.attributes(
                        SwordItem.createAttributes(Tiers.IRON, 4, -2.4F)
                )
        );
    }
}