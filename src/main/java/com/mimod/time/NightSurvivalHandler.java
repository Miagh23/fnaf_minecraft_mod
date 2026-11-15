package com.mimod.time;

import com.mimod.MiMod;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// para que Forge sepa que esta clase escucha eventos del servidor
@Mod.EventBusSubscriber(modid = MiMod.MOD_ID)
public class NightSurvivalHandler {

    // se ejecuta automáticamente en cada tick
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // 1. Filtramos para asegurarnos de que estamos en el Servidor (el cliente no otorga logros)
        // y de que el evento ocurre justo al finalizar el tick del jugador para evitar duplicidades
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        // 2. Preguntamos a la clase de WorldClock qué hora y qué día es en el mundo actual
        int currentHour = WorldClock.getHour(serverPlayer.level());
        long currentDay = WorldClock.getDayNumber(serverPlayer.level());

        // 3. Comprobamos la condición de victoria: qe sean exactamente las 6:00 AM y que estemos al menos en el Día 2  para evitar que salte nada más nacer
        if (currentHour == 6 && currentDay >= 2) {
            grantSurviveNightAdvancement(serverPlayer);
        }
    }

    // se encarga de buscar el logro y otorgarlo
    private static void grantSurviveNightAdvancement(ServerPlayer player) {

        // ruta de localización
        ResourceLocation advancementId = ResourceLocation.fromNamespaceAndPath(MiMod.MOD_ID, "survive_night");

        // busca el logro dentro del servidor del juego
        AdvancementHolder advancement = player.server.getAdvancements().get(advancementId);

        // Si el logro existe en el registro...
        if (advancement != null) {
            // obtenemos el progreso actual del jugador en ese logro
            var progress = player.getAdvancements().getOrStartProgress(advancement);

            // Si el jugador TODAVÍA NO lo ha completado...
            if (!progress.isDone()) {
                // Recorremos los criterios pendientes y se los otorgamos para que salte el aviso en pantalla
                for (String criterion : progress.getRemainingCriteria()) {
                    player.getAdvancements().award(advancement, criterion);
                }
            }
        }
    }
}