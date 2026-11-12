
package com.mimod.entity.client.model.cryingchild;

import com.mimod.entity.custom.CryingChildEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CryingChildModel extends GeoModel<CryingChildEntity> {

    @Override
    public ResourceLocation getModelResource(CryingChildEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(
                "mimod",
                "geo/cryingchild/crying_child.geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(CryingChildEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(
                "mimod",
                "textures/entity/cryingchild/crying_child.png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(CryingChildEntity entity) {
        return null;
    }
}
