package net.khangquach.practicemod.entity.api;

import net.khangquach.practicemod.entity.api.HitboxData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.logging.Level;

public interface AttackBoxData {
    @ApiStatus.Internal
    void addAttackBox(String ref, HitboxData hitboxData);

    /**
     * Returns a hitbox part if the given name was linked in {@link HitboxData#ref()}.
     * <p>
     * Used by the library to provide optional GeckoLib support
     *
     * @param ref the name of the bone the hitbox part is attached to
     * @return the hitbox part attached to the given bone
     */
    HitboxData getAttackBox(String ref);

    /**
     * Sets the position of a currently active attack box. Will be called by the library if GeckoLib is installed
     *
     * @param attackBox the attack box to be moved
     * @param worldPos  the new position relative to the world
     */
    void moveActiveAttackBox(HitboxData attackBox, Vec3d worldPos);

    /**
     * Returns {@code true} if the given attack box will trigger {@link MultiPartEntity#attackBoxHit(PlayerEntity)}  MultiPartEntity#attackBoxHit(Player)}
     */
    boolean isAttackBoxActive(HitboxData attackBox);

    /**
     * Activates all attack boxes for a given duration
     * <p>
     * If GeckoLib is enabled, call this function at the beginning of an attack with
     * as the duration
     *
     * @param attackDuration for how long(in ticks) the attack should be active
     */
    void activateAttackBoxes(World world, double attackDuration);

    @ApiStatus.Internal
    void clientTick(World world);

    @ApiStatus.Internal
    Map<HitboxData, Vec3d> getActiveBoxes();

    /**
     * The last tick of the attack
     */
    long attackBoxEndTime();
}
