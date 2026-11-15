package com.mimod.modItems;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class GuitarPick extends Item {

    public GuitarPick(Item.Properties properties) {
        super(properties);
    }

    public static boolean isPlayingGuitar(Player player) {
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();

        return (main.getItem() instanceof BonnieGuitar && off.getItem() instanceof GuitarPick)
                || (main.getItem() instanceof GuitarPick && off.getItem() instanceof BonnieGuitar);
    }
}
