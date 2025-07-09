package net.khangquach.practicemod.mixin;

import net.khangquach.practicemod.entity.internal.GeckoLibMultiPartMob;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MobEntity.class)
public abstract class GeckoLibMobMixin extends LivingEntity implements GeckoLibMultiPartMob {
    @Unique
    private int moreHitboxes$renderTick;

    protected GeckoLibMobMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean practicemod$isNewRenderTick() {
        return moreHitboxes$renderTick < age;
    }

    @Override
    public void practicemod$updateRenderTick() {
        moreHitboxes$renderTick = age;
    }
}

