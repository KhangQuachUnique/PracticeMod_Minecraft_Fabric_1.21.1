// == Mixin vào EntityRenderDispatcher để vẽ bounding box cho hitbox phụ ==
package net.khangquach.practicemod.mixin;

import net.khangquach.practicemod.entity.api.EntityHitboxData;
import net.khangquach.practicemod.entity.api.HitboxData;
import net.khangquach.practicemod.entity.api.MultiPart;
import net.khangquach.practicemod.entity.api.MultiPartEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(
            method = "renderHitbox",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.AFTER,
                    ordinal = 0,
                    target = "Lnet/minecraft/client/render/WorldRenderer;drawBox(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/util/math/Box;FFFF)V"
            )
    )
    private static void renderMultipartHitbox(
            MatrixStack matrices, VertexConsumer vertices, Entity entity,
            float tickDelta, float red, float green, float blue,
            CallbackInfo ci
    ) {
        if (!(entity instanceof MobEntity mob && mob instanceof MultiPartEntity<?> multiPart)) return;

        double dx = -MathHelper.lerp(tickDelta, entity.prevX, entity.getX());
        double dy = -MathHelper.lerp(tickDelta, entity.prevY, entity.getY());
        double dz = -MathHelper.lerp(tickDelta, entity.prevZ, entity.getZ());

        EntityHitboxData<?> hitboxData = multiPart.getEntityHitboxData();

        // Vẽ culling box tổng
        Box totalBox = hitboxData.getCullingBounds().offset(-entity.getX(), -entity.getY(), -entity.getZ());
        WorldRenderer.drawBox(matrices, vertices, totalBox, 1f, 0f, 1f, 1f);

        // Vẽ attack bounds
        Box attackBox = hitboxData.getAttackBounds().offset(-entity.getX(), -entity.getY(), -entity.getZ());
        WorldRenderer.drawBox(matrices, vertices, attackBox, 0f, 0f, 1f, 1f);

        // Vẽ từng attack part
        for (Map.Entry<HitboxData, Vec3d> entry : hitboxData.getAttackBoxData().getActiveBoxes().entrySet()) {
            HitboxData hb = entry.getKey();
            Vec3d pos = entry.getValue();
            EntityDimensions size = EntityDimensions.changing(hb.width(), hb.height()).scaled(mob.getScale());
            Box partBox = new Box(
                    pos.x - size.width() / 2, pos.y, pos.z - size.width() / 2,
                    pos.x + size.width() / 2, pos.y + size.height(), pos.z + size.width() / 2
            );

            matrices.push();
            double tx = dx + pos.x;
            double ty = dy + pos.y;
            double tz = dz + pos.z;
            matrices.translate(tx, ty, tz);

            boolean hit = MinecraftClient.getInstance().player.getBoundingBox().intersects(partBox);
            WorldRenderer.drawBox(
                    matrices, vertices,
                    partBox.offset(-pos.x, -pos.y, -pos.z),
                    hit ? 1f : 0f, 0f, hit ? 0f : 1f, 1f
            );
            matrices.pop();
        }

        // Vẽ hitbox phụ
        for (MultiPart<?> mp : hitboxData.getCustomParts()) {
            Entity part = mp.getEntity();
            matrices.push();
            double tx = dx + MathHelper.lerp(tickDelta, part.prevX, part.getX());
            double ty = dy + MathHelper.lerp(tickDelta, part.prevY, part.getY());
            double tz = dz + MathHelper.lerp(tickDelta, part.prevZ, part.getZ());
            matrices.translate(tx, ty, tz);

            Box pb = part.getBoundingBox().offset(-part.getX(), -part.getY(), -part.getZ());
            WorldRenderer.drawBox(matrices, vertices, pb, 0f, 1f, 0f, 1f);
            matrices.pop();
        }
    }
}


