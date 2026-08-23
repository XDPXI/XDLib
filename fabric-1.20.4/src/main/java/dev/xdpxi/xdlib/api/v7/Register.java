package dev.xdpxi.xdlib.api.v7;

import static dev.xdpxi.xdlib.Common.log;

import dev.xdpxi.xdlib.Constants;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

/** Utility class for registering blocks, items, and armor materials in Minecraft. */
public class Register {

  public static Block test_block;
  public static Item test_item;
  public static ArmorMaterial test_material;

  /**
   * Initializes and tests the registration of a block, an item, and an armor material. This method
   * is used for testing purposes and logs the results of each registration attempt.
   */
  public static void init() {
    try {
      test_block =
          Register.registerBlock(
              Block::new,
              AbstractBlock.Settings.create().sounds(BlockSoundGroup.STONE),
              "test_block",
              Constants.MOD_ID);
    } catch (Exception e) {
      log.error("[XDLib] - An error occurred while testing registerBlock:", e);
    }

    try {
      test_item =
          Register.registerItem(Item::new, new Item.Settings(), "test_item", Constants.MOD_ID);
    } catch (Exception e) {
      log.error("[XDLib] - An error occurred while testing registerItem:", e);
    }

    try {
      test_material =
          Register.registerMaterial(
              "test_material",
              Map.of(
                  ArmorItem.Type.HELMET,
                  3,
                  ArmorItem.Type.CHESTPLATE,
                  8,
                  ArmorItem.Type.LEGGINGS,
                  6,
                  ArmorItem.Type.BOOTS,
                  3),
              5,
              SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
              () -> Ingredient.ofItems(test_item),
              0.0F,
              0.0F);
    } catch (Exception e) {
      log.error("[XDLib] - An error occurred while testing registerMaterial:", e);
    }
  }

  /**
   * Registers a block and its corresponding block item in the game.
   *
   * @param blockFactory A factory function to create a block.
   * @param settings The settings for the block.
   * @param blockId The unique block ID.
   * @param modId The mod ID.
   * @return The registered block.
   */
  public static Block registerBlock(
      Function<AbstractBlock.Settings, Block> blockFactory,
      AbstractBlock.Settings settings,
      String blockId,
      String modId) {
    Identifier id = new Identifier(modId, blockId);
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
   * @param settings The settings for the item.
   * @param itemId The unique item ID.
   * @param modId The mod ID.
   * @return The registered item.
   */
  public static Item registerItem(
      Function<Item.Settings, Item> itemFactory,
      Item.Settings settings,
      String itemId,
      String modId) {
    Identifier itemIdentifier = new Identifier(modId, itemId);
    Item item = itemFactory.apply(settings);
    return Registry.register(Registries.ITEM, itemIdentifier, item);
  }

  /**
   * Registers an armor material in the game.
   *
   * <p>Prior to Minecraft 1.21, ArmorMaterial is a plain interface rather than a registry entry, so
   * instances are simply constructed and referenced directly by ArmorItem.
   *
   * @param id The ID of the armor material (used as the texture name).
   * @param defensePoints A map of armor types to their defense points.
   * @param enchantability The enchantability of the armor material.
   * @param equipSound The sound played when equipping the armor.
   * @param repairIngredientSupplier A supplier for the repair ingredient.
   * @param toughness The toughness of the armor material.
   * @param knockbackResistance The knockback resistance of the armor material.
   * @return The registered ArmorMaterial.
   */
  public static ArmorMaterial registerMaterial(
      String id,
      Map<ArmorItem.Type, Integer> defensePoints,
      int enchantability,
      SoundEvent equipSound,
      Supplier<Ingredient> repairIngredientSupplier,
      float toughness,
      float knockbackResistance) {
    return new ArmorMaterial() {
      @Override
      public int getDurability(ArmorItem.Type type) {
        return 25;
      }

      @Override
      public int getProtection(ArmorItem.Type type) {
        return defensePoints.getOrDefault(type, 0);
      }

      @Override
      public int getEnchantability() {
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
        return id;
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
