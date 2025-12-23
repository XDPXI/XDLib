package dev.xdpxi.xdlib.registry;

import dev.xdpxi.xdlib.util.EventManager;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Objects;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("realevents");

    // Christmas Items
    public static final DeferredItem<Item> CANDY_CANE;
    public static final DeferredItem<ArmorItem> CANDY_HELMET;
    public static final DeferredItem<ArmorItem> CANDY_CHESTPLATE;
    public static final DeferredItem<ArmorItem> CANDY_LEGGINGS;
    public static final DeferredItem<ArmorItem> CANDY_BOOTS;
    public static final DeferredItem<Item> PRESENT;
    public static final DeferredItem<Item> CHRISTMAS_TREE;

    // Halloween Items
    public static final DeferredItem<Item> CANDY;

    // Birthday Items
    public static final DeferredItem<Item> BLAHAJ;

    static {
        EventManager.EventType currentEvent = EventManager.getCurrentEventType();

        // Register Christmas items only during Christmas
        if (currentEvent == EventManager.EventType.CHRISTMAS) {
            CANDY_CANE = ITEMS.register("candy_cane",
                    () -> new Item(new Item.Properties()
                            .food(new FoodProperties.Builder()
                                    .alwaysEdible()
                                    .nutrition(5)
                                    .saturationModifier(0.5f)
                                    .build())));

            CANDY_HELMET = ITEMS.register("candy_cane_helmet",
                    () -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.HELMET, new Item.Properties()));

            CANDY_CHESTPLATE = ITEMS.register("candy_cane_chestplate",
                    () -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

            CANDY_LEGGINGS = ITEMS.register("candy_cane_leggings",
                    () -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.LEGGINGS, new Item.Properties()));

            CANDY_BOOTS = ITEMS.register("candy_cane_boots",
                    () -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.BOOTS, new Item.Properties()));

            // BlockItems are registered here and reference the blocks
            PRESENT = ITEMS.register("present",
                    () -> new BlockItem(Objects.requireNonNull(ModBlocks.getPresent()), new Item.Properties()));

            CHRISTMAS_TREE = ITEMS.register("christmas_tree",
                    () -> new BlockItem(Objects.requireNonNull(ModBlocks.getChristmasTree()), new Item.Properties()));
        } else {
            CANDY_CANE = null;
            CANDY_HELMET = null;
            CANDY_CHESTPLATE = null;
            CANDY_LEGGINGS = null;
            CANDY_BOOTS = null;
            PRESENT = null;
            CHRISTMAS_TREE = null;
        }

        // Register Halloween items only during Halloween
        if (currentEvent == EventManager.EventType.HALLOWEEN) {
            CANDY = ITEMS.register("candy",
                    () -> new Item(new Item.Properties()
                            .food(new FoodProperties.Builder()
                                    .alwaysEdible()
                                    .nutrition(2)
                                    .saturationModifier(0.5f)
                                    .build())
                            .stacksTo(16)));
        } else {
            CANDY = null;
        }

        // Register Birthday items only during Birthday
        if (currentEvent == EventManager.EventType.BIRTHDAY) {
            BLAHAJ = ITEMS.register("blahaj",
                    () -> new Item(new Item.Properties()));
        } else {
            BLAHAJ = null;
        }
    }
}