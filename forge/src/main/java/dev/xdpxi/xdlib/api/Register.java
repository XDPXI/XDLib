package dev.xdpxi.xdlib.api;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * A utility class for registering blocks and items using Forge's {@link DeferredRegister}.
 * This class simplifies the registration process for both blocks and their associated {@link BlockItem}s.
 */
public class Register {

    /**
     * A {@link DeferredRegister} instance for registering items.
     */
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "xdlib");

    /**
     * A {@link DeferredRegister} instance for registering blocks.
     */
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "xdlib");

    /**
     * Registers a block and its corresponding {@link BlockItem}.
     *
     * @param name          The unique name (within the mod's namespace) for the block.
     * @param blockSupplier A supplier that provides the block instance to be registered.
     * @return A {@link RegistryObject} representing the registered block.
     */
    public static RegistryObject<Block> registerBlock(String name, Supplier<Block> blockSupplier) {
        RegistryObject<Block> block = BLOCKS.register(name, blockSupplier);
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    /**
     * Registers an item.
     *
     * @param name          The unique name (within the mod's namespace) for the item.
     * @param itemSupplier  A supplier that provides the item instance to be registered.
     * @return A {@link RegistryObject} representing the registered item.
     */
    public static RegistryObject<Item> registerItem(String name, Supplier<Item> itemSupplier) {
        return ITEMS.register(name, itemSupplier);
    }
}