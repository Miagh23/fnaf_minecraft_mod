package com.mimod.entity.client.model.chica;

import com.mimod.entity.custom.ChicaEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ChicaModel extends GeoModel<ChicaEntity> {

    @Override
    public ResourceLocation getModelResource(ChicaEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath("mimod", "geo/chica/chica.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ChicaEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath("mimod", "textures/entity/chica/chica.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ChicaEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath("mimod", "animations/chica/chica.animation.json");
    }
}