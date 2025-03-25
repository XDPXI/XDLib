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
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.function.Function;

/**
 * Utility class for registering blocks, items, and armor materials in Minecraft.
 */
public final class Register {
    private Register() {
        throw new UnsupportedOperationException("Register is a utility class and cannot be instantiated.");
    }

    /**
     * Initializes and tests the registration of a block, an item, and an armor material.
     * Logs the results of each registration attempt.
     */
    public static void init() {
        Log.info("[XDLib/Register] - Initializing test registrations...");

        try {
            Log.info("[XDLib/Register] - Registering test block...");
            Block testBlock = registerBlock(
                    Block::new,
                    AbstractBlock.Settings.create().sounds(BlockSoundGroup.STONE),
                    true,
                    "test_block",
                    Main.MOD_ID
            );
        } catch (Exception e) {
            Log.error("[XDLib/Register] - Failed to register test block:", e);
        }

        try {
            Log.info("[XDLib/Register] - Registering test item...");
            Item testItem = registerItem(
                    Item::new,
                    new Item.Settings(),
                    "test_item",
                    Main.MOD_ID
            );
        } catch (Exception e) {
            Log.error("[XDLib/Register] - Failed to register test item:", e);
        }

        Log.warn("[XDLib/Register] - Armor material registration is not yet implemented.");
    }

    /**
     * Registers a block and optionally its corresponding block item.
     *
     * @param blockFactory The function to create the block.
     * @param settings     The block settings.
     * @param registerItem Whether to register the block as an item.
     * @param itemId       The item ID.
     * @param modId        The mod ID.
     * @return The registered block.
     */
    public static Block registerBlock(Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, boolean registerItem, String itemId, String modId) {
        RegistryKey<Block> blockKey = createBlockKey(modId, itemId);
        Block block = blockFactory.apply(settings.registryKey(blockKey));
        Registry.register(Registries.BLOCK, blockKey, block);

        if (registerItem) {
            RegistryKey<Item> itemKey = createItemKey(modId, itemId);
            BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey));
            Registry.register(Registries.ITEM, itemKey, blockItem);
        }

        return block;
    }

    /**
     * Registers an item in the game.
     *
     * @param itemFactory A factory function to create an item.
     * @param settings    The settings for the item.
     * @param itemId      The unique item ID.
     * @param modId       The mod ID.
     * @return The registered item.
     */
    public static Item registerItem(Function<Item.Settings, Item> itemFactory, Item.Settings settings, String itemId, String modId) {
        RegistryKey<Item> itemKey = createItemKey(modId, itemId);
        Item item = itemFactory.apply(settings.registryKey(itemKey));
        return Registry.register(Registries.ITEM, itemKey, item);
    }

    /**
     * Registers an armor item.
     *
     * @param equipmentType The type of armor (e.g., helmet, chestplate, etc.).
     * @param material      The material of the armor.
     * @param itemId        The unique item ID.
     * @param modId         The mod ID.
     * @return The registered armor item.
     */
    public static Item registerArmorItem(EquipmentType equipmentType, ArmorMaterial material, String itemId, String modId) {
        return registerItem(
                settings -> new ArmorItem(material, equipmentType, settings),
                new Item.Settings().maxDamage(equipmentType.getMaxDamage(material.durability())),
                itemId,
                modId
        );
    }

    private static RegistryKey<Block> createBlockKey(String modId, String itemId) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(modId, itemId));
    }

    private static RegistryKey<Item> createItemKey(String modId, String itemId) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(modId, itemId));
    }
}