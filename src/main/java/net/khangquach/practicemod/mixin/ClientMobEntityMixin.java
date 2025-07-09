package net.khangquach.practicemod.mixin;

import net.khangquach.practicemod.entity.api.MultiPartEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.advancement.criterion.ConstructBeaconCriterion.Conditions.level;

@Mixin(MobEntity.class)
public abstract class ClientMobEntityMixin extends LivingEntity {

    protected ClientMobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tickMovement", at = @At("RETURN"))
    public void tickCustomParts(CallbackInfo ci) {
        if (this instanceof MultiPartEntity<?> multiPartEntity) {
            if (getWorld() instanceof ClientWorld clientWorld) {
                multiPartEntity.getEntityHitboxData().getAttackBoxData().clientTick(clientWorld);
            }
        }
    }
}
