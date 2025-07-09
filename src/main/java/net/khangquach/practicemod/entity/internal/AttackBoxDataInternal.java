package net.khangquach.practicemod.entity.internal;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.khangquach.practicemod.entity.api.AttackBoxData;
import net.khangquach.practicemod.entity.api.MultiPartEntity;
import net.khangquach.practicemod.entity.api.HitboxData;
import net.khangquach.practicemod.entity.platform.Services;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.logging.Level;

@ApiStatus.Internal
public class AttackBoxDataInternal<T extends MobEntity & MultiPartEntity<T>> implements AttackBoxData {
    private final Map<String, HitboxData> attackBoxes = new Object2ObjectOpenHashMap<>();
    private final Map<HitboxData, Vec3d> activeAttackBoxes = new Object2ObjectOpenHashMap<>();
    private long attackBoxEndTime;
    private final T entity;

    public AttackBoxDataInternal(T entity) {
        this.entity = entity;
    }

    @Override
    public void addAttackBox(String ref, HitboxData hitboxData) {
        attackBoxes.put(ref, hitboxData);
    }

    @Override
    public HitboxData getAttackBox(String ref) {
        return attackBoxes.get(ref);
    }

    @Override
    public void moveActiveAttackBox(HitboxData attackBox, Vec3d worldPos) {
        activeAttackBoxes.put(attackBox, worldPos);
    }

    @Override
    public boolean isAttackBoxActive(HitboxData attackBox) {
        return activeAttackBoxes.containsKey(attackBox);
    }

    @Override
    public void activateAttackBoxes(World world, double attackDuration) {
        attackBoxes.values().forEach(hitbox -> activeAttackBoxes.put(hitbox, Vec3d.ZERO));
        attackBoxEndTime = (long) (world.getTime() + attackDuration);
    }

    @Override
    public void clientTick(World world) {
        if (world.getTime() > attackBoxEndTime) {
            activeAttackBoxes.clear();
        }
        for (Map.Entry<HitboxData, Vec3d> entry : activeAttackBoxes.entrySet()) {
            HitboxData hitbox = entry.getKey();
            EntityDimensions size = EntityDimensions.changing(hitbox.width(), hitbox.height()).scaled(entity.getScale());
            float width = size.width();
            float height = size.height();
            Vec3d pos = entry.getValue(); // trung tâm hitbox

            Box aabb = new Box(
                    pos.x - width / 2, pos.y, pos.z - width / 2,
                    pos.x + width / 2, pos.y + height, pos.z + width / 2
            );
            PlayerEntity player = DistUtilFactory.DIST_UTIL.handleIntersect(aabb);
            if (player != null) {
                if (entity.attackBoxHit(player)) {
                    activeAttackBoxes.clear();
                }
                break;
            }
        }
    }

    @Override
    public Map<HitboxData, Vec3d> getActiveBoxes() {
        return activeAttackBoxes;
    }

    @Override
    public long attackBoxEndTime() {
        return attackBoxEndTime;
    }

    @ApiStatus.Internal
    public interface DistUtilFactory {
        DistUtilFactory DIST_UTIL = Services.load(DistUtilFactory.class);

        PlayerEntity handleIntersect(Box aabb);
    }
}

