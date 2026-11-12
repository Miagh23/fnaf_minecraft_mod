package com.mimod.event;

import com.mimod.entity.custom.BonnieEntity;
import com.mimod.entity.custom.ChicaEntity;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "mimod") // Sin bus = MOD, escucha el bus general del juego
public class ModAdvancementHandler {

    // Se ejecuta cuando el jugador entra al mundo para desbloquear la pestaña raíz de FNAF
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            grantAdvancement(player, "mimod", "root");
        }
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        // Cuando muere Bonnie a manos de un jugador
        if (event.getEntity() instanceof BonnieEntity && event.getSource().getEntity() instanceof ServerPlayer player) {
            grantAdvancement(player, "mimod", "kill_bonnie");
        }

    }

    // busca el logro en los archivos y se lo otorga al jugador
    private static void grantAdvancement(ServerPlayer player, String modId, String advancementName) {
        // Crea la ruta de identificación del logro buscando el namespace (ej: "mimod") y el nombre del archivo json (ej: "kill_bonnie")
        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(modId, advancementName);
        // Busca en el servidor el logro que coincida con esa ruta
        AdvancementHolder advancement = player.server.getAdvancements().get(loc);
        // Si el logro existe en los archivos del mod y se encuentra correctamente...
        if (advancement != null) {
            // ...se lo otorga oficialmente al jugador usando el criterio "requirement" definido en el JSON
            player.getAdvancements().award(advancement, "requirement");
        }
    }
}