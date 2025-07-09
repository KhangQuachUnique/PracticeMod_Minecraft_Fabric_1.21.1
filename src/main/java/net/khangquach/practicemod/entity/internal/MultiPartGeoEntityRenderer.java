package net.khangquach.practicemod.entity.internal;

import net.minecraft.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
@ApiStatus.Internal
public interface MultiPartGeoEntityRenderer {

    @ApiStatus.Internal
    void practicemod$removeTickForEntity(Entity entity);
}
