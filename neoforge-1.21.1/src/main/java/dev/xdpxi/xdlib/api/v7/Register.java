package dev.xdpxi.xdlib.api.v7;

import dev.xdpxi.xdlib.Constants;
import dev.xdpxi.xdlib.util.Log;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Utility class for registering blocks, items, and armor materials in Minecraft.
 */
public final class Register {

    private static Block test_block;
    private static Item test_item;
    private static ArmorMaterial test_material;

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
            Log.info("Test block registered");
        } catch (Exception e) {
            Log.error("Failed block register", e);
        }

        try {
            test_item = registerItem(
                Item::new,
                new Item.Properties(),
                "test_item",
                Constants.MOD_ID
            );
            Log.info("Test item registered");
        } catch (Exception e) {
            Log.error("Failed item register", e);
        }

        try {
            test_material = registerMaterial(
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
                SoundEvents.ARMOR_EQUIP_LEATHER,
                () -> Ingredient.of(test_item),
                0.0F,
                0.0F,
                false,
                Constants.MOD_ID
            );
            Log.info("Armor material registered");
        } catch (Exception e) {
            Log.error("Failed armor material register", e);
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

    /**
     * Registers an armor material in the game.
     *
     * @param material_id              The unique ID for the armor material.
     * @param defensePoints            A map specifying the defense points for each armor type.
     * @param enchantability           The enchantability of the armor material.
     * @param equipSound               The sound event to play when the armor is equipped.
     * @param repairIngredientSupplier A supplier for the ingredient used to repair the armor.
     * @param toughness                The toughness of the armor material.
     * @param knockbackResistance      The knockback resistance provided by the armor material.
     * @param dyeable                  Whether the armor material can be dyed.
     * @param MOD_ID                   The mod ID under which to register the material.
     * @return The registered armor material.
     */
    public static ArmorMaterial registerMaterial(
        String material_id,
        Map<ArmorItem.Type, Integer> defensePoints,
        int enchantability,
        Holder<SoundEvent> equipSound,
        Supplier<Ingredient> repairIngredientSupplier,
        float toughness,
        float knockbackResistance,
        boolean dyeable,
        String MOD_ID
    ) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
            MOD_ID,
            material_id
        );

        List<ArmorMaterial.Layer> layers = List.of(
            new ArmorMaterial.Layer(id, "", dyeable)
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

        return Registry.register(
            BuiltInRegistries.ARMOR_MATERIAL,
            id,
            material
        );
    }
}
