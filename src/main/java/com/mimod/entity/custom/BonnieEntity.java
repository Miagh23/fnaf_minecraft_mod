package com.mimod.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/**
 * Bonnie está wandering hasta que entras en su rango.
 * Una vez en su rango, se dirige a ti hasta acercarse y matarte.
 * Si le miras, se queda quieto.
 * Puedes salir de su rango mientras le miras y volverá a estar wandering.
 * jumpscare + quita 5 corazones/10.
 * Tiene mucho knockback para simular su agresividad -> te empuja mucho para atrás.
 */
public class BonnieEntity extends Monster {

    protected BonnieEntity(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }
}
