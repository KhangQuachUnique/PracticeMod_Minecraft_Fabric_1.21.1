package net.khangquach.practicemod.item;

import net.khangquach.practicemod.PracticeMod;
import net.khangquach.practicemod.entity.ModEntities;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item HEDGEHOG_SPAWN_EGG = registerItems("hedgehog_spawn_egg",
            new SpawnEggItem(ModEntities.HEDGEHOG, 0xFFFFFF, 0x000000, new Item.Settings()));

    private static Item registerItems(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(PracticeMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        PracticeMod.LOGGER.info("Registering Mod Items for ", PracticeMod.MOD_ID);
    }
}
