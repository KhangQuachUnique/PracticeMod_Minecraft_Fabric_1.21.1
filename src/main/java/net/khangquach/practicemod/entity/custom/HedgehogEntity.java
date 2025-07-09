package net.khangquach.practicemod.entity.custom;

import net.khangquach.practicemod.entity.ModEntities;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.stream.StreamSupport;

public class HedgehogEntity extends AnimalEntity implements GeoEntity {

    private static final int SPIKE_DURATION = 100;
    private static final int DAMAGE_COOLDOWN = 10;
    private static final float SPIKE_CONTACT_DAMAGE = 2.0F;
    private static final double CONTACT_RANGE = 0.3;
    private int spikeTicks = 0;
    private int lastDamageTick = 0;
    
    private AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public HedgehogEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 8) // Max health
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f); // Movement speed
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.25) {
            @Override
            public boolean canStart() {
                return super.canStart() && !HedgehogEntity.this.isSpiked();
            }
        });
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0) {
            @Override
            public boolean canStart() {
                return super.canStart() && !HedgehogEntity.this.isSpiked();
            }
        });
        this.goalSelector.add(3, new TemptGoal(this, 1.1, Ingredient.ofItems(Items.SWEET_BERRIES), false) {
            @Override
            public boolean canStart() {
                return super.canStart() && !HedgehogEntity.this.isSpiked();
            }
        });
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 1.0) {
            @Override
            public boolean canStart() {
                return super.canStart() && !HedgehogEntity.this.isSpiked();
            }
        });
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == Items.SWEET_BERRIES; // No breeding
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.HEDGEHOG.create(world); // Create baby hedgehog
    }

    public boolean isSpiked() {
        return this.spikeTicks > 0;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean result = super.damage(source, amount);
        if (!getWorld().isClient && result) {
            if(!isSpiked()) {
                this.spikeTicks = SPIKE_DURATION;
                this.triggerAnim("spike_controller", "spike");
            }
            if (source.getAttacker() instanceof LivingEntity attacker) {
                attacker.damage(getDamageSources().cactus(), SPIKE_CONTACT_DAMAGE);
                lastDamageTick = (int) getWorld().getTime();
            }
        }
        return result;
    }

    @Override
    public void tick() {
        super.tick();
        if (isSpiked()) {
            spikeTicks--;
            this.getNavigation().stop();
            if (!getWorld().isClient && getWorld().getTime() % 5 == 0) {
                int currentTick = (int) getWorld().getTime();
                if (currentTick - lastDamageTick >= DAMAGE_COOLDOWN) {
                    Box collisionBox = getBoundingBox().expand(CONTACT_RANGE);
                    getWorld().getEntitiesByClass(LivingEntity.class, collisionBox, entity -> entity != this && !(entity instanceof HedgehogEntity))
                            .forEach(entity -> {
                                entity.damage(getDamageSources().cactus(), SPIKE_CONTACT_DAMAGE);
                                lastDamageTick = currentTick;
                            });
                }
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "main_controller", 5, this::predicate));
        controllerRegistrar.add(new AnimationController<>(
                this, "spike_controller", 0, state -> PlayState.STOP
        ).triggerableAnim("spike", RawAnimation.begin().then("animation.hedgehog.spike", Animation.LoopType.PLAY_ONCE)));
    }

    private PlayState predicate(software.bernie.geckolib.animation.AnimationState<HedgehogEntity> hedgehogEntityAnimationState) {
        if (hedgehogEntityAnimationState.getAnimatable().isSpiked()) {
            return PlayState.STOP;
        }

        if (hedgehogEntityAnimationState.isMoving()) {
            hedgehogEntityAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.hedgehog.walk", Animation.LoopType.LOOP));
        } else {
            hedgehogEntityAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.hedgehog.idle", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public static boolean canSpawn(EntityType<HedgehogEntity> type, ServerWorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
        boolean hasBerryBushNearby = StreamSupport.stream(
                BlockPos.iterateOutwards(pos, 3, 1, 3).spliterator(), false
        ).anyMatch(p -> world.getBlockState(p).isOf(Blocks.SWEET_BERRY_BUSH));

        boolean lightOk = world.getBaseLightLevel(pos, 0) > 8;
        boolean groundOk = world.getBlockState(pos.down()).isIn(BlockTags.ANIMALS_SPAWNABLE_ON);

        return hasBerryBushNearby && lightOk && groundOk;
    }
}