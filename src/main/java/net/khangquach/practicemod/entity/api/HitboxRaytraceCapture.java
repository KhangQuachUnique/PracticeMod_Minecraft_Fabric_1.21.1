package net.khangquach.practicemod.entity.api;

import net.minecraft.entity.Entity;

public interface HitboxRaytraceCapture {
    void setHitboxPart(Entity part);
    Entity getHitboxPart();
}
