package com.mimod.entity.custom;

import com.mimod.Goals.FleeingGoal;
import com.mimod.ModSounds;
import com.mimod.interfaces.Fleeing;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
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
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;


import javax.annotation.Nullable;
import java.util.EnumSet;

/**
 * Primero le provoca ceguera y nausea al jugador, además de que sale el sonido.
 * Despues huye ligeramente del jugador.
 * Se sienta y se pone a cantar el hoar hoar hoar.
 * El jugador tiene que huir de su rango o matarlo, si no lo hace muere entre terribles circunstancias.
 */
public class FreddyEntity extends Monster implements GeoEntity, Fleeing {

    private static final int MAX_ATTACK_COOLDOWN = 60;
    private static final int FLEEING_TIME = 20;
    private static final double HEALTH = 20;
    private static final double PLAYER_DETECTION_RANGE = 45;
    private static final double MOVEMENT_SPEED = 0.25;
    private static final double FLEEING_SPEED = 1.75f;
    private static final int BLIND_COOLDOWN_TIME = 20 * 20;

    // private boolean sitting pero que se guarde en servidor;
    private static final EntityDataAccessor<Boolean> DATA_SITTING =
            SynchedEntityData.defineId(FreddyEntity.class, EntityDataSerializers.BOOLEAN);
    //private boolean fleeing pero que se guarde en servidor;
    private static final EntityDataAccessor<Boolean> DATA_FLEEING =
            SynchedEntityData.defineId(FreddyEntity.class, EntityDataSerializers.BOOLEAN);

    private int blindCooldown;

    private SitAndSingGoal singGoal; //referencia al goal de SitAndSing para llamarlo desde la propia entidad

