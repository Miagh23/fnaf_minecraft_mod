package com.mimod.event;

import com.mimod.MiMod;
import com.mimod.entity.ModEntities;
import com.mimod.entity.client.model.chica.ChicaModel;
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
}

