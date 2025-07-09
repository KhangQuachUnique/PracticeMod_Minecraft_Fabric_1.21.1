package net.khangquach.practicemod.entity.internal;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.khangquach.practicemod.entity.api.AnchorData;
import net.khangquach.practicemod.entity.api.MultiPartEntity;
import net.khangquach.practicemod.entity.api.HitboxData;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@ApiStatus.Internal
public class AnchorDataInternal<T extends MobEntity & MultiPartEntity<T>> implements AnchorData {
    private final Map<String, HitboxData> anchors = new Object2ObjectOpenHashMap<>();
    private final Set<String> anchorOverride = new ObjectArraySet<>();
    private final Map<HitboxData, Vec3d> anchorPositions = new Object2ObjectOpenHashMap<>();
    private final Map<HitboxData, Vec3d> anchorPositionBackup = new Object2ObjectOpenHashMap<>();
    private final T entity;

    public AnchorDataInternal(T entity) {
        this.entity = entity;
    }

    @Override
    public Optional<Vec3d> getAnchorPos(String ref) {
        return Optional.ofNullable(anchorPositions.get(anchors.get(ref)));
    }

    @Override
    public boolean isAnchor(String ref) {
        return anchors.containsKey(ref);
    }

    @Override
    public void addAnchor(String ref, HitboxData hitboxData) {
        anchors.put(ref, hitboxData);
    }

    @Override
    public void updatePositions() {
        for (Map.Entry<String, HitboxData> entry : anchors.entrySet()) {
            if (anchorOverride.contains(entry.getKey())) {
                anchorOverride.remove(entry.getKey());
            } else {
                Vec3d offset = entry.getValue().pos();

                // Góc yaw (độ) → radian
                float yaw = entity.getBodyYaw();
                double rad = Math.toRadians(-yaw);

                // Tự quay thủ công Vec3d quanh trục Y
                double cos = Math.cos(rad);
                double sin = Math.sin(rad);
                double x = offset.x * cos - offset.z * sin;
                double z = offset.x * sin + offset.z * cos;

                Vec3d rotatedOffset = new Vec3d(x, offset.y, z);
                Vec3d scaledOffset = rotatedOffset.multiply(entity.getScale());

                Vec3d newPos = entity.getPos().add(scaledOffset);
                anchorPositions.put(entry.getValue(), newPos);
            }
        }
    }

    @Override
    public void updatePosition(String ref, Vec3d localPos) {
        //Since we are getting the position from geckolib its 1 tick behind which can be really noticeable when using it to position a rider
        //That's why we try to guess the next position based on the difference to the previous position
        HitboxData hitbox = anchors.get(ref);
        Vec3d prevActual = anchorPositionBackup.get(hitbox);
        Vec3d pos = entity.getPos().add(localPos);
        anchorPositionBackup.put(hitbox, pos);
        if (prevActual != null) {
            if (prevActual.subtract(anchorPositions.get(hitbox)).length() > 0.05) {
                //Our previous guess was wrong (probably because the mob stopped moving) so we assume that this one is also wrong
                pos = pos.add(pos.subtract(prevActual).multiply(0.5));
            } else {
                pos = pos.add(pos.subtract(prevActual));
            }
        }
        anchorPositions.put(hitbox, pos);
        //Need to override because GeckoLib mixin calls this method after entity tick but before updatePositions
        anchorOverride.add(ref);
    }
}

