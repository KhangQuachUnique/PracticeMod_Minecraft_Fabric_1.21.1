package net.khangquach.practicemod.mixin;

import net.khangquach.practicemod.entity.api.MultiPart;
import net.khangquach.practicemod.entity.api.MultiPartEntityHitResult;
import net.minecraft.util.hit.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityHitResult.class)
public abstract class EntityHitResultMixin implements MultiPartEntityHitResult {
    @Unique
    @Nullable
    private MultiPart<?> moreHitboxes$part;

    @Override
    public void moreHitboxes$setMultiPart(MultiPart<?> part) {
        this.moreHitboxes$part = part;
    }

    @Override
    public @Nullable MultiPart<?> moreHitboxes$getMultiPart() {
        return moreHitboxes$part;
    }
}
