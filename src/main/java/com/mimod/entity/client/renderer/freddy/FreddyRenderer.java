package com.mimod.entity.client.renderer.freddy;

import com.mimod.entity.client.model.chica.ChicaModel;
import com.mimod.entity.client.model.freddy.FreddyModel;
import com.mimod.entity.custom.ChicaEntity;
import com.mimod.entity.custom.FreddyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FreddyRenderer extends GeoEntityRenderer<FreddyEntity> {
    public FreddyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FreddyModel());
    }
}
