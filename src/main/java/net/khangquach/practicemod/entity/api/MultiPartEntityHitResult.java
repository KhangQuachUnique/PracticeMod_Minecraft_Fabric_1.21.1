package net.khangquach.practicemod.entity.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public interface MultiPartEntityHitResult {

    void moreHitboxes$setMultiPart(MultiPart<?> part);

    /**
     * Returns the {@link MultiPart} that was originally the entity of the hit result
     *
     * @return the {@link MultiPart} targeted
     */
    @Nullable
    @Contract(pure = true)
    MultiPart<?> moreHitboxes$getMultiPart();
}
