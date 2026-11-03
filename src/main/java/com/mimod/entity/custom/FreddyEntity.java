package com.mimod.entity.custom;

import com.mimod.Goals.FleeingGoal;
import com.mimod.interfaces.Fleeing;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

/**
 * Se sienta y se pone a cnatar el hoar hoar hoar. Durante ese tiempo le da ceguera y mareo al jugador.
 * El jugador tiene que huir de su rango o matarlo, si no lo hace muere entre terribles circunstancias.
 */
public class FreddyEntity extends Monster implements GeoEntity, Fleeing {

    private static final int MAX_ATTACK_COOLDOWN = 60;
    private static final int FLEEING_TIME = 20;
    private static final double HEALTH = 20;
    private static final double PLAYER_DETECTION_RANGE = 45;
    private static final double MOVEMENT_SPEED = 0.25;
    private static final double FLEEING_SPEED = 1.75f;

    private boolean sitting;
    private boolean fleeing;

    //GECKOLIB: caché que guarda el estado de las animaciones de esta entidad
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    //GECKOLIB: animaciones (los nombres deben coincidir con los del archivo .animation.json)
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.freddyfazbear.idle");
    private static final RawAnimation POSE_ANIM = RawAnimation.begin().thenLoop("animation.freddyfazbear.pose");
    private static final RawAnimation STAGE_ANIM = RawAnimation.begin().thenLoop("animation.freddyfazbear.stage");
    private static final RawAnimation NEW_ANIM = RawAnimation.begin().thenLoop("animation.freddyfazbear.new");
    private static final RawAnimation SING_ANIM = RawAnimation.begin().thenLoop("animation.freddyfazbear.signpose"); //ESTA MAL ESCRITO EN EL JSON TAMBIEN
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.freddyfazbear.walk");
    private static final RawAnimation AGGRESION_ANIM = RawAnimation.begin().thenLoop("animation.freddyfazbear.aggression");
    private static final RawAnimation SIT_ANIM = RawAnimation.begin().thenLoop("animation.freddyfazbear.sit");
    private static final RawAnimation JUMPSCARE_ANIM = RawAnimation.begin().then("jumpscare", Animation.LoopType.PLAY_ONCE);


    protected FreddyEntity(EntityType<? extends Monster> monster, Level world) {
        super(monster, world);
        this.fleeing=false;
        this.sitting=false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    /**
     * Define los atributos básicos de la entidad.
     *
     * @return
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, HEALTH)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, PLAYER_DETECTION_RANGE);
    }

    protected void registerGoals() {
        //flotar en agua
        this.goalSelector.addGoal(0, new FloatGoal(this));

        //evitar el sol
        this.goalSelector.addGoal(1, new RestrictSunGoal(this));
        this.goalSelector.addGoal(2, new FleeSunGoal(this, (double) 1.0F));

        this.goalSelector.addGoal(3,new FleeingGoal(this,FLEEING_SPEED));

        this.goalSelector.addGoal(4, new SitAndSingGoal(this));

        //dar tumbos
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, (double) 1.0F));

        //mirar al jugador
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));

        //mirar alrededor
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));


        //fijar el objetivo en le jugador. SOLO FUNCIONA CON EL JUGADOR
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, true));

    }

    /**
     * spawnea el mob. groupd data es obligatorio devolverlo con las caracteristicas del super por logica de minecraft.
     *
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


    @Override
    public boolean isFleeing() {
        return this.fleeing;
    }

    @Override
    public void setFleeing(boolean fleeing) {
        this.fleeing=fleeing;
    }

    @Override
    public void setAttackCooldown(int ticks) {

    }

    @Override
    public int getFleeingTime() {
        return FLEEING_TIME;
    }

    @Override
    public int getMaxAttackCooldown() {
        return MAX_ATTACK_COOLDOWN;
    }


}
