package net.khangquach.practicemod.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.khangquach.practicemod.entity.ModEntities;
import net.khangquach.practicemod.entity.custom.HedgehogEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;

public class ModEntitySpawns {
    public static void addSpawns() {
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(BiomeKeys.TAIGA, BiomeKeys.SNOWY_TAIGA),
                SpawnGroup.CREATURE,
                ModEntities.HEDGEHOG,
                7,  // weight (trọng số)
                2,   // min group size
                4    // max group size
        );

        SpawnRestriction.register(
                ModEntities.HEDGEHOG,
                SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                HedgehogEntity::canSpawn
        );
    }
}
