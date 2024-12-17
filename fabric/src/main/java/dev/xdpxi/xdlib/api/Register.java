package dev.xdpxi.xdlib.api;

import dev.xdpxi.xdlib.Constants;
import net.minecraft.block.Block;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A utility class for registering various types of game content, including blocks, items, and armor materials.
 */
public class Register {

    /**
     * Registers a block in the game. Optionally registers a corresponding {@link BlockItem}.
     *
     * @param block            The block to be registered.
     * @param name             The unique name (within the mod's namespace) for the block.
     * @param shouldRegisterItem Whether a {@link BlockItem} for the block should also be registered.
     * @return The registered {@link Block}.
     */
    public static Block registerBlock(Block block, String name, boolean shouldRegisterItem) {
        Identifier id = Identifier.of(Constants.MOD_ID, name);

        if (shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Settings());
            Registry.register(Registries.ITEM, id, blockItem);
        }

        return Registry.register(Registries.BLOCK, id, block);
    }

    /**
     * Registers an item in the game.
     *
     * @param item The item to be registered.
     * @param id   The unique name (within the mod's namespace) for the item.
     * @return The registered {@link Item}.
     */
    public static Item registerItem(Item item, String id) {
        Identifier itemID = Identifier.of(Constants.MOD_ID, id);

        return Registry.register(Registries.ITEM, itemID, item);
    }

    /**
     * Registers a custom armor material.
     *
     * @param id                       The unique name (within the mod's namespace) for the armor material.
     * @param defensePoints            A map specifying the defense points for each armor type.
     * @param enchantability           The enchantability of the armor material.
     * @param equipSound               The sound played when equipping armor of this material.
     * @param repairIngredientSupplier A supplier providing the ingredient used to repair the armor.
     * @param toughness                The toughness value of the armor material.
     * @param knockbackResistance      The knockback resistance provided by the armor material.
     * @param dyeable                  Whether the armor material supports dyeing.
     * @return A {@link RegistryEntry} of the registered {@link ArmorMaterial}.
     */
    public static RegistryEntry<ArmorMaterial> registerMaterial(String id, Map<ArmorItem.Type, Integer> defensePoints, int enchantability, RegistryEntry<SoundEvent> equipSound, Supplier<Ingredient> repairIngredientSupplier, float toughness, float knockbackResistance, boolean dyeable) {
        List<ArmorMaterial.Layer> layers = List.of(
                new ArmorMaterial.Layer(Identifier.of(Constants.MOD_ID, id), "", dyeable)
        );

        ArmorMaterial material = new ArmorMaterial(defensePoints, enchantability, equipSound, repairIngredientSupplier, layers, toughness, knockbackResistance);

        material = Registry.register(Registries.ARMOR_MATERIAL, Identifier.of(Constants.MOD_ID, id), material);

        return RegistryEntry.of(material);
    }
}