package com.mimod.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ChicaEntity extends Monster
{

    private final RangedCupcakeAttackGoal  cupcakeGoal = new RangedCupcakeAttackGoal();

    public ChicaEntity(EntityType<? extends Monster> monster, Level world) {
        super(monster, world);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(3, new FleeSunGoal(this, (double)1.0F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, (double)1.0F));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));

    }

    static class RangedCupcakeAttackGoal{

    }


    /**
     * Hereda de la bola de fuego del ghast, no deja fuego y tiene mayor potencia de explosión.
     */
        static class Cupcake extends LargeFireball{


            public Cupcake(Level level, LivingEntity owner, Vec3 direction, int power) {
                super(level, owner, direction, power);
            }

            @Override
            protected void onHit(HitResult result) {
                if (!this.level().isClientSide) {
                    // El último parámetro 'false' = SIN fuego
                    this.level().explode(
                            this,
                            this.getX(), this.getY(), this.getZ(),
                            3.0F,                              // potencia (3 = creeper)
                            false,                             // false = no genera fuego
                            Level.ExplosionInteraction.TNT     // indica como interactua con el mundo y que puede destruir/afectar
                    );
                    this.discard();
                }
            }
        }

}






