package net.khangquach.practicemod.mixin;

import net.khangquach.practicemod.entity.MultiPartLevel;
import net.khangquach.practicemod.entity.api.MultiPart;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.Collection;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements MultiPartLevel {

    private final Collection<MultiPart<?>> multipartEntities = new ArrayList<>();

    @Override
    public Collection<MultiPart<?>> moreHitboxes$getMultiParts() {
        return multipartEntities;
    }
}