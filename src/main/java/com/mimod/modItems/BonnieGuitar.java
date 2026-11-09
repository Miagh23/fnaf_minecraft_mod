package com.mimod.modItems;

import com.mimod.ModSounds;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.sounds.SoundSource;

public class BonnieGuitar extends AxeItem {

    public BonnieGuitar(Item.Properties properties) {
        super(Tiers.IRON,
                properties.attributes(
                        AxeItem.createAttributes(Tiers.IRON, 4, -2.4F)
                )
        );
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity target) {

        if (!player.level().isClientSide()) {
            player.level().playSound(
                    null,
                    player.blockPosition(),
                    ModSounds.BONNIE_GUITAR_SFX.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        return super.onLeftClickEntity(stack, player, target);
    }
}