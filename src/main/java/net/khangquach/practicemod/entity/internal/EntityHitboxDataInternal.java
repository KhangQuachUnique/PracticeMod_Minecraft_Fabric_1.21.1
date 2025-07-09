package net.khangquach.practicemod.entity.internal;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.khangquach.practicemod.entity.api.*;
import net.khangquach.practicemod.entity.platform.Services;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@ApiStatus.Internal
public class EntityHitboxDataInternal<T extends MobEntity & MultiPartEntity<T>> implements EntityHitboxData<T> {
    private final List<MultiPart<T>> parts = new ObjectArrayList<>();
    private final Map<String, MultiPart<T>> partsByRef = new Object2ObjectOpenHashMap<>();
    private final T entity;
    private final AttackBoxData attackBoxData;
    private final AnchorData anchorData;
    private final boolean fixPosOnRefresh;
    private final boolean usesAttackBounds;
    private Box attackBounds = new Box(0, 0, 0, 0, 0, 0);
    private Box cullingBounds = new Box(0, 0, 0, 0, 0, 0);
    private float headRadius;
    private float frustumWidthRadius;
    private float frustumHeight;

    public EntityHitboxDataInternal(T entity, boolean fixPosOnRefresh, boolean usesAttackBounds) {
        this.entity = entity;
        this.attackBoxData = new AttackBoxDataInternal<>(entity);
        this.anchorData = new AnchorDataInternal<>(entity);
        this.fixPosOnRefresh = fixPosOnRefresh;
        this.usesAttackBounds = usesAttackBounds;
        Identifier id = Registries.ENTITY_TYPE.getId(entity.getType());
        List<HitboxData> hitboxData = HitboxDataLoader.HITBOX_DATA.getHitboxes(id);
        if (hitboxData != null && !hitboxData.isEmpty()) {
            spawnHitBoxes(hitboxData);
        }
        makeAttackBounds();
        makeBoundingBoxForCulling();
    }

    private void spawnHitBoxes(List<HitboxData> hitboxesData) {
        float maxFrustumWidthRadius = 0;
        float maxFrustumHeight = 0;
        for (HitboxData hitboxData : hitboxesData) {
            if (hitboxData.isAttackBox()) {
                attackBoxData.addAttackBox(hitboxData.ref(), hitboxData);
            } else if (hitboxData.isAnchor()) {
                anchorData.addAnchor(hitboxData.ref(), hitboxData);
            } else {
                MultiPart<T> part = Services.MULTI_PART.create(entity, hitboxData);
                parts.add(part);
                if (!hitboxData.ref().isBlank()) {
                    partsByRef.put(hitboxData.ref(), part);
                }
                float w = hitboxData.getFrustumWidthRadius();
                if (hitboxData.name().contains("head") && (headRadius == 0 || w > maxFrustumWidthRadius)) {
                    headRadius = w;
                }
                if (w > maxFrustumWidthRadius) {
                    maxFrustumWidthRadius = w;
                }
                float h = hitboxData.getFrustumHeight();
                if (h > maxFrustumHeight) {
                    maxFrustumHeight = h;
                }
            }
        }
        frustumWidthRadius = maxFrustumWidthRadius;
        frustumHeight = maxFrustumHeight;
    }

    @Override
    public AttackBoxData getAttackBoxData() {
        return attackBoxData;
    }

    @Override
    public AnchorData getAnchorData() {
        return anchorData;
    }

    @Override
    public void makeBoundingBoxForCulling() {
        if (hasCustomParts()) {
            cullingBounds = entity.makeBoundingBoxForCulling(frustumWidthRadius, frustumHeight);
        }
    }

    @Override
    public Box getCullingBounds() {
        return cullingBounds;
    }

    @Override
    public void makeAttackBounds() {
        if (!usesAttackBounds) {
            return;
        }
        attackBounds = entity.makeAttackBoundingBox(getHeadRadius() * entity.getScale());
    }

    @Override
    public Box getAttackBounds() {
        return attackBounds;
    }

    @Override
    public float getHeadRadius() {
        return headRadius;
    }

    @Override
    public boolean hasCustomParts() {
        return !parts.isEmpty();
    }

    @Override
    public List<MultiPart<T>> getCustomParts() {
        return parts;
    }

    @Override
    public @Nullable MultiPart<T> getCustomPart(String ref) {
        return partsByRef.get(ref);
    }

    @Override
    public boolean fixPosOnRefresh() {
        return fixPosOnRefresh;
    }
}

