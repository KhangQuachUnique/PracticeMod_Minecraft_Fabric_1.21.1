package net.khangquach.practicemod.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.khangquach.practicemod.PracticeMod;
import net.khangquach.practicemod.entity.custom.*;
import net.minecraft.entity.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;

import java.util.Random;

public class ModEntities {
    public static final EntityType<HedgehogEntity> HEDGEHOG = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(PracticeMod.MOD_ID, "hedgehog"),
            EntityType.Builder.create(HedgehogEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.7f, 0.65f)
                    .build());

    public static final EntityType<DragonEntity> DRAGON = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(PracticeMod.MOD_ID, "dragon"),
            EntityType.Builder.create(DragonEntity::new, SpawnGroup.CREATURE)
                    .dimensions(3f,3f)
                    .build());


    public static void registerModEntities() {
        PracticeMod.LOGGER.info("Registering mods entities for ", PracticeMod.MOD_ID);
    }
}
