
package com.mimod.event;

import com.mimod.entity.ModEntities;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;

//We import the class where our mask is declared at, that being the MiMod Java class
import com.mimod.MiMod;

@Mod.EventBusSubscriber(
        modid = "mimod",
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class AnimatronicMaskHandler {

    // We check whether the player is wearing Freddy's mask or not.
    private static boolean isWearingFreddyMask(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD)
                .is(MiMod.FREDDY_MASK.get());
    }

    // Checks whether the entity is Freddy, Chica or Bonnie.
    private static boolean isTargetAnimatronic(Entity entity) {
        return entity.getType() == ModEntities.FREDDY.get()
                || entity.getType() == ModEntities.CHICA.get()
                || entity.getType() == ModEntities.BONNIE.get();
    }

    // Prevents the animatronics from selecting a masked player.
    @SubscribeEvent
    public static void onTargetChange(LivingChangeTargetEvent event) {

        if (!isTargetAnimatronic(event.getEntity())) {
            return;
        }

        LivingEntity newTarget = event.getNewTarget();

        if (newTarget instanceof Player player
                && isWearingFreddyMask(player)) {
            event.setNewTarget(null);
        }
    }

    // Stops an existing chase if the player puts on the mask.
    @SubscribeEvent
    public static void onLivingTick(LivingTickEvent event) {

        LivingEntity entity = event.getEntity();

        if (!(entity instanceof Mob mob)) {
            return;
        }

        if (!isTargetAnimatronic(mob)) {
            return;
        }

        if (mob.getTarget() instanceof Player player
                && isWearingFreddyMask(player)) {
            mob.setTarget(null);
        }
    }
}