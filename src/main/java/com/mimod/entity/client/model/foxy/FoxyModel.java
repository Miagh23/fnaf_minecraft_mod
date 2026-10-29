package com.mimod.entity.client.model.foxy;

import com.mimod.entity.custom.foxy.FoxyEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FoxyModel extends GeoModel<FoxyEntity> {

    @Override
    public ResourceLocation getModelResource(FoxyEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath("mimod", "geo/foxy.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FoxyEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath("mimod", "textures/entity/foxy/foxy.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FoxyEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath("mimod", "animations/foxy.animation.json");
    }
}