    //GECKOLIB: caché que guarda el estado de las animaciones de esta entidad
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    //GECKOLIB: animaciones (los nombres deben coincidir con los del archivo .animation.json)
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.freddyfazbear.idle");
    private static final RawAnimation POSE_ANIM = RawAnimation.begin().thenLoop("animation.freddyfazbear.pose");
    private static final RawAnimation STAGE_ANIM = RawAnimation.begin().thenLoop("animation.freddyfazbear.stage");
    private static final RawAnimation NEW_ANIM = RawAnimation.begin().thenLoop("animation.freddyfazbear.new");
    private static final RawAnimation SING_ANIM = RawAnimation.begin().thenLoop ("animation.freddyfazbear.signpose"); //ESTA MAL ESCRITO EN EL JSON TAMBIEN
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.freddyfazbear.walk");
    private static final RawAnimation AGGRESSION_ANIM = RawAnimation.begin().thenLoop("animation.freddyfazbear.aggression");
    private static final RawAnimation SIT_ANIM = RawAnimation.begin().thenLoop("animation.freddyfazbear.sit");
    private static final RawAnimation JUMPSCARE_ANIM = RawAnimation.begin().then("jumpscare", Animation.LoopType.PLAY_ONCE);

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SITTING, false);
        builder.define(DATA_FLEEING, false);
    }
    public FreddyEntity(EntityType<? extends Monster> monster, Level world) {
        super(monster, world);
        //this.fleeing=false;
        //this.sitting=false;
        this.blindCooldown=10;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

        controllerRegistrar.add(new AnimationController<>(this, "movement", 5, state -> {


            if (this.isSitting()) {
                // Agresiva: siempre animación de agresión, se mueva o no (strafing incluido)
                return state.setAndContinue(SIT_ANIM);
            }
            if (this.isFleeing()) {
                return state.setAndContinue(AGGRESSION_ANIM);
            }
            if(state.isMoving())
                return state.setAndContinue(WALK_ANIM);

            return state.setAndContinue(STAGE_ANIM);
        }));

        controllerRegistrar.add(new AnimationController<>(this, "attack_controller", 0, state -> PlayState.STOP)
                .triggerableAnim("jumpscare", JUMPSCARE_ANIM));

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

    @Override
    protected void registerGoals() {
        //flotar en agua
        this.goalSelector.addGoal(0, new FloatGoal(this));

        //evitar el sol
        this.goalSelector.addGoal(1, new RestrictSunGoal(this));
        this.goalSelector.addGoal(2, new FleeSunGoal(this, (double) 1.0F));

        this.goalSelector.addGoal(3, new BlindPlayerGoal(this));

        this.goalSelector.addGoal(4,new FleeingGoal(this,FLEEING_SPEED));

        this.singGoal= new SitAndSingGoal(this); //Se guarda la referencia para llamarlo desde el die de la entidad
        this.goalSelector.addGoal(5,singGoal);

        //dar tumbos
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, (double) 1.0F));

        //mirar al jugador
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));

        //mirar alrededor
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));


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

        if (blindCooldown > 0) blindCooldown--;

        super.aiStep();
    }

    /**
     * Elimina los efectos de freddy si este muere.
     * @param source
     */
    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (singGoal != null) singGoal.cleanup(false); // guarda el goal en un campo al registrarlo
    }

    /**
     * Elimina los efectos de freddy si despawnea por chuncks, el jugador se desconecta del server, etc.
     * Para prevenir bugs.
     * @param reason
     */
    @Override
    public void remove(Entity.RemovalReason reason) {
        if (singGoal != null) singGoal.cleanup(false);
        super.remove(reason);
    }

    @Override
    public boolean isFleeing() {
        return this.entityData.get(DATA_FLEEING);
    }

    @Override
    public void setFleeing(boolean fleeing) {
        this.entityData.set(DATA_FLEEING, fleeing);
    }

    /**
     * Usaremos este método para cambiar el sitting a true.
     * @param ticks es lo que devuelve getMaxAttackCooldown
     */
    @Override
    public void setAttackCooldown(int ticks)
    {
        this.entityData.set(DATA_SITTING, ticks > 0);
    }

    @Override
    public int getFleeingTime() {
        return FLEEING_TIME;
    }

    @Override
    public int getMaxAttackCooldown() {
        return MAX_ATTACK_COOLDOWN;
    }

    private boolean isSitting() {
        return this.entityData.get(DATA_SITTING);
    }


    public boolean canBlind() { return blindCooldown <= 0; }
    public void startBlindCooldown(int ticks) { blindCooldown = ticks; }

    static class BlindPlayerGoal extends Goal{

        private FreddyEntity mob;

        BlindPlayerGoal(FreddyEntity mob)
        {
            this.mob=mob;
        }

        /**
         * ciega si detecta a un jugador y ni está huyendo o sentado. También que no este en cooldown.
         * @return
         */
        @Override
        public boolean canUse() {
            return this.mob.getTarget() != null &&
                    !this.mob.isFleeing() &&
                    !mob.isSitting() &&
                    this.mob.canBlind();
        }

        /**
         * Solo se ejecuta una vez asi que no hace falta el tick.
         * Al cambiarse el fleeing a true, no se vuelve a ejecutar y se pasa a fleeingGoal.
         */
        @Override
        public void start() {
            LivingEntity target = mob.getTarget();
            target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, MobEffectInstance.INFINITE_DURATION, 0, false, false));
            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, MobEffectInstance.INFINITE_DURATION, 0, false, false));
            mob.setFleeing(true);
        }
    }

    static class SitAndSingGoal extends Goal
    {

        // Debe ser > 1, si no el jugador ya empieza "fuera" del radio y el goal termina al instante
        public static double RADIUS_MODIFIER = 1.5D;

        private static final int COUNTDOWN_TICKS = 20 * 10;   // tiempo hasta matar
        private static final double SONG_PITCH =0.8f; //menos de 1 es slowed, más es nightcore
        private static final double SONG_SECONDS = 6.1D; // duración exacta del .ogg

        // duración real de tu .ogg. Se divide entre el pitch ya que afecta la velocidad y se multiplica por los 20 ticks.
        private static final int SONG_LENGTH_TICKS = (int) Math.round(SONG_SECONDS * 20.0D / SONG_PITCH);



        private FreddyEntity mob;
        private LivingEntity player;
        private double maxDistSqr;
        private int countdown;
        private int songTimer;
        private float songRange;

        SitAndSingGoal(FreddyEntity mob)
        {
            this.mob=mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }
        @Override
        public boolean canUse() {
            return this.mob.getTarget() != null && mob.isSitting();
        }

        @Override
        public boolean canContinueToUse() {
            // Usamos 'player' guardado, NO getTarget(): el targetSelector puede poner el target
            // a null cuando el jugador se aleja de follow_range, y ahí se nos escaparía sin limpiar.
            return mob.isAlive()
                    && player != null
                    && player.isAlive()
                    && mob.distanceToSqr(player) <= maxDistSqr;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true; // para que el contador cuente tick a tick
        }


        @Override
        public void start() {
            player = mob.getTarget();
            // distanceToSqr devuelve distancia al cuadrado, por eso el modificador también va al cuadrado
            maxDistSqr = mob.distanceToSqr(player) * RADIUS_MODIFIER * RADIUS_MODIFIER;
            countdown = COUNTDOWN_TICKS;
            songTimer = 0;

            /*Calcula el volumen y rango de la cancion en funcion del rango de escape para que coincida.
             Cada unidad de "volumen" corresponde a 16 bloques. El mínimo es 1.0, menos de 1 solo baja el volumen y no el rango.
             Más de 1 no afecta al volumen, solo al rango y son 16 bloques más por unidad.
             Se hace la raíz cuadrada de la distancia, se divide entre los 16 bloques y si es >1 se elige ese volumen.
             */
            songRange= (float) Math.max(1.0D, Math.sqrt(maxDistSqr) / 16.0D);
            mob.getNavigation().stop();
        }

        @Override
        public void tick() {
            mob.getLookControl().setLookAt(player, 30.0F, 30.0F);


//            // algo asi donde estaria la ruta del song
            if (songTimer-- <= 0) {
                mob.level().playSound((Player) null, mob.getX(), mob.getY(), mob.getZ(),
                        ModSounds.FREDDY_SONG.get(), SoundSource.HOSTILE, songRange, (float) SONG_PITCH);
                songTimer = SONG_LENGTH_TICKS;
            }

            if (--countdown <= 0) {
                // genericKill ignora creativo, armadura y tótem de la inmortalidad
                player.hurt(mob.damageSources().genericKill(), Float.MAX_VALUE);
            }
        }

        /**
         * Si se llama porque el jugador escapa se vuelve true, si no false
         */
        @Override
        public void stop() {
            boolean escaped = mob.isAlive()
                    && player != null
                    && player.isAlive()
                    && mob.distanceToSqr(player) > maxDistSqr;
            cleanup(escaped);
        }

        /** Pública para llamarla también desde FreddyEntity#die(), porque stop() no se invoca si el mob muere. */
        public void cleanup(boolean escaped) {
            if (mob.level() instanceof ServerLevel sl) {
              ClientboundStopSoundPacket pkt = new ClientboundStopSoundPacket(
                       ModSounds.FREDDY_SONG.get().getLocation(), SoundSource.HOSTILE);
               for (ServerPlayer p : sl.players()) p.connection.send(pkt);
            }
            if (player != null) {
                player.removeEffect(MobEffects.CONFUSION);
                player.removeEffect(MobEffects.BLINDNESS);
                player = null;
            }
            mob.setAttackCooldown(-1); //-1 indica false para el sitting si no, canUse() sería true otra vez y reiniciaría el goal
            if (escaped) mob.startBlindCooldown(BLIND_COOLDOWN_TIME);
        }

    }




}
