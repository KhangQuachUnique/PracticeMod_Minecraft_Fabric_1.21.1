package net.khangquach.practicemod.mixin;

import net.khangquach.practicemod.entity.api.*;
import net.khangquach.practicemod.entity.internal.GeckoLibMultiPartMob;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

@Mixin(GeoEntityRenderer.class)
public abstract class GeoEntityRendererMixin<T extends Entity & GeoAnimatable> {

    @Inject(method = "renderRecursively(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/entity/Entity;Lsoftware/bernie/geckolib/cache/object/GeoBone;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/VertexConsumer;ZFIII)V",
            require = 0, remap = false, at = @At(value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lsoftware/bernie/geckolib/renderer/GeoEntityRenderer;applyRenderLayersForBone(Lnet/minecraft/client/util/math/MatrixStack;Lsoftware/bernie/geckolib/animatable/GeoAnimatable;Lsoftware/bernie/geckolib/cache/object/GeoBone;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/VertexConsumer;FII)V"))
    public void getBonePositions(MatrixStack poseStack, T animatable, GeoBone bone, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour, CallbackInfo ci) {
        if (animatable instanceof GeckoLibMultiPartEntity<?> multiPartEntity) {
            if (animatable instanceof GeckoLibMultiPartMob multiPartMob && !multiPartMob.practicemod$isNewRenderTick()) {
                return;
            }
            MultiPart<?> part = multiPartEntity.getEntityHitboxData().getCustomPart(bone.getName());
            if (part != null) {
                //Tick hitboxes
                Vector3d localPos = bone.getLocalPosition();
                part.setOverride(new AnimationOverride(new Vec3d(localPos.x, localPos.y, localPos.z), bone.getScaleX(), bone.getScaleY()));
                //TODO: Could also update the position of the part directly but that would make separating the library from geckolib more tedious
            } else if (multiPartEntity.getEntityHitboxData().getAnchorData().isAnchor(bone.getName())) {
                Vector3d localPos = bone.getLocalPosition();
                multiPartEntity.getEntityHitboxData().getAnchorData().updatePosition(bone.getName(), new Vec3d(localPos.x, localPos.y, localPos.z));
            } else if (multiPartEntity.canSetAnchorPos(bone.getName())) {
                Vector3d localPos = bone.getLocalPosition();
                multiPartEntity.setAnchorPos(bone.getName(), new Vec3d(localPos.x, localPos.y, localPos.z));
            } else {
                AttackBoxData attackBoxData = multiPartEntity.getEntityHitboxData().getAttackBoxData();
                HitboxData attackBox = attackBoxData.getAttackBox(bone.getName());
                if (attackBox != null && attackBoxData.isAttackBoxActive(attackBox)) {
                    Vector3d worldPos = bone.getWorldPosition();
                    multiPartEntity.getEntityHitboxData().getAttackBoxData().moveActiveAttackBox(attackBox, new Vec3d(worldPos.x, worldPos.y, worldPos.z));
                }
            }
        }
    }
}
