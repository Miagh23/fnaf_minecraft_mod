package com.mimod.entity.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class ChicaEntity extends Monster implements RangedAttackMob
{
    //El ataque del cupcake

    //indica si está en modo huida (despues de disparar el cupcake)
    private static final int MAX_ATTACK_COOLDOWN = 60;
    private static final int FLEEING_TIME=100;
    private static final double HEALTH=20;
    private static final double RANGE=25;
    private static final double MOVEMENT_SPEED=0.25;
    private int attackCooldown;
    private boolean fleeing;

    //TEMPORAL PARA ANIMACIONES
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;


    /**
     *
     * @param monster el mob de chica en cuestión
     * @param world el mundo
     */
    public ChicaEntity(EntityType<? extends ChicaEntity> monster, Level world) {

        super(monster, world);
        this.fleeing =false;
        this.attackCooldown=MAX_ATTACK_COOLDOWN;
    }

    /**
     * Define los atributos básicos de la entidad.
     * @return
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, HEALTH)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, RANGE);
    }

    /**
     * Define los comportamiento del mob y sus prioridades (cuanto más pequeño el numero, más alta la prioridad).
     * Evita el sol, el agua, mira al jugador y al azar.
     * También elige su objetivo mirando si algo le ha atacado, luego el jugador y luego los golems.
     */
    protected void registerGoals() {
        //flotar en agua
        this.goalSelector.addGoal(0, new FloatGoal(this));

        //evitar el sol
        this.goalSelector.addGoal(1, new RestrictSunGoal(this));
        this.goalSelector.addGoal(2, new FleeSunGoal(this, (double)1.0F));

        //huir
        this.goalSelector.addGoal(3, new FleeingGoal(this, 1.3D));

        //atacar
        this.goalSelector.addGoal(4, new RangedCupcakeAttackGoal(this, 1.0D, 40, 12.0F));

        //dar tumbos
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, (double)1.0F));

        //mirar al jugador
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));

        //mirar alrededor
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        //fijar el objetivo en alguien que le acaba de atacar
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
        //fijar el objetivo en le jugador
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
        //fijar el objetivo en un golem
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
        level.addFreshEntity(cupcake); //ESENCIAL

    }

    /**
     * spawnea el mob. groupd data es obligatorio devolverlo con las caracteristicas del super por logica de minecraft.
     * @param level
     * @param difficulty
     * @param spawnType
     * @param groupData
     * @return
     */
    @Nullable
    @Override

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData groupData) {
        groupData = super.finalizeSpawn(level, difficulty, spawnType, groupData);

        return groupData;
    }

    /**
     * Hace que se queme con el sol
     */
    @Override
    public void aiStep() {

            if (this.isSunBurnTick()) {
                this.igniteForSeconds(8.0F);
            }

            super.aiStep();
    }

    /**
     * Equivalente al Update. Define que pasa en cada tick de tiempo (1s=20ticks) mientras la entidad exista.
     */
    @Override
    public void tick() {
        super.tick();
        if (attackCooldown > 0) attackCooldown--;
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



    /**
     * TEMPORAL PARA ANIMACIONES
     * Añade la animación del mob. Va restando ticks hasta que llega a 0,
     * entonces activa la animación y resetea el contador.
     * Es importante que el contador de ticks coincida con la longitud
     * en ticks de la animación para que se genere un bucle limpio.
     */
    private void setupAnimationStates()
    {
        if(this.idleAnimationTimeout<=0)
        {
            this.idleAnimationTimeout=40;
            this.idleAnimationState.start(this.tickCount);
        }
        else
        {
            --this.idleAnimationTimeout;
        }
    }

    static class RangedCupcakeAttackGoal extends Goal {
        private final ChicaEntity mob;
        private final double speedModifier;
        private final int seeTimeRequired;
        private final float attackRadiusSqr;
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
            return this.mob.getTarget() != null && !this.mob.isFleeing() && mob.canAttack();
        }

        /**
         * Hace un update cada tick y no cada 2 como es el defecto, más precisión para el ataque.
         * @return
         */
        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        /**
         * Se ejecuta cuando se activa el canUse. Arranca el comportamiento y su lógica.
         */
        public void start() {
            super.start();
            this.mob.setAggressive(true);
        }

        @Override
        /**
         * Se ejecuta cuando se desactiva el canUse. Reinicia los contadores y las banderas.
         *
         */
        public void stop() {
            super.stop();
            this.mob.setAggressive(false);
           this.seeTime=0; //reinicia el contador de visión

        }

        /**
         * Determina como funciona este comportamiento. El Update del comportamiento (Goal) de atacar en sí,
         * solo funciona mientras se haya activado el metodo start de este comportamiento.
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

                if (!(distanceToTarget > (double) this.attackRadiusSqr) && this.seeTime >= seeTimeRequired) {
                    this.mob.getNavigation().stop();

                } else {
                    this.mob.getNavigation().moveTo(target, this.speedModifier);
                }

                this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);


                if (hasLineOfSight && distanceToTarget <= this.attackRadiusSqr && this.seeTime >= this.seeTimeRequired) {


                    this.mob.performRangedAttack(target, 3);
                    this.mob.setFleeing(true);


                }
            }

        }
    }

    /**
     * El comportamiento de huir. Tiene parametros del mob, su velocidad y el tiempo en ticks de huida restante. (20ticks=1s)
     */
    static class FleeingGoal extends Goal {
        private final ChicaEntity mob;
        private final double speed;
        private int fleeTicks;

        public FleeingGoal(ChicaEntity mob, double speed) {
            this.mob = mob;
            this.speed = speed;

            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        /**
         * Solo se activa cuando el mob está huyendo.
         * @return
         */
        @Override
        public boolean canUse() {
            return mob.isFleeing();
        }

        /**
         * Solo sigue activo mientras este en modo huir y mientras le quede tiempo de huida.
         * @return
         */
        @Override
        public boolean canContinueToUse() {
            return mob.isFleeing() && fleeTicks > 0;
        }

        /**
         * Cuando arranca, inicia el temporizador y ejecuta el método runAway()
         */
        @Override
        public void start() {
            fleeTicks = mob.FLEEING_TIME;
            runAway();
        }

        /**
         * En cada tick, mientras este comportamiento(goal) este activo, se reduce el tiempo de huida
         * y se llama al método runAway() si fuera necesario.
         */
        @Override
        public void tick() {
            fleeTicks--;
            // Si llegó al destino antes de tiempo, busca otro punto más lejos
            if (mob.getNavigation().isDone()) {
                runAway();
            }
        }

        /**
         * Detecta a su objetivo y huye en una dirección aleatoria de él. Si no puede seguir huyendo en esa dirección, vuelva a calcularla.
         */
        private void runAway() {
            LivingEntity target = mob.getTarget();
            Vec3 from = target != null ? target.position() : mob.position();
                                                 //mob, horizontalRange, verticalRange, positionToAvoid
            Vec3 pos = DefaultRandomPos.getPosAway(mob, 16, 7, from);
            if (pos != null) {
                mob.getNavigation().moveTo(pos.x, pos.y, pos.z, speed);
            }
        }

        /**
         * Cuando acaba este comportamiento, se cambia la bandera
         * y se activa el cooldown de ataque (así se evita que ataque instantáneamente despues de huir).
         */
        @Override
        public void stop() {
            mob.getNavigation().stop();
            mob.setFleeing(false);
            mob.setAttackCooldown(mob.MAX_ATTACK_COOLDOWN);
        }
    }

    /**
     * Hereda de la bola de fuego del ghast, no deja fuego y tiene mayor potencia de explosión.
     */
        static class Cupcake extends LargeFireball{


        private static final int EXPLOSION_POWER = 3; //explosion de creeper=3

            public Cupcake(Level level, LivingEntity owner, Vec3 direction) {

                super(level, owner, direction, EXPLOSION_POWER);
            }

        /**
         * Define que hace cuando golpea
         * @param result
         */
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

        /**
         * Indica que el proyectil, es decir el cupcake, no se puede dañar.
         * @param source
         * @param amount
         * @return
         */
            @Override
            public boolean hurt(DamageSource source, float amount) {
                 return false;
        }
        }


    public boolean isFleeing() {
        return fleeing;
    }

    public void setFleeing(boolean fleeing) {
        this.fleeing = fleeing;
    }


    public boolean canAttack() { return attackCooldown <= 0; }
    public void setAttackCooldown(int ticks) { this.attackCooldown = ticks; }

}







