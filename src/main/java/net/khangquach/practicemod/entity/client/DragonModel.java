package net.khangquach.practicemod.entity.client;

import net.khangquach.practicemod.PracticeMod;
import net.khangquach.practicemod.entity.custom.DragonEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.model.GeoModel;

class DragonModel extends GeoModel<DragonEntity> {
    @Override
    public Identifier getModelResource(DragonEntity object) {
        return Identifier.of("practicemod", "geo/dragon.geo.json");
    }

    @Override
    public Identifier getTextureResource(DragonEntity object) {
        return Identifier.of("practicemod", "textures/entity/dragon.png");
    }

    @Override
    public Identifier getAnimationResource(DragonEntity object) {
        return Identifier.of("practicemod", "animations/dragon.animation.json");
    }
}
