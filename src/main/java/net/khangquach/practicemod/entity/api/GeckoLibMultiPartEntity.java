package net.khangquach.practicemod.entity.api;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Vec3d;

public interface GeckoLibMultiPartEntity<T extends MobEntity & MultiPartEntity<T>> extends MultiPartEntity<T> {

    /**
     * This method will be called clientside if {@link GeckoLibMultiPartEntity#canSetAnchorPos(String)} returned {@code true}
     * and should be used instead of {@link AnchorData#getAnchorPos(String)} if the position is only needed on the client.
     * <p>
     * Possible use cases are the positioning of geckolib particle listeners
     *
     * @param boneName the name of the bone
     * @param localPos the position of the bone relative to the mobs position
     */
    default void setAnchorPos(String boneName, Vec3d localPos) {
    }

    /**
     * Called to check if the position for the given bone should be calculated and passed to {@link GeckoLibMultiPartEntity#setAnchorPos(String, Vec3d)}
     *
     * @param boneName the name of the bone
     * @return {@code true} if the position for the given bone should be calculated
     * @apiNote this method will be called for every bone that is not attached to a {@link MultiPart} or {@link AnchorData}
     */
    default boolean canSetAnchorPos(String boneName) {
        return false;
    }
}