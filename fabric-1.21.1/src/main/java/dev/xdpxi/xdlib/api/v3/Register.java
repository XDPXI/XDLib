package dev.xdpxi.xdlib.api.v3;

import dev.xdpxi.xdlib.Main;
import dev.xdpxi.xdlib.util.Log;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

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
                    new Block(AbstractBlock.Settings.create().sounds(BlockSoundGroup.STONE)),
                    "test_block",
                    Main.MOD_ID
            );
        } catch (Exception e) {
            Log.error("[XDLib/Register] - An error occurred while testing registerBlock:", e);
        }

        Log.info("[XDLib/Register] - Testing registerItem...");
        try {
            test_item = Register.registerItem(
                    new Item(new Item.Settings()),
                    "test_item",
                    Main.MOD_ID
            );
        } catch (Exception e) {
            Log.error("[XDLib/Register] - An error occurred while testing registerItem:", e);
        }

        Log.info("[XDLib/Register] - Testing registerMaterial...");
        try {
            test_material = Register.registerMaterial("test_material",
                    Map.of(
                            ArmorItem.Type.HELMET, 3,
                            ArmorItem.Type.CHESTPLATE, 8,
                            ArmorItem.Type.LEGGINGS, 6,
                            ArmorItem.Type.BOOTS, 3
                    ),
                    5,
                    SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
                    () -> Ingredient.ofItems(test_item),
                    0.0F,
                    0.0F,
                    false,
                    Main.MOD_ID);
        } catch (Exception e) {
            Log.error("[XDLib/Register] - An error occurred while testing registerMaterial:", e);
        }
    }

    /**
     * Registers a block and its corresponding block item in the game.
     *
     * @param block  The block to register.
     * @param name   The name of the block.
     * @param MOD_ID The mod ID.
     * @return The registered block.
     */
    public static Block registerBlock(Block block, String name, String MOD_ID) {
        Identifier id = Identifier.of(MOD_ID, name);

        BlockItem blockItem = new BlockItem(block, new Item.Settings());
        Registry.register(Registries.ITEM, id, blockItem);

        return Registry.register(Registries.BLOCK, id, block);
    }

    /**
     * Registers an item in the game.
     *
     * @param item   The item to register.
     * @param id     The ID of the item.
     * @param MOD_ID The mod ID.
     * @return The registered item.
     */
    public static Item registerItem(Item item, String id, String MOD_ID) {
        Identifier itemID = Identifier.of(MOD_ID, id);
        return Registry.register(Registries.ITEM, itemID, item);
    }

    /**
     * Registers an armor material in the game.
     *
     * @param id                       The ID of the armor material.
     * @param defensePoints            A map of armor types to their defense points.
     * @param enchantability           The enchantability of the armor material.
     * @param equipSound               The sound played when equipping the armor.
     * @param repairIngredientSupplier A supplier for the repair ingredient.
     * @param toughness                The toughness of the armor material.
     * @param knockbackResistance      The knockback resistance of the armor material.
     * @param dyeable                  Whether the armor material is dyeable.
     * @param MOD_ID                   The mod ID.
     * @return A RegistryEntry containing the registered ArmorMaterial.
     */
    public static RegistryEntry<ArmorMaterial> registerMaterial(String id, Map<ArmorItem.Type, Integer> defensePoints, int enchantability, RegistryEntry<SoundEvent> equipSound, Supplier<Ingredient> repairIngredientSupplier, float toughness, float knockbackResistance, boolean dyeable, String MOD_ID) {
        List<ArmorMaterial.Layer> layers = List.of(
                new ArmorMaterial.Layer(Identifier.of(MOD_ID, id), "", dyeable)
        );

        ArmorMaterial material = new ArmorMaterial(defensePoints, enchantability, equipSound, repairIngredientSupplier, layers, toughness, knockbackResistance);

        material = Registry.register(Registries.ARMOR_MATERIAL, Identifier.of(MOD_ID, id), material);

        return RegistryEntry.of(material);
    }
}