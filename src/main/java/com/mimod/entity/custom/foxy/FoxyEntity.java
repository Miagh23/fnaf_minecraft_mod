package com.mimod.entity.custom.foxy;

import com.mimod.ModSounds;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

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

    public FoxyEntity(EntityType<? extends FoxyEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.FOLLOW_RANGE, 35.0);
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean success = super.doHurtTarget(target);

        // Si el ataque tuvo éxito y ocurre en el servidor, reproducimos el sonido del jumpscare
        if (success && !this.level().isClientSide()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.FOXY_JUMPSCARE.get(),
                    net.minecraft.sounds.SoundSource.HOSTILE, 1.5F, 1.0F
            );
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

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));

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

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}