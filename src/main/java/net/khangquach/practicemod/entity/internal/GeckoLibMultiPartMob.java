package net.khangquach.practicemod.entity.internal;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
@ApiStatus.Internal
public interface GeckoLibMultiPartMob {

    /**
     * @return {@code true} if the GeckoLib renderer has not yet done a render pass this game tick
     */
    @ApiStatus.Internal
    boolean practicemod$isNewRenderTick();

    /**
     * Updates the current render tick. Called after the first GeckoLib render pass
     */
    @ApiStatus.Internal
    void practicemod$updateRenderTick();
}