package com.mimod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class ModSounds {
    // Creamos el registro usando el MOD_ID de tu clase MiMod
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MiMod.MOD_ID);

    // Registramos el sonido del jumpscare

    public static final RegistryObject<SoundEvent> FOXY_JUMPSCARE = registerSound("foxy_jumpscare");

    public static final RegistryObject<SoundEvent> FREDDY_SONG = registerSound("freddy_song");

    public static final RegistryObject<SoundEvent> BONNIE_GUITAR_SFX = registerSound("bonnie_guitar_sfx");

    //SONIDOS DE LA GUITARRA PERSONALIZADA
    public static final RegistryObject<SoundEvent> GUITAR_NOTE_MI_LOW = registerSound("guitar_note_mi_low");
    public static final RegistryObject<SoundEvent> GUITAR_NOTE_LA = registerSound("guitar_note_la");
    public static final RegistryObject<SoundEvent> GUITAR_NOTE_RE = registerSound("guitar_note_re");
    public static final RegistryObject<SoundEvent> GUITAR_NOTE_SOL = registerSound("guitar_note_sol");
    public static final RegistryObject<SoundEvent> GUITAR_NOTE_SI = registerSound("guitar_note_si");
    public static final RegistryObject<SoundEvent> GUITAR_NOTE_MI_HIGH = registerSound("guitar_note_mi_high");

    public static final List<RegistryObject<SoundEvent>> GUITAR_NOTES = List.of(
            GUITAR_NOTE_MI_LOW, GUITAR_NOTE_LA, GUITAR_NOTE_RE, GUITAR_NOTE_SOL, GUITAR_NOTE_SI, GUITAR_NOTE_MI_HIGH);

    public static final RegistryObject<SoundEvent> GUITAR_RIFF_MI_LOW  = registerSound("guitar_riff_mi_low");
    public static final RegistryObject<SoundEvent> GUITAR_RIFF_LA      = registerSound("guitar_riff_la");
    public static final RegistryObject<SoundEvent> GUITAR_RIFF_RE      = registerSound("guitar_riff_re");
    public static final RegistryObject<SoundEvent> GUITAR_RIFF_SOL     = registerSound("guitar_riff_sol");
    public static final RegistryObject<SoundEvent> GUITAR_RIFF_SI      = registerSound("guitar_riff_si");
    public static final RegistryObject<SoundEvent> GUITAR_RIFF_MI_HIGH = registerSound("guitar_riff_mi_high");

    public static final List<RegistryObject<SoundEvent>> GUITAR_RIFFS_LONG = List.of(
            GUITAR_RIFF_MI_LOW, GUITAR_RIFF_LA, GUITAR_RIFF_RE,
            GUITAR_RIFF_SOL, GUITAR_RIFF_SI, GUITAR_RIFF_MI_HIGH
    );

    //Y por cada sonido una linea mas como la de arriba aqui abajo

    public static RegistryObject<SoundEvent> registerSound(String soundId)
    {
      return   SOUND_EVENTS.register(soundId,
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MiMod.MOD_ID, soundId)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }

}

