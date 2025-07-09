package net.khangquach.practicemod.entity.api;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;

public record HitboxData(String name, Vec3d pos, float width, float height, String ref, boolean isAttackBox,
                         boolean isAnchor) {

    @ApiStatus.Internal
    public float getFrustumWidthRadius() {
        return (float) Math.max(Math.abs(pos.x) + width / 2, Math.abs(pos.z) + width / 2);
    }

    @ApiStatus.Internal
    public float getFrustumHeight() {
        return (float) pos.y + height;
    }

    @ApiStatus.Internal
    public static HitboxData readBuf(PacketByteBuf buf) {
        return new HitboxData(buf.readString(), new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()), buf.readFloat(), buf.readFloat(), buf.readString(), buf.readBoolean(), buf.readBoolean());
    }

    @ApiStatus.Internal
    public static void writeBuf(PacketByteBuf buf, HitboxData hitbox) {
        buf.writeString(hitbox.name);
        buf.writeDouble(hitbox.pos.x);
        buf.writeDouble(hitbox.pos.y);
        buf.writeDouble(hitbox.pos.z);
        buf.writeFloat(hitbox.width);
        buf.writeFloat(hitbox.height);
        buf.writeString(hitbox.ref);
        buf.writeBoolean(hitbox.isAttackBox);
        buf.writeBoolean(hitbox.isAnchor);
    }
}
