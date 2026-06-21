package dev.xdpxi.xdlib.api.v7;

import dev.xdpxi.xdlib.Constants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

import static dev.xdpxi.xdlib.Common.log;

/**
 * Utility class for registering blocks, items, and armor materials in Minecraft.
 */
public final class Register {

    private static Block test_block;
    private static Item test_item;

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
            test_block = registerBlock(
                    Block::new,
                    BlockBehaviour.Properties.of().sound(SoundType.STONE),
                    "test_block",
                    Constants.MOD_ID
            );
            log.info("Test block registered");
        } catch (Exception e) {
            log.error("Failed block register", e);
        }

        try {
            test_item = registerItem(
                    Item::new,
                    new Item.Properties(),
                    "test_item",
                    Constants.MOD_ID
            );
            log.info("Test item registered");
        } catch (Exception e) {
            log.error("Failed item register", e);
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
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                modId,
                itemId
        );

        Block block = blockFactory.apply(properties);
        block = Registry.register(BuiltInRegistries.BLOCK, id, block);

        BlockItem blockItem = new BlockItem(block, new Item.Properties());
        Registry.register(BuiltInRegistries.ITEM, id, blockItem);

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
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                modId,
                itemId
        );
        Item item = itemFactory.apply(properties);

        return Registry.register(BuiltInRegistries.ITEM, id, item);
    }
}
