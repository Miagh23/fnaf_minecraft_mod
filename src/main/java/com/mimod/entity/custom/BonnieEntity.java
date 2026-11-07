package com.mimod.entity.custom;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;

/**
 * Bonnie está wandering hasta que entras en su rango.
 * Una vez en su rango, se dirige a ti hasta acercarse y matarte.
 * Si le miras, se queda quieto.
 * Puedes salir de su rango mientras le miras y volverá a estar wandering.
 * jumpscare + quita 5 corazones/10.
 * Tiene mucho knockback para simular su agresividad -> te empuja mucho para atrás.
 */
public class BonnieEntity extends Monster implements GeoEntity {

    public BonnieEntity(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    // atributos
    private static final double HEALTH = 20.0;
    private static final double MOVEMENT_SPEED = 0.3;
    private static final double DETECTION_RANGE = 35.0;
    private static final double KNOCKBACK_RESISTANCE = 0.5;

    //TODO: CAMBIAR EN CASO DE QUE SEA NECESARIO; LO ESTOY INTENTANDO CONECTAR PARA PODER VER ALGO AL LANZARLO.
    // GECKOLIB: caché que guarda el estado de las animaciones de esta entidad
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    // GECKOLIB: animaciones
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.bonnie.idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.bonnie.walk");
    private static final RawAnimation AGGRESSION_ANIM = RawAnimation.begin().thenLoop("animation.bonnie.aggression");
    private static final RawAnimation JUMPSCARE_ANIM = RawAnimation.begin().then("jumpscare", Animation.LoopType.PLAY_ONCE);

    // constructor atributos
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, HEALTH)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, DETECTION_RANGE)
                .add(Attributes.ATTACK_DAMAGE, 8.0) // que quite 4 corazones (1 corazón = 2 puntos)
                .add(Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE);
    }

    // metas/comportamiento
    @Override
    protected void registerGoals() {
        super.registerGoals();

        // detecta al jugador
        this.targetSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal<>
                (this, net.minecraft.world.entity.player.Player.class, true));

        // Prioridad 1: la meta personalizada de acecho, congelación por la mirada y ataque (velocidad de persecución: 1.2D)
        this.goalSelector.addGoal(1, new BonnieStalkingGoal(this, 1.2D));

        // que deambule tranquilamente cuando no ve a nadie
        this.goalSelector.addGoal(2, new net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal(this, 1.0D));

        // para que mueva la cabeza de vez en cuando al estar quieto
        this.goalSelector.addGoal(3, new net.minecraft.world.entity.ai.goal.LookAtPlayerGoal(this, net.minecraft.world.entity.player.Player.class, 8.0F));
    }
    /**
     * condición de activación (canUse) --> si tiene un objetivo dentro de su rango
     * se comprueba si la cámara del jugador apunta a Bonnie: si mira --> se para; si no le mira --> avanza para acercarse y matar
     * */
    static class BonnieStalkingGoal extends Goal {
        private final BonnieEntity mob; // referencia a nuestro mob de Bonnie
        private final double speedModifier; // velocidad cuando te persigue

        public BonnieStalkingGoal(BonnieEntity bonnie, double speedModifier) {
            this.mob = bonnie;
            this.speedModifier = speedModifier;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK)); // bloquea el movimiento y la mirada estándar mientras actúa esta meta
        }

        @Override
        public boolean canUse() {
            return this.mob.getTarget() != null; // se activa si tiene un objetivo al que acechar
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true; // obliga a actualizar la lógica en cada tick para que la reacción a la mirada sea instantánea
        }

        @Override
        public void start() {
            super.start();
            this.mob.setAggressive(true); // pone a Bonnie en modo agresivo al empezar la persecución.
        }

        @Override
        public void stop() {
            super.stop();
            this.mob.setAggressive(false); // Quita el modo agresivo si pierde al objetivo.
            this.mob.getNavigation().stop(); // Detiene la navegación.
        }

        @Override
        public void tick() {
            LivingEntity target = this.mob.getTarget(); // obtiene al objetivo
            if (target == null) return; // si no tiene objetivo, salimos

            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F); // que siempre mire al jugador

            // detecta si el jugador está mirando a Bonnie
            boolean playerIsLookingAtMe = isPlayerLookingAtMob(target, this.mob);

            if (playerIsLookingAtMe) {
                this.mob.getNavigation().stop(); // si el jugador le mira, Bonnie se congela en el sitio
                this.mob.setDeltaMovement(0.0D, this.mob.getDeltaMovement().y, 0.0D); // frena su inercia física
                // dejo la y para que si está saltando, pueda actuar la gravedad y no se quede flotando
            } else {
                this.mob.getNavigation().moveTo(target, this.speedModifier);  // si el jugador no le mira, le persigue

                // si está a distancia de ataque cuerpo a cuerpo, ejecuta el golpe y el jumpscare
                if (this.mob.distanceToSqr(target) <= 4.0D) { // distancia de alcance cuerpo a cuerpo
                    this.mob.doHurtTarget(target);
                }
            }
        }

        /**
         * para calcular si el vector de la cámara del jugador apunta hacia Bonnie
         */
        private boolean isPlayerLookingAtMob(LivingEntity player, Mob mob) {
            Vec3 playerViewVector = player.getViewVector(1.0F).normalize(); // obtiene hacia dónde mira el jugador


            // Calcula el vector que va desde los ojos del jugador hasta la posición de Bonnie
            Vec3 vectorToMob = new Vec3(
                    mob.getX() - player.getX(),
                    mob.getEyeY() - player.getEyeY(),
                    mob.getZ() - player.getZ()
            ).normalize();

            // compara ambos vectores mediante el producto escalar (dot product)
            // si el resultado es mayor que un límite (por ejemplo, 0.4), significa que le está mirando
            double dotProduct = playerViewVector.dot(vectorToMob);
            return dotProduct > 0.4D; // : si el cálculo pasa de X, considero que le está mirando a los ojos; si baja, no
            // ajustar dependiendo de si queremos que sea más fácil o difícil congelarle
        }
    }

    /**
     * se ejecuta cuando Bonnie alcanza al jugador y le da el golpe cuerpo a cuerpo
     * 10 puntos de daño (5 corazones) y knockback
     */
    @Override
    public boolean doHurtTarget(Entity target) {
        // ataque base de Minecraft (esto aplica el daño configurado en los atributos: 10.0F)
        boolean hasHurt = super.doHurtTarget(target);

        if (hasHurt && target instanceof LivingEntity livingTarget) {
            // KNOCKBACK --> calcula la dirección opuesta desde Bonnie hacia el jugador y la impulsamos con fuerza
            double knockbackStrength = 0.8D; // a mayor número, más lejos saldrá volando el jugador (TODO: ajustarlo si es necesario)
            double xRatio = Math.sin(this.getYRot() * ((float)Math.PI / 180F));
            double zRatio = -Math.cos(this.getYRot() * ((float)Math.PI / 180F));

            // aplica el knockback
            livingTarget.knockback(knockbackStrength, xRatio, zRatio);

            // TODO: JUMPSCARE
            // aquí podéis poner: this.triggerAnim("attack_controller", "jumpscare");
        }

        return hasHurt;
    }


    //TODO: CAMBIAR AL METER LAS ANIMACIONES
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 5, state -> {
            if (state.isMoving()) {
                if (this.isAggressive()) {
                    return state.setAndContinue(AGGRESSION_ANIM); // Si te persigue, usa la animación de agresión
                }
                return state.setAndContinue(WALK_ANIM); // Si camina normal, usa la de andar
            }
            return state.setAndContinue(IDLE_ANIM); // Si está quieto (wandering / parado), usa el idle
        }));

        controllers.add(new AnimationController<>(this, "attack_controller", 0, state -> PlayState.STOP)
                .triggerableAnim("jumpscare", JUMPSCARE_ANIM));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }



    

}
