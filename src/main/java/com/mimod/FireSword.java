
package com.mimod;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Tiers;

/**
 * Este comentario simboliza la unión entre un grupo de desarrolladores con una pasión y misión, reunir a los animatróncios para poder volverse los verdaderos fnaf nights at freddys
 */
public class FireSword extends SwordItem
{
    public FireSword(Properties properties)
    {
        super(Tiers.DIAMOND, new Item.Properties().attributes(
                SwordItem.createAttributes(Tiers.DIAMOND,5,-2.4f)
        ));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
        target.igniteForSeconds(5);
        return super.hurtEnemy(stack,target,attacker);


    }

}

