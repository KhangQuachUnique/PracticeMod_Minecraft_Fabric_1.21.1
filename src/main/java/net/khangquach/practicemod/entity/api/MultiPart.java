package net.khangquach.practicemod.entity.api;

import net.khangquach.practicemod.entity.api.HitboxData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;

public interface MultiPart<T extends MobEntity & MultiPartEntity<T>> {
    String getPartName();

    Entity getEntity();

    T getParent();

    void setOverride(AnimationOverride animationOverride);

    AnimationOverride getOverride();

    /**
     * Used only if no GeckoLib bone has been set
     *
     * @return the initial local position defined in {@link HitboxData#pos()}
     */
    Vec3d getOffset();

    @ApiStatus.Internal
    interface Factory {
        <T extends MobEntity & MultiPartEntity<T>> MultiPart<T> create(T parent, HitboxData hitboxData);
    }
}

