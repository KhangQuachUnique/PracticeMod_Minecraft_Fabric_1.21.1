package net.khangquach.practicemod.entity.api;

import net.khangquach.practicemod.entity.api.HitboxData;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

public interface AnchorData {
    /**
     * Returns an anchor position if the given name was linked in {@link HitboxData#ref()}.
     * <p>
     * If GeckoLib support is enabled this position will match the referenced bone
     *
     * @param ref the name of the anchor
     * @return the anchor position in the world or an empty {@code Optional}
     */
    Optional<Vec3d> getAnchorPos(String ref);

    /**
     * Returns {@code true} if an anchor is referenced by the given string
     */
    boolean isAnchor(String ref);

    @ApiStatus.Internal
    void addAnchor(String ref, HitboxData hitboxData);

    @ApiStatus.Internal
    void updatePositions();

    @ApiStatus.Internal
    void updatePosition(String ref, Vec3d pos);
}
