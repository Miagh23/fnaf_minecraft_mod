package com.mimod.entity.custom.foxy;

import com.mimod.MiMod;
import com.mimod.ModSounds;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;

public class FoxyEntity extends Monster implements GeoEntity {

    private final AnimatableInstanceCache cache =
            GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE =
            RawAnimation.begin().thenLoop("animation.foxy.showtime");

    private static final RawAnimation WALK =
            RawAnimation.begin().thenLoop("animation.foxy.walk");

    private static final RawAnimation AGGRESSION =
            RawAnimation.begin().thenLoop("animation.foxy.aggression");

    private static final RawAnimation JUMPSCARE =
            RawAnimation.begin().thenLoop("jumpscare");

    private static final int FLEEING_TIME = 40;          // ticks huyendo (20 = 1 s)
    private static final int MAX_ATTACK_COOLDOWN = 40;   // ticks sin poder atacar tras huir
    private static final double FLEEING_SPEED = 1.5;

    private int attackCooldown = 0;

    // Sincronizado servidor -> cliente para que las animaciones lo vean
    private static final EntityDataAccessor<Boolean> FLEEING =
            SynchedEntityData.defineId(FoxyEntity.class, EntityDataSerializers.BOOLEAN);

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FLEEING, false);
    }

    public boolean isFleeing() { return this.entityData.get(FLEEING); }
    public void setFleeing(boolean fleeing) { this.entityData.set(FLEEING, fleeing); }

    public boolean canAttack() { return attackCooldown <= 0; }
    public void setAttackCooldown(int ticks) { this.attackCooldown = ticks; }

    @Override
    public void tick() {
        super.tick();
        if (attackCooldown > 0) attackCooldown--;
    }


    @Override
    public void aiStep() {
        if (this.isSunBurnTick()) {
            this.igniteForSeconds(8.0F);
        }
        super.aiStep();
    }
/*Here's where we specify the custom drop-offs*/
    @Override
    protected void dropCustomDeathLoot(ServerLevel level,
                                       DamageSource source,
                                       boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);

        this.spawnAtLocation(
                new ItemStack(com.mimod.MiMod.FOXY_HOOK.get()));
    }

    public FoxyEntity(EntityType<? extends FoxyEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.FOLLOW_RANGE, 35.0);
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean success = super.doHurtTarget(target);

        // Si el ataque tuvo éxito y ocurre en el servidor, reproducimos el sonido del jumpscare y activa el
        // comportamiento de huir
        if (success && !this.level().isClientSide()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.FOXY_JUMPSCARE.get(),
                    net.minecraft.sounds.SoundSource.HOSTILE, 1.5F, 1.0F
            );

            if (target instanceof Player) {
                this.setFleeing(true);
            }
        }

        return success;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(
                2, new MeleeAttackGoal(this, 1.3, false)
        );

        this.goalSelector.addGoal(
                5, new WaterAvoidingRandomStrollGoal(this, 0.8)
        );

        this.goalSelector.addGoal(
                6, new LookAtPlayerGoal(this, Player.class, 8.0F)
        );

        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.goalSelector.addGoal(1, new FleeingGoal(this, FLEEING_SPEED));
        this.goalSelector.addGoal(2, new FoxyMeleeGoal(this, 10, false));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));

        this.goalSelector.addGoal(3, new RestrictSunGoal(this));
        this.goalSelector.addGoal(4, new FleeSunGoal(this, 1.0));

        this.targetSelector.addGoal(
                2, new NearestAttackableTargetGoal<>(
                        this, Player.class, true
                )
        );
    }

    @Override
    public void registerControllers(
            AnimatableManager.ControllerRegistrar controllers
    ) {
        controllers.add(
                new AnimationController<>(this, "movement_controller", 0,
                        state -> {

                            // Si está atacando, reproducir jumpscare.
                            if (this.swinging) {
                                return state.setAndContinue(JUMPSCARE);
                            }

                            if (this.isFleeing()) {
                                return state.setAndContinue(WALK);
                            }

                            // Si tiene un jugador como objetivo, perseguirlo.
                            if (this.getTarget() instanceof Player
                                    && this.getTarget().isAlive()) {
                                return state.setAndContinue(AGGRESSION);
                            }

                            // Si se está moviendo, caminar.
                            if (state.isMoving()) {
                                return state.setAndContinue(WALK);
                            }

                            // Si no hace nada, idle.
                            return state.setAndContinue(IDLE);
                        })
        );
    }

    /** Ataque cuerpo a cuerpo que no se activa mientras huye o está en cooldown. */
    static class FoxyMeleeGoal extends MeleeAttackGoal {
        private final FoxyEntity foxy;

        public FoxyMeleeGoal(FoxyEntity foxy, double speed, boolean followUnseen) {
            super(foxy, speed, followUnseen);
            this.foxy = foxy;
        }

        @Override
        public boolean canUse() {
            return foxy.canAttack() && !foxy.isFleeing() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return foxy.canAttack() && !foxy.isFleeing() && super.canContinueToUse();
        }
    }

    /** Huye del objetivo durante FLEEING_TIME ticks. */
    static class FleeingGoal extends Goal {
        private final FoxyEntity mob;
        private final double speed;
        private int fleeTicks;

        public FleeingGoal(FoxyEntity mob, double speed) {
            this.mob = mob;
            this.speed = speed;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() { return mob.isFleeing(); }

        @Override
        public boolean canContinueToUse() { return mob.isFleeing() && fleeTicks > 0; }

        @Override
        public void start() {
            fleeTicks = FLEEING_TIME;
            runAway();
        }

        @Override
        public void tick() {
            fleeTicks--;
            if (mob.getNavigation().isDone()) runAway();
        }



        private void runAway() {
            LivingEntity target = mob.getTarget();
            Vec3 from = target != null ? target.position() : mob.position();
            Vec3 pos = DefaultRandomPos.getPosAway(mob, 16, 7, from);
            if (pos != null) {
                mob.getNavigation().moveTo(pos.x, pos.y, pos.z, speed);
            }
        }

        @Override
        public void stop() {
            mob.getNavigation().stop();
            mob.setFleeing(false);
            mob.setAttackCooldown(MAX_ATTACK_COOLDOWN);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}