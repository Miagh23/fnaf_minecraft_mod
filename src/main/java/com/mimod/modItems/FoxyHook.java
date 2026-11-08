
package com.mimod.modItems;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.entity.LivingEntity;

public class FoxyHook extends SwordItem {

    public FoxyHook(Properties properties) {
        super(
                Tiers.IRON,
                properties.attributes(
                        SwordItem.createAttributes
                                (Tiers.IRON, 5, -2.4f)
                )
        );
    }

    @Override
    public boolean hurtEnemy(
            ItemStack stack,
            LivingEntity target,
            LivingEntity attacker) {

        target.igniteForSeconds(5);

        return super.hurtEnemy(stack, target, attacker);
    }
}
