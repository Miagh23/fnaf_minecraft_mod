package com.mimod.event;

import com.mimod.entity.ModEntities;
import com.mimod.entity.custom.BonnieEntity;
import com.mimod.entity.custom.ChicaEntity;
import com.mimod.entity.custom.FreddyEntity;
import com.mimod.entity.custom.foxy.FoxyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "mimod")
public class ServerTickHandler {

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        // Comprobamos que estamos en el servidor y al final del tick
        if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
            if (event.level instanceof ServerLevel serverLevel) {

                // Solo actuamos si es de noche
                if (!serverLevel.isDay()) {

                    // BONNIE: Rango cercano (entre 20 y 40 bloques de distancia)
                    spawnAnimatronicIfNeeded(serverLevel, BonnieEntity.class, ModEntities.BONNIE.get(), 20, 20);

                    // FREDDY: Rango medio-corto (entre 30 y 50 bloques de distancia)
                    spawnAnimatronicIfNeeded(serverLevel, FreddyEntity.class, ModEntities.FREDDY.get(), 30, 20);

                    // CHICA: Rango medio (entre 40 y 60 bloques de distancia)
                    spawnAnimatronicIfNeeded(serverLevel, ChicaEntity.class, ModEntities.CHICA.get(), 40, 20);

                    // FOXY: Rango medio-largo (entre 50 y 70 bloques)
                    spawnAnimatronicIfNeeded(serverLevel, FoxyEntity.class, ModEntities.FOXY.get(), 50, 20);
                }
            }
        }
    }

    /**
     * para comprobar y spawnear cualquier animatronic según su distancia.
     * @param minDistance Distancia mínima obligatoria respecto al jugador.
     * @param randomRange Rango extra aleatorio que se suma a la distancia mínima.
     */
    private static <T extends Mob> void spawnAnimatronicIfNeeded(ServerLevel serverLevel,
                                                                 Class<T> entityClass,
                                                                 net.minecraft.world.entity.EntityType<T> entityType,
                                                                 int minDistance,
                                                                 int randomRange) {

        // Comprueba si ya existe alguna instancia viva de este animatronic específico en el mundo
        var existingEntities = serverLevel.getEntitiesOfClass(
                entityClass,
                new AABB(-30000, -64, -30000, 30000, 320, 30000)
        );

        // Si no hay ninguno vivo, procedemos a spawnear uno cerca de los jugadores --> esto es de cada entidad, es decir, va a haber siempre uno de los 4 animatrónicos
        if (existingEntities.isEmpty()) {
            for (ServerPlayer player : serverLevel.players()) {

                // Decide aleatoriamente la dirección (Norte/Sur y Este/Oeste)
                int signX = serverLevel.random.nextBoolean() ? 1 : -1;
                int signZ = serverLevel.random.nextBoolean() ? 1 : -1;

                // Calcula el desplazamiento basado en los rangos que le pasamos
                int offsetX = signX * (minDistance + serverLevel.random.nextInt(randomRange));
                int offsetZ = signZ * (minDistance + serverLevel.random.nextInt(randomRange));

                BlockPos spawnPos = player.blockPosition().offset(offsetX, 0, offsetZ);

                // Crea la entidad correspondiente
                T animatronic = entityType.create(serverLevel);
                if (animatronic != null) {
                    animatronic.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, 0.0F, 0.0F);
                    serverLevel.addFreshEntity(animatronic);
                    System.out.println("¡FORZADO NOCTURNO: " + entityClass.getSimpleName() + " ha aparecido en " + spawnPos + "!");
                    break; // Rompe el bucle para este animatronic una vez spawnea su ejemplar
                }
            }
        }
    }
}