package net.khangquach.practicemod.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.khangquach.practicemod.entity.api.MultiPart;
import net.khangquach.practicemod.entity.api.MultiPartEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract double getX();

    @Shadow
    public abstract double getY();

    @Shadow
    public abstract double getZ();

    @Shadow
    public abstract void setPos(double x, double y, double z);

    @Inject(
            method = "onTrackedDataSet(Lnet/minecraft/entity/data/TrackedData;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;calculateDimensions()V")
    )
    private void saveY(TrackedData<?> data, CallbackInfo ci, @Share("oldY") LocalDoubleRef oldY) {
        oldY.set(this.getY());
    }

    @Inject(
            method = "onTrackedDataSet(Lnet/minecraft/entity/data/TrackedData;)V",
            at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/entity/Entity;calculateDimensions()V")
    )
    private void restoreY(TrackedData<?> data, CallbackInfo ci, @Share("oldY") LocalDoubleRef oldY) {
        if (this instanceof MultiPartEntity<?> multi && multi.getEntityHitboxData().fixPosOnRefresh()) {
            this.setPos(this.getX(), oldY.get(), this.getZ());
        }
    }

    @Inject(method = "calculateDimensions", at = @At("RETURN"))
    public void refreshDimensionsForParts(CallbackInfo ci, @Share("oldY") LocalDoubleRef oldY) {
        if (this instanceof MultiPartEntity<?> multiPartEntity) {
            for (MultiPart<?> part : multiPartEntity.getEntityHitboxData().getCustomParts()) {
                part.getEntity().calculateDimensions();
            }
        }
    }

    @ModifyReturnValue(method = "getBoundingBox", at = @At("RETURN"))
    public Box changeCullBox(Box original) {
        if (this instanceof MultiPartEntity<?> multiPartEntity && multiPartEntity.getEntityHitboxData() != null && multiPartEntity.getEntityHitboxData().hasCustomParts()) {
            return multiPartEntity.getEntityHitboxData().getCullingBounds();
        }
        return original;
    }

    @Inject(method = "setBoundingBox", at = @At("RETURN"))
    public void updateBounds(Box aABB, CallbackInfo ci) {
        if (this instanceof MultiPartEntity<?> multiPartEntity && multiPartEntity.getEntityHitboxData() != null) {
            multiPartEntity.getEntityHitboxData().makeAttackBounds();
            multiPartEntity.getEntityHitboxData().makeBoundingBoxForCulling();
        }
    }

    @Inject(method = "setId", at = @At("RETURN"))
    public void setPartIds(int id, CallbackInfo ci) {
        if (this instanceof MultiPartEntity<?> multiPartEntity) {
            var list = multiPartEntity.getEntityHitboxData().getCustomParts();
            for (int i = 0; i < list.size(); i++) {
                list.get(i).getEntity().setId(id + i + 1);
            }
        }
    }

    @Inject(method = "remove", at = @At("RETURN"))
    public void callRemoveCallback(Entity.RemovalReason removalReason, CallbackInfo ci) {
        if (this instanceof MultiPartEntity<?> multiPartEntity) {
            for (MultiPart<?> part : multiPartEntity.getEntityHitboxData().getCustomParts()) {
                part.getEntity().remove(removalReason);
            }
        }
    }
}

