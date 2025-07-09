package net.khangquach.practicemod.mixin;

import net.khangquach.practicemod.entity.api.MultiPart;
import net.minecraft.entity.Entity;
import net.minecraft.world.entity.EntityLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.server.world.ServerEntityManager.class)
public abstract class ServerEntityManagerMixin {

    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    public void doNotAddMultiPart(EntityLike entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof MultiPart) {
            cir.cancel();
        }
    }
}
