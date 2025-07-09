package net.khangquach.practicemod.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.khangquach.practicemod.PracticeMod;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static  final ItemGroup PRACTICE_ITEMS_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(PracticeMod.MOD_ID,"practice_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.HEDGEHOG_SPAWN_EGG))
                    .displayName(Text.translatable("itemGroup.practice_mod"))
                    .entries(((displayContext, entries) -> {
                        entries.add(ModItems.HEDGEHOG_SPAWN_EGG);
                    }))
                    .build());

    public static void registerItemGroups() {
        PracticeMod.LOGGER.info("Registering Item Groups for " + PracticeMod.MOD_ID);
    }
}
