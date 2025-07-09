package net.khangquach.practicemod.entity.client;

import net.khangquach.practicemod.PracticeMod;
import net.khangquach.practicemod.entity.custom.DragonEntity;
import net.khangquach.practicemod.entity.internal.MultiPartGeoEntityRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DragonRenderer extends GeoEntityRenderer<DragonEntity> {
    public DragonRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DragonModel());
    }

    @Override
    public Identifier getTexture(DragonEntity animatable) {
        return Identifier.of(PracticeMod.MOD_ID, "textures/entity/dragon.png");
    }

    @Override
    public void render(DragonEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {

        if(entity.isBaby()) {
            poseStack.scale(0.4f,0.4f,0.4f);
        }
        if (this instanceof MultiPartGeoEntityRenderer renderer) {
            renderer.practicemod$removeTickForEntity(animatable);
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
