package net.khangquach.practicemod.entity.custom;

import net.khangquach.practicemod.PracticeMod;
import net.khangquach.practicemod.entity.api.EntityHitboxData;
import net.khangquach.practicemod.entity.api.EntityHitboxDataFactory;
import net.khangquach.practicemod.entity.api.MultiPart;
import net.khangquach.practicemod.entity.api.MultiPartEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;


public class DragonEntity extends MobEntity implements MultiPartEntity<DragonEntity>, GeoAnimatable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final EntityHitboxData<DragonEntity> hitboxData = EntityHitboxDataFactory.create(this);

    public DragonEntity(EntityType<? extends MobEntity> type, World world) {
        super(type, world);
    }

    @Override
    public EntityHitboxData<DragonEntity> getEntityHitboxData() {
        return hitboxData;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20) // Max health
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f); // Movement speed
    }

    @Override
    public void tick() {
        super.tick();
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

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object o) {
        return 0;
    }

    public boolean handlePartDamage(String partName, DamageSource source, float amount) {
        switch (partName) {
            case "head" -> {
                // Đầu bị đánh sẽ bị damage nặng hơn
                PracticeMod.LOGGER.info("hit head!!!");
                return this.damage(source, amount * 2);
            }
            case "left_wing", "right_wing" -> {
                // Cánh nhận ít damage hơn
                PracticeMod.LOGGER.info("hit wing!!!");
                return this.damage(source, amount * 0.5f);
            }
            default -> {
                PracticeMod.LOGGER.info("hit dafault!!!");
                return this.damage(source, amount);
            }
        }
    }

    @Override
    public boolean partHurt(MultiPart<DragonEntity> multiPart, @NotNull DamageSource source, float amount) {
        return true;
    }
}

