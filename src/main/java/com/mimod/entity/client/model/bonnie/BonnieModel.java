package com.mimod.entity.client.model.bonnie;

import com.mimod.entity.custom.BonnieEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BonnieModel extends GeoModel<BonnieEntity> {

    @Override
    public ResourceLocation getModelResource(BonnieEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath("mimod", "geo/bonnie/bonnie.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BonnieEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath("mimod", "textures/entity/bonnie/bonnie.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BonnieEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath("mimod", "animations/bonnie/bonnie.animation.json");
    }
}
