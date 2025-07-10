package net.khangquach.practicemod.entity.custom;

import net.khangquach.practicemod.PracticeMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class DragonEntity extends MobEntity implements Monster, GeoAnimatable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Dragon Parts - Multi-hitbox system
    private final DragonPart[] parts;
    public final DragonPart head;
    private final DragonPart neck;
    private final DragonPart body;
    private final DragonPart tail1;
    private final DragonPart tail2;
    private final DragonPart tail3;
    private final DragonPart rightWing;
    private final DragonPart leftWing;

    // Animation variables
    public float prevWingPosition;
    public float wingPosition;
    public float yawAcceleration;

    // Segment tracking for smooth movement
    public final double[][] segmentCircularBuffer = new double[64][3];
    public int latestSegment = -1;

    public DragonEntity(EntityType<? extends MobEntity> type, World world) {
        super(type, world);

        // Initialize dragon parts với size khác nhau
        this.head = new DragonPart(this, "head", 1.0F, 1.0F);
        this.neck = new DragonPart(this, "neck", 3.0F, 3.0F);
        this.body = new DragonPart(this, "body", 5.0F, 3.0F);
        this.tail1 = new DragonPart(this, "tail", 2.0F, 2.0F);
        this.tail2 = new DragonPart(this, "tail", 2.0F, 2.0F);
        this.tail3 = new DragonPart(this, "tail", 2.0F, 2.0F);
        this.rightWing = new DragonPart(this, "wing", 4.0F, 2.0F);
        this.leftWing = new DragonPart(this, "wing", 4.0F, 2.0F);

        // Array chứa tất cả parts
        this.parts = new DragonPart[]{
                this.head, this.neck, this.body,
                this.tail1, this.tail2, this.tail3,
                this.rightWing, this.leftWing
        };

        this.setHealth(this.getMaxHealth());
        this.noClip = false;
        this.ignoreCameraFrustum = true;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 200.0F)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.6F)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.8F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0F);
    }

    @Override
    public void tick() {
        super.tick();
        this.tickDragonMovement();
    }

    private void tickDragonMovement() {
        this.prevWingPosition = this.wingPosition;

        if (this.isDead()) {
            return;
        }

        // Wing flapping animation
        Vec3d velocity = this.getVelocity();
        float wingSpeed = 0.2F / ((float)velocity.horizontalLength() * 10.0F + 1.0F);
        wingSpeed *= (float)Math.pow(2.0F, velocity.y);
        this.wingPosition += wingSpeed;

        this.setYaw(MathHelper.wrapDegrees(this.getYaw()));

        // Initialize segment buffer nếu chưa có
        if (this.latestSegment < 0) {
            for(int i = 0; i < this.segmentCircularBuffer.length; ++i) {
                this.segmentCircularBuffer[i][0] = (double)this.getYaw();
                this.segmentCircularBuffer[i][1] = this.getY();
            }
        }

        // Update segment buffer
        if (++this.latestSegment == this.segmentCircularBuffer.length) {
            this.latestSegment = 0;
        }
        this.segmentCircularBuffer[this.latestSegment][0] = (double)this.getYaw();
        this.segmentCircularBuffer[this.latestSegment][1] = this.getY();

        // Update dragon parts positions
        this.updateDragonParts();

        // Handle collision damage
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            this.handlePartCollisions(serverWorld);
        }
    }

    private void updateDragonParts() {
        // Lưu old positions cho smooth rendering
        Vec3d[] oldPositions = new Vec3d[this.parts.length];
        for(int i = 0; i < this.parts.length; ++i) {
            oldPositions[i] = new Vec3d(this.parts[i].getX(), this.parts[i].getY(), this.parts[i].getZ());
        }

        // Calculate rotation values
        float yawRad = this.getYaw() * ((float)Math.PI / 180F);
        float sinYaw = MathHelper.sin(yawRad);
        float cosYaw = MathHelper.cos(yawRad);

        // Update body
        this.movePart(this.body, sinYaw * 0.5F, 0.0F, -cosYaw * 0.5F);

        // Update wings
        this.movePart(this.rightWing, cosYaw * 4.5F, 2.0F, sinYaw * 4.5F);
        this.movePart(this.leftWing, cosYaw * -4.5F, 2.0F, sinYaw * -4.5F);

        // Update head và neck
        float headSin = MathHelper.sin(yawRad - this.yawAcceleration * 0.01F);
        float headCos = MathHelper.cos(yawRad - this.yawAcceleration * 0.01F);
        float headHeight = this.getHeadVerticalMovement();

        this.movePart(this.head, headSin * 6.5F, headHeight + 6.5F, -headCos * 6.5F);
        this.movePart(this.neck, headSin * 5.5F, headHeight + 5.5F, -headCos * 5.5F);

        // Update tail segments
        this.updateTailSegments(sinYaw, cosYaw);

        // Update render positions
        for(int i = 0; i < this.parts.length; ++i) {
            this.parts[i].prevX = oldPositions[i].x;
            this.parts[i].prevY = oldPositions[i].y;
            this.parts[i].prevZ = oldPositions[i].z;
            this.parts[i].lastRenderX = oldPositions[i].x;
            this.parts[i].lastRenderY = oldPositions[i].y;
            this.parts[i].lastRenderZ = oldPositions[i].z;
        }
    }

    private void updateTailSegments(float sinYaw, float cosYaw) {
        double[] baseSegment = this.getSegmentProperties(5, 1.0F);

        for(int i = 0; i < 3; ++i) {
            DragonPart tailPart = null;
            if (i == 0) tailPart = this.tail1;
            if (i == 1) tailPart = this.tail2;
            if (i == 2) tailPart = this.tail3;

            double[] segmentData = this.getSegmentProperties(12 + i * 2, 1.0F);
            float segmentYaw = this.getYaw() * ((float)Math.PI / 180F) +
                    this.wrapYawChange(segmentData[0] - baseSegment[0]) * ((float)Math.PI / 180F);

            float segmentSin = MathHelper.sin(segmentYaw);
            float segmentCos = MathHelper.cos(segmentYaw);
            float distance = (float)(i + 1) * 2.0F;

            this.movePart(tailPart,
                    -(sinYaw * 1.5F + segmentSin * distance),
                    segmentData[1] - baseSegment[1] - ((distance + 1.5F)) + 1.5F,
                    (cosYaw * 1.5F + segmentCos * distance));
        }
    }

    private void movePart(DragonPart part, double dx, double dy, double dz) {
        part.setPosition(this.getX() + dx, this.getY() + dy, this.getZ() + dz);
    }

    private float getHeadVerticalMovement() {
        return -1.0F; // Simplified head movement
    }

    public double[] getSegmentProperties(int segmentNumber, float tickDelta) {
        if (this.isDead()) {
            tickDelta = 0.0F;
        }

        tickDelta = 1.0F - tickDelta;
        int i = this.latestSegment - segmentNumber & 63;
        int j = this.latestSegment - segmentNumber - 1 & 63;
        double[] result = new double[3];
        double d = this.segmentCircularBuffer[i][0];
        double e = MathHelper.wrapDegrees(this.segmentCircularBuffer[j][0] - d);
        result[0] = d + e * (double)tickDelta;
        d = this.segmentCircularBuffer[i][1];
        e = this.segmentCircularBuffer[j][1] - d;
        result[1] = d + e * (double)tickDelta;
        result[2] = MathHelper.lerp((double)tickDelta, this.segmentCircularBuffer[i][2], this.segmentCircularBuffer[j][2]);
        return result;
    }

    private float wrapYawChange(double yawDegrees) {
        return (float)MathHelper.wrapDegrees(yawDegrees);
    }

    private void handlePartCollisions(ServerWorld world) {
        if (this.hurtTime == 0) {
            // Wing attack - launch entities
            this.launchEntities(world, world.getOtherEntities(this,
                    this.rightWing.getBoundingBox().expand(4.0F, 2.0F, 4.0F),
                    EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
            this.launchEntities(world, world.getOtherEntities(this,
                    this.leftWing.getBoundingBox().expand(4.0F, 2.0F, 4.0F),
                    EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));

            // Head và neck damage
            this.damageEntities(world.getOtherEntities(this,
                    this.head.getBoundingBox().expand(1.0F), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
            this.damageEntities(world.getOtherEntities(this,
                    this.neck.getBoundingBox().expand(1.0F), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
        }
    }

    private void launchEntities(ServerWorld world, List<Entity> entities) {
        double centerX = (this.body.getBoundingBox().minX + this.body.getBoundingBox().maxX) / 2.0F;
        double centerZ = (this.body.getBoundingBox().minZ + this.body.getBoundingBox().maxZ) / 2.0F;

        for(Entity entity : entities) {
            double dx = entity.getX() - centerX;
            double dz = entity.getZ() - centerZ;
            double distance = Math.max(dx * dx + dz * dz, 0.1);
            entity.addVelocity(dx / distance * 4.0F, 0.2F, dz / distance * 4.0F);

            if (entity instanceof PlayerEntity) {
                DamageSource damageSource = this.getDamageSources().mobAttack(this);
                entity.damage(damageSource, 5.0F);
            }
        }
    }

    private void damageEntities(List<Entity> entities) {
        for(Entity entity : entities) {
            if (entity instanceof PlayerEntity) {
                DamageSource damageSource = this.getDamageSources().mobAttack(this);
                entity.damage(damageSource, 10.0F);
            }
        }
    }

    // Multi-part damage handling
    public boolean damagePart(DragonPart part, DamageSource source, float amount) {
        if (this.isDead()) {
            return false;
        }

        // Different damage multipliers cho các parts
        if (part == this.head) {
            // Head nhận full damage
            PracticeMod.LOGGER.info("Hit head! Full damage: " + amount);
        } else if (part == this.rightWing || part == this.leftWing) {
            // Wings nhận ít damage hơn
            amount = amount / 4.0F + Math.min(amount, 1.0F);
            PracticeMod.LOGGER.info("Hit wing! Reduced damage: " + amount);
        } else {
            // Other parts nhận damage bình thường
            amount = amount / 2.0F + Math.min(amount, 1.0F);
            PracticeMod.LOGGER.info("Hit " + part.name + "! Normal damage: " + amount);
        }

        if (amount < 0.01F) {
            return false;
        }

        // Apply damage to main entity
        if (source.getAttacker() instanceof PlayerEntity) {
            return super.damage(source, amount);
        }

        return false;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        // Redirect normal damage to body part
        return !this.getWorld().isClient ? this.damagePart(this.body, source, amount) : false;
    }

    // Get all dragon parts
    public DragonPart[] getBodyParts() {
        return this.parts;
    }

    @Override
    public boolean canHit() {
        return false; // Main entity không thể hit, chỉ parts mới hit được
    }

    @Override
    public boolean collidesWith(Entity other) {
        return false;
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        // Add animation controllers here
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object o) {
        return 0;
    }
}

