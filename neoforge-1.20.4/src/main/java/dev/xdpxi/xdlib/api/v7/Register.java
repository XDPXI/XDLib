package dev.xdpxi.xdlib.api.v7;

import dev.xdpxi.xdlib.Constants;
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

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static dev.xdpxi.xdlib.Common.log;

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
                    Constants.MOD_ID
            );
            log.info("Armor material registered");
        } catch (Exception e) {
            log.error("Failed armor material register", e);
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
        ResourceLocation id = new ResourceLocation(modId, itemId);

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
        ResourceLocation id = new ResourceLocation(modId, itemId);
        Item item = itemFactory.apply(properties);

        return Registry.register(BuiltInRegistries.ITEM, id, item);
    }

    /**
     * Registers an armor material in the game.
     * <p>
     * Prior to Minecraft 1.20.5, ArmorMaterial is a plain interface rather than a registry
     * entry, so instances are simply constructed and referenced directly by ArmorItem.
     *
     * @param id                       The ID of the armor material (used as the texture name).
     * @param defensePoints            A map specifying the defense points for each armor type.
     * @param enchantability           The enchantability of the armor material.
     * @param equipSound               The sound event to play when the armor is equipped.
     * @param repairIngredientSupplier A supplier for the ingredient used to repair the armor.
     * @param toughness                The toughness of the armor material.
     * @param knockbackResistance      The knockback resistance provided by the armor material.
     * @param modId                    The mod ID under which to register the material.
     * @return The registered armor material.
     */
    public static ArmorMaterial registerMaterial(
            String id,
            Map<ArmorItem.Type, Integer> defensePoints,
            int enchantability,
            SoundEvent equipSound,
            Supplier<Ingredient> repairIngredientSupplier,
            float toughness,
            float knockbackResistance,
            String modId
    ) {
        return new ArmorMaterial() {
            @Override
            public int getDurabilityForType(ArmorItem.Type type) {
                return 25;
            }

            @Override
            public int getDefenseForType(ArmorItem.Type type) {
                return defensePoints.getOrDefault(type, 0);
            }

            @Override
            public int getEnchantmentValue() {
                return enchantability;
            }

            @Override
            public SoundEvent getEquipSound() {
                return equipSound;
            }

            @Override
            public Ingredient getRepairIngredient() {
                return repairIngredientSupplier.get();
            }

            @Override
            public String getName() {
                return modId + ":" + id;
            }

            @Override
            public float getToughness() {
                return toughness;
            }

            @Override
            public float getKnockbackResistance() {
                return knockbackResistance;
            }
        };
    }
}
