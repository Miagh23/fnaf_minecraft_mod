package com.mimod.entity;

import com.mimod.entity.custom.FoxyEntity;
import com.mimod.MiMod;
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



    public static final RegistryObject<EntityType<FoxyEntity>> FOXY =
            ENTITY_TYPES.register("foxy", () ->
                    EntityType.Builder.of(FoxyEntity::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.95f).build("foxy"));

    /**
     * Registra el mod en la categoría de entity_types
     * @param eventBus
     */
    public static void register(IEventBus eventBus)
    {
        ENTITY_TYPES.register(eventBus);
    }


}
