package dev.xdpxi.xdlib.api.v7;

import dev.xdpxi.xdlib.Constants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Map;
import java.util.function.Function;

import static dev.xdpxi.xdlib.Common.log;

/**
 * Utility class for registering blocks, items, and armor materials in Minecraft.
 */
public final class Register {

    private Register() {
        throw new UnsupportedOperationException(
                "Register is a utility class and cannot be instantiated."
        );
    }

    /**
     * Initializes and tests the registration of a block, an item, and an armor material.
     * Logs the results of each registration attempt.
     */
    public static void init() {
        try {
            Block testBlock = registerBlock(
                    Block::new,
                    BlockBehaviour.Properties.of().sound(SoundType.STONE),
                    "test_block",
                    Constants.MOD_ID
            );
        } catch (Exception e) {
            log.error("[XDLib] - Failed to register test block:", e);
        }

        try {
            Item testItem = registerItem(
                    Item::new,
                    new Item.Properties(),
                    "test_item",
                    Constants.MOD_ID
            );
        } catch (Exception e) {
            log.error("[XDLib] - Failed to register test armor material:", e);
        }

        try {
            ResourceKey<EquipmentAsset> ARMOR_MATERIAL_KEY = ResourceKey.create(
                    EquipmentAssets.ROOT_ID,
                    Identifier.fromNamespaceAndPath(Constants.MOD_ID, "test_armor_material")
            );
            ArmorMaterial INSTANCE = new ArmorMaterial(
                    1000,
                    Map.of(
                            ArmorType.HELMET,
                            3,
                            ArmorType.CHESTPLATE,
                            8,
                            ArmorType.LEGGINGS,
                            6,
                            ArmorType.BOOTS,
                            3
                    ),
                    5,
                    SoundEvents.ARMOR_EQUIP_IRON,
                    0.0F,
                    0.0F,
                    null,
                    ARMOR_MATERIAL_KEY
            );
        } catch (Exception e) {
            log.error("[XDLib] - Failed to register test armor material:", e);
        }
    }

    /**
     * Registers a block and optionally its corresponding block item.
     *
     * @param blockFactory The function to create the block.
     * @param properties   The block properties.
     * @param itemId       The item ID.
     * @param modId        The mod ID.
     * @return The registered block.
     */
    public static Block registerBlock(
            Function<BlockBehaviour.Properties, Block> blockFactory,
            BlockBehaviour.Properties properties,
            String itemId,
            String modId
    ) {
        ResourceKey<Block> blockKey = createBlockKey(modId, itemId);
        Block block = blockFactory.apply(properties.setId(blockKey));
        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);

        ResourceKey<Item> itemKey = createItemKey(modId, itemId);
        BlockItem blockItem = new BlockItem(
                block,
                new Item.Properties().setId(itemKey)
        );
        Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);

        return block;
    }

    /**
     * Registers an item in the game.
     *
     * @param itemFactory A factory function to create an item.
     * @param properties  The properties for the item.
     * @param itemId      The unique item ID.
     * @param modId       The mod ID.
     * @return The registered item.
     */
    public static Item registerItem(
            Function<Item.Properties, Item> itemFactory,
            Item.Properties properties,
            String itemId,
            String modId
    ) {
        ResourceKey<Item> itemKey = createItemKey(modId, itemId);
        Item item = itemFactory.apply(properties.setId(itemKey));
        return Registry.register(BuiltInRegistries.ITEM, itemKey, item);
    }

    private static ResourceKey<Block> createBlockKey(
            String modId,
            String itemId
    ) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(modId, itemId));
    }

    private static ResourceKey<Item> createItemKey(
            String modId,
            String itemId
    ) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(modId, itemId));
    }
}
