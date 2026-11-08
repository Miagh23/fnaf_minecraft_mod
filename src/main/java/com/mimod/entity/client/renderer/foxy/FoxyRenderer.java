package com.mimod.entity.client.renderer.foxy;

import com.mimod.entity.client.model.foxy.FoxyModel;
import com.mimod.entity.custom.foxy.FoxyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FoxyRenderer extends GeoEntityRenderer<FoxyEntity> {

    public FoxyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FoxyModel());
    }
}