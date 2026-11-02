package com.mimod.entity.client.renderer.bonnie;

import com.mimod.entity.client.model.bonnie.BonnieModel;
import com.mimod.entity.custom.BonnieEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BonnieRenderer extends GeoEntityRenderer<BonnieEntity> {
    public BonnieRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BonnieModel());
    }

    @Override
    public ResourceLocation getTextureLocation(BonnieEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath("mimod", "textures/entity/bonnie/bonnie.png");
    }
}
