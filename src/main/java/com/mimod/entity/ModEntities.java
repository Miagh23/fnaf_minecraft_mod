package com.mimod.entity;

import com.mimod.MiMod;
import com.mimod.entity.custom.ChicaEntity;
import com.mimod.entity.custom.RhinoEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    /**
     * Crea la categoría de ENTITY_TYPES en el mod
     */
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES=
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MiMod.MOD_ID);
    /**
     * Usa la categoría ENTITY_TYPES para crear/registrar el mob como RHINO y define su tamaño.
     */
    public static final RegistryObject<EntityType<RhinoEntity>> RHINO =
            ENTITY_TYPES.register("rinoceronte",()->
                    EntityType.Builder.of(RhinoEntity::new, MobCategory.CREATURE)
                            .sized(1.5f, 1.5f).build("rinoceronte"));


    public static final RegistryObject<EntityType<ChicaEntity>> CHICA =
            ENTITY_TYPES.register("chica", () ->
                    EntityType.Builder.of(ChicaEntity::new, MobCategory.MONSTER)
                            //el primer numero es el ancho y largo (siempre es un cuadrado), el segundo la altura
                            .sized(0.6F, 1.8F)//el jugador y zombie tiene esta escala.
                            .build("chica"));

    public static final RegistryObject<EntityType<ChicaEntity.Cupcake>> CUPCAKE =
            ENTITY_TYPES.register("cupcake", () ->
                    EntityType.Builder.<ChicaEntity.Cupcake>of(ChicaEntity.Cupcake::new, MobCategory.MISC) // = miscellaneous -> no son seres vivos ni mobs tradicionales
                            .sized(0.25F, 0.25F)
                            .build("cupcake"));


    /**
     * Registra el mod en la categoría de entity_types
     * @param eventBus
     */
    public static void register(IEventBus eventBus)
    {
        ENTITY_TYPES.register(eventBus);
    }


}
