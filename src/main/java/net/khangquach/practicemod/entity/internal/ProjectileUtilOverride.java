package net.khangquach.practicemod.entity.internal;


import net.khangquach.practicemod.entity.api.MultiPart;
import net.khangquach.practicemod.entity.api.MultiPartEntityHitResult;
import net.minecraft.util.hit.EntityHitResult;

public class ProjectileUtilOverride {

    /**
     * Uses {@link MultiPartEntityHitResult} to add the correct part to the EntityHitResult
     */
    public static EntityHitResult modifyPartEntity(EntityHitResult original) {
        if (original == null) {
            return null;
        }
        if (original.getEntity() instanceof MultiPart<?> part) {
            EntityHitResult hitResult = new EntityHitResult(part.getParent(), original.getPos());
            ((MultiPartEntityHitResult) hitResult).moreHitboxes$setMultiPart(part);
            return hitResult;
        }
        return original;
    }
}