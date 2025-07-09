package net.khangquach.practicemod.entity.client;

import net.khangquach.practicemod.PracticeMod;
import net.khangquach.practicemod.entity.custom.HedgehogEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class HedgehogModel extends GeoModel<HedgehogEntity> {

    @Override
    public Identifier getModelResource(HedgehogEntity hedgehogEntity) {
        return Identifier.of(PracticeMod.MOD_ID, "geo/hedgehog.geo.json");
    }

    @Override
    public Identifier getTextureResource(HedgehogEntity hedgehogEntity) {
        return Identifier.of(PracticeMod.MOD_ID, "textures/entity/hedgehog.png");
    }

    @Override
    public Identifier getAnimationResource(HedgehogEntity hedgehogEntity) {
        return Identifier.of(PracticeMod.MOD_ID, "animations/hedgehog.animation.json");
    }
}
