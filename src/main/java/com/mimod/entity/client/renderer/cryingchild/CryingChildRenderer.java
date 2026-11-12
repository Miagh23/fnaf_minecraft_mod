
package com.mimod.entity.client.renderer.cryingchild;

import com.mimod.entity.client.model.cryingchild.CryingChildModel;
import com.mimod.entity.custom.CryingChildEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CryingChildRenderer extends GeoEntityRenderer<CryingChildEntity> {

    public CryingChildRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CryingChildModel());
    }
}
