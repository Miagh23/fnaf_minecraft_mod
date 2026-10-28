package com.mimod.entity.client.cupcake;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mimod.MiMod;
import com.mimod.entity.custom.ChicaEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/**
 * esta clase es para ver cómo se vería cupcake en lugar del lanzallamas
 * Cambiará cuando añadan los modelos
 */
public class CupcakeRenderer extends EntityRenderer<ChicaEntity.Cupcake> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MiMod.MOD_ID, "textures/entity/cupcake/cupcake.png");

    // constructor obligatorio que exige Minecraft
    public CupcakeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    // obligatorio-> indica qué archivo PNG debe usar esta entidad
    @Override
    public ResourceLocation getTextureLocation(ChicaEntity.Cupcake entity) {
        return TEXTURE;
    }

    // se ejecuta en cada fotograma para dibujar la entidad e
    @Override
    public void render(
            ChicaEntity.Cupcake entity,
            float entityYaw /*rotación horizontal*/,
            float partialTicks /*porcentaje fluido entre ticks*/,
            PoseStack poseStack /*permite mover, rotar o escalar las coordenadas 3D*/,
            MultiBufferSource buffer /*empaqueta la información visual para enviarla*/,
            int packedLight /*iluminación ambiental del entorno*/
    ) {
        poseStack.pushPose(); // guarda una copia de las coordenadas actuales para no alterar el resto del mundo

        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation()); // para mirar siempre de frente a la cámara del jugador

        poseStack.scale(1F, 1F, 1F); // tamaño cupcake

        // para pintar la textura PNG:
        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity)));
        PoseStack.Pose posestack$pose = poseStack.last();

        // dibuja los vértices aplicando la textura de cupcake
        this.vertex(posestack$pose, vertexconsumer, -0.5F, -0.5F, 0, 1, packedLight, 0);
        this.vertex(posestack$pose, vertexconsumer,  0.5F, -0.5F, 1, 1, packedLight, 0);
        this.vertex(posestack$pose, vertexconsumer,  0.5F,  0.5F, 1, 0, packedLight, 0);
        this.vertex(posestack$pose, vertexconsumer, -0.5F,  0.5F, 0, 0, packedLight, 0);

        poseStack.popPose(); // restaura las coordenadas 3D originales tras terminar de dibujar
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight); // ejecuta el ciclo base heredado
    }

    // para simplificar la creación de los vértices del sprite plano
    private void vertex(PoseStack.Pose pose, VertexConsumer consumer, float x, float y, float u, float v, int packedLight, int normalX) {
        consumer.addVertex(pose, x, y, 0.0F)
                .setColor(255, 255, 255, 255) // para que la textura se vea sin filtros
                .setUv(u, v) // coordenadas de la imagen PNG que se van a recortar.
                .setOverlay(OverlayTexture.NO_OVERLAY) // sin efectos de superposición de daño
                .setLight(15728880) // para que el cupcake no se vea oscuro con el entorno
                .setNormal(0.0F, 1.0F, 0.0F); // para calcular las sombras de la luz.
    }
}