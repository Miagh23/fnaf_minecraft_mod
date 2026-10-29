package com.mimod.entity.client.renderer.chica;

import com.mimod.entity.client.model.chica.ChicaModel;
import com.mimod.entity.custom.ChicaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ChicaRenderer extends GeoEntityRenderer<ChicaEntity> {

    public ChicaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ChicaModel());
    }
}