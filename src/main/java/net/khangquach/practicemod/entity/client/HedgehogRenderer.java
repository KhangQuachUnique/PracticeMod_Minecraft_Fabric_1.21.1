package net.khangquach.practicemod.entity.client;

import net.khangquach.practicemod.PracticeMod;
import net.khangquach.practicemod.entity.custom.HedgehogEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HedgehogRenderer extends GeoEntityRenderer<HedgehogEntity> {
    public HedgehogRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HedgehogModel());
    }

    @Override
    public Identifier getTexture(HedgehogEntity animatable) {
        return Identifier.of(PracticeMod.MOD_ID, "textures/entity/hedgehog.png");
    }

    @Override
    public void render(HedgehogEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        if(entity.isBaby()) {
            poseStack.scale(0.4f,0.4f,0.4f);
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
