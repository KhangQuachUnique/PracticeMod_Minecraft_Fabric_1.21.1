package net.khangquach.practicemod.entity;

import net.khangquach.practicemod.entity.api.MultiPart;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

@ApiStatus.Internal
public interface MultiPartLevel {
    Collection<MultiPart<?>> moreHitboxes$getMultiParts();

    void moreHitboxes$addMultiPart(MultiPart<?> part);

    void moreHitboxes$removeMultiPart(MultiPart<?> part);
}
