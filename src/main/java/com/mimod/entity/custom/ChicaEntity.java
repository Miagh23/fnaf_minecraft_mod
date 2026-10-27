package com.mimod.entity.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
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
    public void performRangedAttack(LivingEntity target, float velocity) {

        //el mundo
        Level level = this.level();
        //solo lo ejecuta en el server
        if (level.isClientSide) return;

        // Origen: un poco por delante de la cara de Chica
        Vec3 view = this.getViewVector(1.0F);
        double startX = this.getX() + view.x * 2.0D;
        double startY = this.getY(0.5D) + 0.5D;
        double startZ = this.getZ() + view.z * 2.0D;

        // Vector hacia el centro del cuerpo del objetivo
        double dx = target.getX() - startX;
        double dy = target.getY(0.5D) - startY;
        double dz = target.getZ() - startZ;

        Vec3 dir = new Vec3(dx,dy,dz);

        //el sonido: a que jugadores excluye, posicion, sonido, categoría, volumen y tono.
        level.playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.GHAST_SHOOT, SoundSource.HOSTILE, 10.0F, 1.0F);

        Cupcake cupcake = new Cupcake(level,this,dir.normalize());
        cupcake.setPos(startX,startY,startZ);
        level.addFreshEntity(cupcake);

    }

    @Override
    /**
     * Es inmune a las explosiones provocadas por ella misma.
     */
    public boolean hurt(DamageSource source, float amount) {
        // Inmune a explosiones causadas por ella misma
        if (source.is(DamageTypeTags.IS_EXPLOSION) && source.getEntity() == this) {
            return false;
        }
        return super.hurt(source, amount);
    }



    static class RangedCupcakeAttackGoal extends Goal {
        private final ChicaEntity mob;
        private final double speedModifier;
        private final int seeTimeRequired;
        private final float attackRadiusSqr;
        private int attackTime = -1;
        private int seeTime;

        public RangedCupcakeAttackGoal(ChicaEntity chica, double speedModifier, int seeTimeRequired, float attackRadius) {
            this.mob = chica;
            this.speedModifier = speedModifier;
            this.seeTimeRequired = seeTimeRequired;
            this.attackRadiusSqr = attackRadius * attackRadius; //POR RAZONES TRIGONOMETRICAS SE USAN CUADRADOS PARA ASI EVITAR CALCULAR RAICES
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        /**
         * Dispara solo si tiene un target y si no ha disparado ya.
         */
        public boolean canUse() {
            return this.mob.getTarget() != null && !this.mob.hasShot();
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

        /**
         * Determina cuando realizar cada accion. Si hay un objetivo y le puede ver va subiendo el contador
         */
        @Override
        public void tick() {
            LivingEntity target = this.mob.getTarget();
            if (target != null) {
                double distanceToTarget = this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
                boolean hasLineOfSight = this.mob.getSensing().hasLineOfSight(target);
                boolean aux1 = this.seeTime > 0;
                if (hasLineOfSight != aux1) {
                    this.seeTime = 0;
                }

                if (hasLineOfSight) {
                    this.seeTime++;
                } else {
                    this.seeTime--;
                }

                if (!(distanceToTarget > (double) this.attackRadiusSqr) && this.seeTime >= 20) {
                    this.mob.getNavigation().stop();

                } else {
                    this.mob.getNavigation().moveTo(target, this.speedModifier);
                }

                this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);


                if (hasLineOfSight && distanceToTarget <= this.attackRadiusSqr && this.seeTime >= this.seeTimeRequired) {


                    this.mob.performRangedAttack(target, 3);


                }
            }

        }
    }


    /**
     * Hereda de la bola de fuego del ghast, no deja fuego y tiene mayor potencia de explosión.
     */
        static class Cupcake extends LargeFireball{


        private static final int EXPLOSION_POWER = 3;

            public Cupcake(Level level, LivingEntity owner, Vec3 direction) {

                super(level, owner, direction, EXPLOSION_POWER);
            }

            @Override
            protected void onHit(HitResult result) {
                if (!this.level().isClientSide) {
                    // El último parámetro 'false' = SIN fuego
                    this.level().explode(
                            this,
                            this.getX(), this.getY(), this.getZ(),
                            EXPLOSION_POWER,                              // potencia (3 = creeper)
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







