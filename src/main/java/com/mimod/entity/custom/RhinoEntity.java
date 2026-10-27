package com.mimod.entity.custom;

import com.mimod.entity.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;


public class RhinoEntity extends Animal {

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
     public RhinoEntity(EntityType<? extends Animal> pEntityType, Level pLevel)
     {
        super(pEntityType, pLevel);
     }

     //Para dalre comida a un animal y que procree
    @Override
    public boolean isFood(ItemStack itemStack) {
        return false;
    }


    /**
    los goals es lo que define el comportamiento que va a tener el mob y es esencial.
    El primer número es el orden de prioridad que tendrá el mob. El segundo parámetro es el tipo de objetivo que tendrá.
     **/
    @Override
     protected void registerGoals()
     {
         this.goalSelector.addGoal(0, new FloatGoal(this));
         this.goalSelector.addGoal(1, new PanicGoal(this, 2.0));
         this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));

         this.goalSelector.addGoal(3, new FollowParentGoal(this,1.25));
         this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0));
         this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class,6.0F));
         this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
     }

     /**
     Los atributos cambian las características del mob.
     Algunos son obligatorios siempre, otros dependen de los objetivos y
     tipo de entidad que sea
      **/
     public static AttributeSupplier.Builder createAttributes()
     {
         return Animal.createLivingAttributes()
                 .add(Attributes.MAX_HEALTH, 30D)
                 .add(Attributes.MOVEMENT_SPEED, 0.35D)
                 .add(Attributes.FOLLOW_RANGE, 24D);
     }

    @Override
    public boolean alwaysAccepts() {
        return super.alwaysAccepts();
    }

    //Devuelve un AgeableMob aka el bebe de lo que sea este mod que tendras que definir y eso.
    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {

         return ModEntities.RHINO.get().create(serverLevel);
    }

    /**
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

    /**
     * Se define como se procesan los ticks y se define que las animaciones
     * solo se ejecuten en el lado del cliente (es innecesario en el servidor)
     */
    @Override
    public void tick()
    {
        super.tick();

        if(this.level().isClientSide()) {
            this.setupAnimationStates();
        }

    }


}
