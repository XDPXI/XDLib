package dev.xdpxi.xdlib.api.v5;

import dev.xdpxi.xdlib.Main;
import dev.xdpxi.xdlib.util.Log;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.function.Function;

/**
 * Utility class for registering blocks, items, and armor materials in Minecraft.
 */
public class Register {
    public static Block test_block;
    public static Item test_item;
    public static RegistryEntry<ArmorMaterial> test_material;

    /**
     * Initializes and tests the registration of a block, an item, and an armor material.
     * This method is used for testing purposes and logs the results of each registration attempt.
     */
    public static void init() {
        Log.info("[XDLib/Register] - Testing registerBlock...");
        try {
            test_block = Register.registerBlock(
                    Block::new,
                    AbstractBlock.Settings.create().sounds(BlockSoundGroup.STONE),
                    true,
                    "test_block",
                    Main.MOD_ID
            );
        } catch (Exception e) {
            Log.error("[XDLib/Register] - An error occurred while testing registerBlock:", e);
        }

        Log.info("[XDLib/Register] - Testing registerItem...");
        try {
            test_item = Register.registerItem(
                    Item::new,
                    new Item.Settings(),
                    "test_item",
                    Main.MOD_ID
            );
        } catch (Exception e) {
            Log.error("[XDLib/Register] - An error occurred while testing registerItem:", e);
        }

        Log.info("[XDLib/Register] - Testing registerMaterial...");
        Log.error("[XDLib/Register] - This feature is not yet implemented.");
    }

    /**
     * Registers a block and its corresponding block item in the game.
     *
     * @param blockFactory The factory function to create the block.
     * @param settings     The block settings.
     * @param registerItem Whether to register the block as an item.
     * @param itemId       The item ID.
     * @param modId        The mod ID.
     * @return The registered block.
     */
    public static Block registerBlock(Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, boolean registerItem, String itemId, String modId) {
        RegistryKey<Block> blockKey = keyOfBlock(modId, itemId);
        Block block = blockFactory.apply(settings.registryKey(blockKey));

        if (registerItem) {
            RegistryKey<Item> itemKey = keyOfItem(modId, itemId);

            BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey));
            Registry.register(Registries.ITEM, itemKey, blockItem);
        }

        return Registry.register(Registries.BLOCK, blockKey, block);
    }

    private static RegistryKey<Block> keyOfBlock(String modId, String itemId) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(modId, itemId));
    }

    private static RegistryKey<Item> keyOfItem(String modId, String itemId) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(modId, itemId));
    }

    /**
     * Registers an item in the game.
     *
     * @param itemFactory A factory function to create an Item using provided settings.
     * @param settings    The settings used to configure the item.
     * @param itemId      The unique ID of the item.
     * @param modId       The mod's namespace ID.
     * @return The registered item.
     */
    public static Item registerItem(Function<Item.Settings, Item> itemFactory, Item.Settings settings, String itemId, String modId) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(modId, itemId));
        Item item = itemFactory.apply(settings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }

    /**
     * Creates a new ArmorItem for the specified equipment type and material,
     * registers it, and returns the registered item.
     *
     * @param equipmentType The type of the equipment (e.g., helmet, chestplate, etc.).
     * @param material      The material of the armor (e.g., iron, diamond, etc.).
     * @param itemId        The unique identifier for the item.
     * @param modId         The identifier for the mod to which the item belongs.
     * @return The registered ArmorItem.
     */
    public static Item registerArmorItem(EquipmentType equipmentType, ArmorMaterial material, String itemId, String modId) {
        return registerItem(
                settings -> new ArmorItem(material, equipmentType, settings),
                new Item.Settings().maxDamage(equipmentType.getMaxDamage(material.durability())),
                itemId,
                modId
        );
    }
}