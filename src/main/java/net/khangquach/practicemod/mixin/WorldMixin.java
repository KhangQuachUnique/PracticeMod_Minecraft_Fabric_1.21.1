package net.khangquach.practicemod.mixin;

import net.khangquach.practicemod.entity.MultiPartLevel;
import net.khangquach.practicemod.entity.api.MultiPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(World.class)
public abstract class WorldMixin implements MultiPartLevel {

    @Inject(
            method = "getOtherEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;)Ljava/util/List;",
            at = @At("RETURN")
    )
    private void onGetOtherEntities(Entity except, Box box, Predicate<? super Entity> predicate, CallbackInfoReturnable<List<Entity>> cir) {
        List<Entity> list = cir.getReturnValue();

        for (MultiPart<?> part : moreHitboxes$getMultiParts()) {
            Entity partEntity = part.getEntity();
            Entity parent = part.getParent();

            if (parent != except && partEntity.getBoundingBox().intersects(box)
                    && predicate.test(partEntity) && predicate.test(parent)) {
                list.add(partEntity);
            }
        }
    }
}
