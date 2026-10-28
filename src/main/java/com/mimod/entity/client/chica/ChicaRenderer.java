package com.mimod.entity.client.chica;

import com.mimod.MiMod;
import com.mimod.entity.client.rhino.RhinoModel;
import com.mimod.entity.custom.ChicaEntity;
import com.mimod.entity.custom.RhinoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

//TEMPORAL PARA PROBAR LOGICA
public class ChicaRenderer extends MobRenderer<ChicaEntity, ChicaModel<ChicaEntity>> {

    /**
     * Renderiza el mob. Pasa el contexto, un modelo que crea con su capa y el shadowRadius.
     * @param pContext= El contexto que recibe de MobRenderer
     */

    public ChicaRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new ChicaModel<>(pContext.bakeLayer(ChicaModel.LAYER_LOCATION)),0.85f);
    }

    /**
     * Devuelve la ruta donde está almacenado el png con la spritesheet del modelo
     * @param
     * @return
     */
    @Override
    public ResourceLocation getTextureLocation(ChicaEntity chicaEntity) {
        return ResourceLocation.fromNamespaceAndPath(MiMod.MOD_ID,"textures/entity/rhino/rhino.png");
    }




}
