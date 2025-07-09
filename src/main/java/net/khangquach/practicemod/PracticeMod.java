package net.khangquach.practicemod;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.khangquach.practicemod.entity.ModEntities;
import net.khangquach.practicemod.entity.custom.DragonEntity;
import net.khangquach.practicemod.entity.custom.HedgehogEntity;
import net.khangquach.practicemod.item.ModItemGroups;
import net.khangquach.practicemod.item.ModItems;
import net.khangquach.practicemod.world.gen.ModWorldGeneration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PracticeMod implements ModInitializer {
	public static final String MOD_ID = "practicemod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModItemGroups.registerItemGroups();
		ModWorldGeneration.generateWorldModGen();

		ModEntities.registerModEntities();
		FabricDefaultAttributeRegistry.register(ModEntities.HEDGEHOG, HedgehogEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.DRAGON, DragonEntity.createAttributes());
	}
}