package dev.xdpxi.xdlib.api.v6;

import dev.xdpxi.xdlib.Constants;
import dev.xdpxi.xdlib.Main;
import dev.xdpxi.xdlib.util.Log;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.Map;
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
        try {
            Log.info("[XDLib] - Registering test block...");
            Block testBlock = registerBlock(
                    Block::new,
                    AbstractBlock.Settings.create().sounds(BlockSoundGroup.STONE),
                    "test_block",
                    Constants.MOD_ID
            );
        } catch (Exception e) {
            Log.error("[XDLib] - Failed to register test block:", e);
        }

        try {
            Log.info("[XDLib] - Registering test item...");
            Item testItem = registerItem(
                    Item::new,
                    new Item.Settings(),
                    "test_item",
                    Constants.MOD_ID
            );
        } catch (Exception e) {
            Log.error("[XDLib] - Failed to register test armor material:", e);
        }

        try {
            Log.info("[XDLib] - Registering test armor material...");
            RegistryKey<EquipmentAsset> ARMOR_MATERIAL_KEY = RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, Identifier.of(Constants.MOD_ID, "test_armor_material"));
            ArmorMaterial INSTANCE = new ArmorMaterial(
                    1000,
                    Map.of(
                            EquipmentType.HELMET, 3,
                            EquipmentType.CHESTPLATE, 8,
                            EquipmentType.LEGGINGS, 6,
                            EquipmentType.BOOTS, 3
                    ),
                    5,
                    SoundEvents.ITEM_ARMOR_EQUIP_IRON,
                    0.0F,
                    0.0F,
                    null,
                    ARMOR_MATERIAL_KEY
            );
        } catch (Exception e) {
            Log.error("[XDLib] - Failed to register test armor material:", e);
        }
    }

    /**
     * Registers a block and optionally its corresponding block item.
     *
     * @param blockFactory The function to create the block.
     * @param settings     The block settings.
     * @param itemId       The item ID.
     * @param modId        The mod ID.
     * @return The registered block.
     */
    public static Block registerBlock(Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, String itemId, String modId) {
        RegistryKey<Block> blockKey = createBlockKey(modId, itemId);
        Block block = blockFactory.apply(settings.registryKey(blockKey));
        Registry.register(Registries.BLOCK, blockKey, block);

        RegistryKey<Item> itemKey = createItemKey(modId, itemId);
        BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, blockItem);

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