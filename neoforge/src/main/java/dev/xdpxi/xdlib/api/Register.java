package dev.xdpxi.xdlib.api;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * A utility class for registering blocks and items using NeoForge's {@link DeferredRegister}.
 * This class provides methods to streamline the registration of blocks, items, and {@link BlockItem}s.
 */
public class Register {

    /**
     * A {@link DeferredRegister} instance for registering items.
     */
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, "xdlib");

    /**
     * A {@link DeferredRegister} instance for registering blocks.
     */
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, "xdlib");

    /**
     * Registers a block and its corresponding {@link BlockItem}.
     *
     * @param name          The unique name (within the mod's namespace) for the block.
     * @param blockSupplier A supplier that provides the block instance to be registered.
     */
    public static void registerBlock(String name, Supplier<Block> blockSupplier) {
        Block block = blockSupplier.get();
        BLOCKS.register(name, () -> block);
        ITEMS.register(name, () -> new BlockItem(block, new Item.Properties()));
    }

    /**
     * Registers an item.
     *
     * @param name          The unique name (within the mod's namespace) for the item.
     * @param itemSupplier  A supplier that provides the item instance to be registered.
     */
    public static void registerItem(String name, Supplier<Item> itemSupplier) {
        ITEMS.register(name, itemSupplier);
    }
}