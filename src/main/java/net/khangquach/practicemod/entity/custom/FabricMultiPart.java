package net.khangquach.practicemod.entity.custom;

import net.khangquach.practicemod.entity.api.AnimationOverride;
import net.khangquach.practicemod.entity.api.HitboxData;
import net.khangquach.practicemod.entity.api.MultiPart;
import net.khangquach.practicemod.entity.api.MultiPartEntity;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class FabricMultiPart<T extends MobEntity & MultiPartEntity<T>> extends Entity implements MultiPart<T> {
    public final T parent;
    private final EntityDimensions size;
    private final Vec3d offset;
    private final String partName;
    @Nullable
    private AnimationOverride animationOverride;

    public FabricMultiPart(T parent, HitboxData hitboxData) {
        super(parent.getType(), parent.getWorld());
        this.parent = parent;
        this.size = EntityDimensions.changing(hitboxData.width(), hitboxData.height());
        this.offset = hitboxData.pos();
        this.partName = hitboxData.name();
        this.noClip = true;
        this.calculateDimensions();
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        return parent.interact(player, hand);
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    @Override
    public boolean damage(@NotNull DamageSource source, float amount) {
        if (isInvulnerableTo(source)) {
            return false;
        }
        return parent.partHurt(this, source, amount);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        setRemoved(reason);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull EntityPose pose) {
        if (animationOverride != null) {
            return size.scaled(parent.getScale()).scaled(animationOverride.scaleW(), animationOverride.scaleH());
        }
        return size.scaled(parent.getScale());
    }

    @Override
    public @NotNull Entity getRootVehicle() {
        return getParent().getRootVehicle();
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    public String getPartName() {
        return partName;
    }

    @Override
    public T getParent() {
        return parent;
    }

    @Override
    public Entity getEntity() {
        return this;
    }

    @Override
    public Vec3d getOffset() {
        return offset;
    }

    @Override
    public void setOverride(AnimationOverride newOverride) {
        if (animationOverride != null && (animationOverride.scaleH() != newOverride.scaleH() || animationOverride.scaleW() != newOverride.scaleW())) {
            animationOverride = newOverride;
            calculateDimensions();
        } else {
            animationOverride = newOverride;
        }
    }

    @Override
    public AnimationOverride getOverride() {
        return animationOverride;
    }

    @ApiStatus.Internal
    public static class FabricMultiPartFactory implements MultiPart.Factory {

        @Override
        public <T extends MobEntity & MultiPartEntity<T>> MultiPart<T> create(T parent, HitboxData hitboxData) {
            return new FabricMultiPart<>(parent, hitboxData);
        }
    }
}
