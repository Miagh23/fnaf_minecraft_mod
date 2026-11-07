package com.mimod.event;

import com.mimod.entity.ModEntities;
import com.mimod.entity.custom.BonnieEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// Registra esta clase para que escuche los eventos globales del juego usando el ID del mod
@Mod.EventBusSubscriber(modid = "mimod")
public class ServerTickHandler {

    // Anotación para que se ejecute automáticamente en cada tick del juego
    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        // Verifica que la ejecución ocurra exclusivamente en el servidor y al final del tick actual
        if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
            // Comprueba que el nivel del evento es un mundo de servidor válido y lo guarda en 'serverLevel'
            if (event.level instanceof ServerLevel serverLevel) {

                // Evalúa si ya es de noche
                if (!serverLevel.isDay()) {
                    // Busca si ya existe alguna instancia viva de Bonnie usando una caja delimitadora gigante (AABB)
                    var existingBonnies = serverLevel.getEntitiesOfClass(
                            BonnieEntity.class,
                            new AABB(-30000, -64, -30000, 30000, 320, 30000)
                    );

                    // Si la lista está vacía, significa que no hay ningún Bonnie vivo en el mundo
                    if (existingBonnies.isEmpty()) {
                        // Recorre a todos los jugadores conectados en el servidor
                        for (ServerPlayer player : serverLevel.players()) {
                            // Genera una distancia segura: al menos 20 bloques lejos, hasta un máximo de 40 bloques
                            int signX = serverLevel.random.nextBoolean() ? 1 : -1;
                            int signZ = serverLevel.random.nextBoolean() ? 1 : -1;
                            int offsetX = signX * (20 + serverLevel.random.nextInt(20)); // Rango: de 20 a 40 bloques en X
                            int offsetZ = signZ * (20 + serverLevel.random.nextInt(20));

                            BlockPos spawnPos = player.blockPosition().offset(offsetX, 0, offsetZ);

                            // Crea una nueva instancia de la entidad Bonnie usando el registro de entidades
                            BonnieEntity bonnie = ModEntities.BONNIE.get().create(serverLevel);

                            // Se asegura de que la entidad se haya creado correctamente en memoria
                            if (bonnie != null) {
                                // Mueve a Bonnie a las coordenadas calculadas (centrándolo en el bloque con +0.5D) con rotación en 0
                                bonnie.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, 0.0F, 0.0F);
                                // Añade oficialmente a Bonnie al mundo físico del servidor
                                serverLevel.addFreshEntity(bonnie);
                                // Imprime el mensaje de depuración en la consola de IntelliJ con las coordenadas de aparición
                                System.out.println("¡FORZADO SPAWN NOCTURNO: Bonnie ha aparecido en " + spawnPos + "!");
                                // Rompe el bucle para que solo aparezca un Bonnie por noche
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}