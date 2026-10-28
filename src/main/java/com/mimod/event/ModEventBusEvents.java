package com.mimod.event;

import com.mimod.MiMod;
import com.mimod.entity.ModEntities;
import com.mimod.entity.client.chica.ChicaModel;
import com.mimod.entity.client.cupcake.CupcakeRenderer;
import com.mimod.entity.client.rhino.RhinoModel;
import com.mimod.entity.custom.ChicaEntity;
import com.mimod.entity.custom.RhinoEntity;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MiMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(RhinoModel.LAYER_LOCATION, RhinoModel::createBodyLayer);
        event.registerLayerDefinition(ChicaModel.LAYER_LOCATION, ChicaModel::createBodyLayer);//Falta crear el modelo de chica
    }

    /**
     * Se registran y crean todos las entidades nuevas y sus atributos
     * @param event
     */
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event)
    {
        event.put(ModEntities.RHINO.get(), RhinoEntity.createAttributes().build());
        event.put(ModEntities.CHICA.get(), ChicaEntity.createAttributes().build());
    }

    /**
     * registra el render del proyectil - cupcake
     */
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.CUPCAKE.get(), CupcakeRenderer::new);
    }
}
