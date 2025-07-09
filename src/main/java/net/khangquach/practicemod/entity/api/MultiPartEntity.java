package net.khangquach.practicemod.entity.api;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public interface MultiPartEntity<T extends MobEntity & MultiPartEntity<T>> {
    EntityHitboxData<T> getEntityHitboxData();

    boolean partHurt(MultiPart<T> multiPart, @NotNull DamageSource source, float amount);

    default Box makeAttackBoundingBox(float scaledHeadRadius) {
        MobEntity mob = (MobEntity) this;
        if (scaledHeadRadius == 0) {
            float increase = Math.min(mob.getWidth() / 2, 2.25f);
            return inflateAABB(mob.getBoundingBox(), increase, increase, increase);
        } else {
            float radius = scaledHeadRadius * 0.9f;
            return inflateAABB(mob.getBoundingBox(), radius, radius * 0.55, radius);
        }
    }

    default Box makeBoundingBoxForCulling(float frustumWidthRadius, float frustumHeight) {
        MobEntity mob = (MobEntity) this;
        float x = frustumWidthRadius * mob.getScale();
        float y = frustumHeight * mob.getScale();
        Vec3d pos = mob.getPos();
        return new Box(pos.x - x, pos.y, pos.z - x, pos.x + x, pos.y + y, pos.z + x);
    }

    private Box inflateAABB(Box base, double x, double y, double z) {
        return new Box(base.minX - x, base.minY - Math.min(1, y), base.minZ - z, base.maxX + x, base.maxY + y, base.maxZ + z);
    }

    default boolean attackBoxHit(PlayerEntity player) {
        return true;
    }
}
