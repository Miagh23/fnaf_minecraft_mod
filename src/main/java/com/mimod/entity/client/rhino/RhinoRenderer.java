package com.mimod.entity.client.rhino;

import com.mimod.MiMod;
import com.mimod.entity.custom.RhinoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RhinoRenderer extends MobRenderer<RhinoEntity, RhinoModel<RhinoEntity>> {

    /**
     * Renderiza el mob. Pasa el contexto, un modelo que crea con su capa y el shadowRadius.
     * @param pContext= El contexto que recibe de MobRenderer
     */

    public RhinoRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new RhinoModel<>(pContext.bakeLayer(RhinoModel.LAYER_LOCATION)),0.85f);
    }

    /**
     * Devuelve la ruta donde está almacenado el png con la spritesheet del modelo
     * @param pEntity
     * @return
     */
    @Override
    public ResourceLocation getTextureLocation(RhinoEntity pEntity) {
        return ResourceLocation.fromNamespaceAndPath(MiMod.MOD_ID,"textures/entity/rhino/rhino.png");
    }

}
