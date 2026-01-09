package dev.xdpxi.xdlib.api.v6;

import dev.xdpxi.xdlib.Constants;
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
import java.util.function.Function;
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
        try {
            test_block = Register.registerBlock(
                    Block::new,
                    AbstractBlock.Settings.create().sounds(BlockSoundGroup.STONE),
                    "test_block",
                    Constants.MOD_ID
            );
        } catch (Exception e) {
            Log.error(
                    "[XDLib] - An error occurred while testing registerBlock:",
                    e
            );
        }

        try {
            test_item = Register.registerItem(
                    Item::new,
                    new Item.Settings(),
                    "test_item",
                    Constants.MOD_ID
            );
        } catch (Exception e) {
            Log.error(
                    "[XDLib] - An error occurred while testing registerItem:",
                    e
            );
        }

        try {
            test_material = Register.registerMaterial(
                    "test_material",
                    Map.of(
                            ArmorItem.Type.HELMET,
                            3,
                            ArmorItem.Type.CHESTPLATE,
                            8,
                            ArmorItem.Type.LEGGINGS,
                            6,
                            ArmorItem.Type.BOOTS,
                            3
                    ),
                    5,
                    SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
                    () -> Ingredient.ofItems(test_item),
                    0.0F,
                    0.0F,
                    false,
                    Constants.MOD_ID
            );
        } catch (Exception e) {
            Log.error(
                    "[XDLib] - An error occurred while testing registerMaterial:",
                    e
            );
        }
    }

    /**
     * Registers a block and its corresponding block item in the game.
     *
     * @param blockFactory A factory function to create a block.
     * @param settings     The settings for the block.
     * @param blockId      The unique block ID.
     * @param modId        The mod ID.
     * @return The registered block.
     */
    public static Block registerBlock(
            Function<AbstractBlock.Settings, Block> blockFactory,
            AbstractBlock.Settings settings,
            String blockId,
            String modId
    ) {
        Identifier id = Identifier.of(modId, blockId);
        Block block = blockFactory.apply(settings);
        Registry.register(Registries.BLOCK, id, block);

        BlockItem blockItem = new BlockItem(block, new Item.Settings());
        Registry.register(Registries.ITEM, id, blockItem);

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
    public static Item registerItem(
            Function<Item.Settings, Item> itemFactory,
            Item.Settings settings,
            String itemId,
            String modId
    ) {
        Identifier itemIdentifier = Identifier.of(modId, itemId);
        Item item = itemFactory.apply(settings);
        return Registry.register(Registries.ITEM, itemIdentifier, item);
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
    public static RegistryEntry<ArmorMaterial> registerMaterial(
            String id,
            Map<ArmorItem.Type, Integer> defensePoints,
            int enchantability,
            RegistryEntry<SoundEvent> equipSound,
            Supplier<Ingredient> repairIngredientSupplier,
            float toughness,
            float knockbackResistance,
            boolean dyeable,
            String MOD_ID
    ) {
        List<ArmorMaterial.Layer> layers = List.of(
                new ArmorMaterial.Layer(Identifier.of(MOD_ID, id), "", dyeable)
        );

        ArmorMaterial material = new ArmorMaterial(
                defensePoints,
                enchantability,
                equipSound,
                repairIngredientSupplier,
                layers,
                toughness,
                knockbackResistance
        );

        material = Registry.register(
                Registries.ARMOR_MATERIAL,
                Identifier.of(MOD_ID, id),
                material
        );

        return RegistryEntry.of(material);
    }
}
