package com.mimod.entity.client.model.freddy;

import com.mimod.entity.custom.FreddyEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FreddyModel extends GeoModel<FreddyEntity> {
    @Override
    public ResourceLocation getModelResource(FreddyEntity freddyEntity) {
        return ResourceLocation.fromNamespaceAndPath("mimod", "geo/freddy/freddy.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FreddyEntity freddyEntity) {
        return ResourceLocation.fromNamespaceAndPath("mimod", "textures/entity/freddy/freddy.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FreddyEntity freddyEntity) {
        return ResourceLocation.fromNamespaceAndPath("mimod", "animations/freddy/freddy.animation.json");
    }
}
