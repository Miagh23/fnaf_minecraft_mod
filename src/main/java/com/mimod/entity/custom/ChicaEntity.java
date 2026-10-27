package com.mimod.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class ChicaEntity extends Monster implements RangedAttackMob
{
    //El ataque del cupcake
    private final RangedCupcakeAttackGoal  cupcakeGoal =
            new RangedCupcakeAttackGoal(this,1f,20,20);
    //indica si ha disparado ya el cupcake
    private boolean hasShot;

    /**
     *
     * @param monster el mob de chica en cuestión
     * @param world el mundo
     */
    public ChicaEntity(EntityType<? extends Monster> monster, Level world) {

        super(monster, world);
        this.hasShot =false;
    }

    /**
     * Define el comportamiento del mob y sus prioridades (cuanto más pequeño el numero, más alta la prioridad).
     * Evita el sol, el agua, mira al jugador y al azar.
     * También elige su objetivo mirando si algo le ha atacado, luego el jugador y luego los golems.
     */
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

    @Override
    public void performRangedAttack(LivingEntity p_33317_, float p_33318_) {

    }



    static class RangedCupcakeAttackGoal extends Goal
    {
        private final ChicaEntity mob;
        private final double speedModifier;
        private int attackIntervalMin;
        private final float attackRadiusSqr;
        private int attackTime = -1;
        private int seeTime;

        public RangedCupcakeAttackGoal(ChicaEntity chica, double speedModifier, int attackIntervalMin, float attackRadius) {
            this.mob = chica;
            this.speedModifier = speedModifier;
            this.attackIntervalMin = attackIntervalMin;
            this.attackRadiusSqr = attackRadius * attackRadius; //POR RAZONES TRIGONOMETRICAS SE USAN CUADRADOS PARA ASI EVITAR CALCULAR RAICES
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        /**
         * Dispara solo si tiene un target y si no ha disparado ya.
         */
        public boolean canUse() {
            return this.mob.getTarget() == null ? false : !this.mob.hasShot();
        }

        @Override
        /**
         * Se ejecuta cuando se activa el canUse.
         */
        public void start() {
            super.start();
            this.mob.setAggressive(true);
        }

        @Override
        /**
         * Se ejecuta cuando se desactiva el canUse. Reinicia los contadores y las banderas.
         * Pd: Ahora mismo, no funcionaria correctamente ya que hasShot entra en un bucle con el goal
         */
        public void stop() {
            super.stop();
            this.mob.setAggressive(false);
            this.seeTime = 0;
            this.attackTime = -5;
            this.mob.setHasShot(false);

        }
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

    public boolean hasShot() {
        return hasShot;
    }

    public void setHasShot(boolean hasShot) {
        this.hasShot = hasShot;
    }

}






