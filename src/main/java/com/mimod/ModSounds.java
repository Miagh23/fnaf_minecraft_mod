package com.mimod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    // Creamos el registro usando el MOD_ID de tu clase MiMod
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MiMod.MOD_ID);

    // Registramos el sonido del jumpscare
    public static final RegistryObject<SoundEvent> FOXY_JUMPSCARE = SOUND_EVENTS.register("foxy_jumpscare",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MiMod.MOD_ID, "foxy_jumpscare")));
    //Y por cada sonido una linea mas como la de arriba aqui abajo

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}


