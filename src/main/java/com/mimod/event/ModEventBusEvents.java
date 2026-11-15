package com.mimod.event;

import com.mimod.MiMod;
import com.mimod.entity.ModEntities;
import com.mimod.keymapping.client.ModKeys;
import com.mimod.entity.client.renderer.bonnie.BonnieRenderer;
import com.mimod.entity.client.renderer.chica.ChicaRenderer;
import com.mimod.entity.client.cupcake.CupcakeRenderer;
import com.mimod.entity.client.renderer.freddy.FreddyRenderer;
import com.mimod.entity.custom.BonnieEntity;
import com.mimod.entity.custom.ChicaEntity;
import com.mimod.entity.custom.FreddyEntity;
import com.mimod.entity.client.renderer.foxy.FoxyRenderer;
import com.mimod.entity.custom.foxy.FoxyEntity;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MiMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    /**
     * Se registran y crean todos las entidades nuevas y sus atributos
     * @param event
     */
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event)
    {
        event.put(ModEntities.CHICA.get(), ChicaEntity.createAttributes().build());
        event.put(ModEntities.BONNIE.get(), BonnieEntity.createAttributes().build());
        event.put(ModEntities.FREDDY.get(), FreddyEntity.createAttributes().build());
        event.put(ModEntities.FOXY.get(), FoxyEntity.createAttributes().build());
    }

    /**
     * registra el render del proyectil - cupcake
     */
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.CUPCAKE.get(), CupcakeRenderer::new);
        event.registerEntityRenderer(ModEntities.CHICA.get(), ChicaRenderer::new);
        event.registerEntityRenderer(ModEntities.BONNIE.get(), BonnieRenderer::new);
        event.registerEntityRenderer(ModEntities.FREDDY.get(), FreddyRenderer::new);
        event.registerEntityRenderer(ModEntities.FOXY.get(), FoxyRenderer::new);
    }

    /**
     * rgstra las notas asociadas con sus teclas
     * @param event
     */
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(ModKeys.NOTE_MI_LOW);
        event.register(ModKeys.NOTE_LA);
        event.register(ModKeys.NOTE_RE);
        event.register(ModKeys.NOTE_SOL);
        event.register(ModKeys.NOTE_SI);
        event.register(ModKeys.NOTE_MI_HIGH);
        event.register(ModKeys.LONG_MODIFIER);
        event.register(ModKeys.SCALE_SIB);
        event.register(ModKeys.SCALE_DO);
        event.register(ModKeys.SCALE_RE);
        event.register(ModKeys.SCALE_FAS);
        event.register(ModKeys.SCALE_SOLS);
        event.register(ModKeys.SCALE_MAYOR_MODIFIER);
    }
}

