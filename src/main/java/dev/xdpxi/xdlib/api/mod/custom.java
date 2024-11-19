package dev.xdpxi.xdlib.api.mod;

import dev.xdpxi.xdlib.api.mod.customClass.custom0;
import dev.xdpxi.xdlib.api.mod.customClass.custom1;
import dev.xdpxi.xdlib.api.mod.customClass.custom2;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.List;

public class custom {
    private static final String minecraftVersion = FabricLoader.getInstance().getModContainer("minecraft")
            .map(container -> container.getMetadata().getVersion().getFriendlyString())
            .orElse("Unknown");

    public static void ItemGroup(String itemGroupID, String modID, Item itemIconID, List<Item> itemsToAdd) {
        if (itemGroupID == null || modID == null) {
            throw new IllegalArgumentException("itemGroupID or modID is null");
        }

        try {
            if (minecraftVersion.equals("1.21") || minecraftVersion.equals("1.21.1")) {
                custom1.ItemGroup(itemGroupID, modID, itemIconID, itemsToAdd);
            } else if (minecraftVersion.equals("1.21.2") || minecraftVersion.equals("1.21.3")) {
                //custom2.ItemGroup(itemGroupID, modID, itemIconID, itemsToAdd);
            } else if (minecraftVersion.equals("1.20.5") || minecraftVersion.equals("1.20.6")) {
                custom0.ItemGroup(itemGroupID, modID, itemIconID, itemsToAdd);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create ItemGroup: " + e.getMessage(), e);
        }
    }

    public static void AddToItemGroup(String itemGroupID, String modID, List<Item> itemsToAdd) {
        ItemGroup(itemGroupID, modID, null, itemsToAdd);
    }

    public static Item Item(String itemID, String modID, RegistryKey<ItemGroup> itemGroup) {
        if (itemID == null || modID == null) {
            throw new IllegalArgumentException("itemID or modID is null");
        }

        try {
            if (minecraftVersion.equals("1.21") || minecraftVersion.equals("1.21.1")) {
                return custom1.Item(itemID, modID, itemGroup);
            } else if (minecraftVersion.equals("1.21.2") || minecraftVersion.equals("1.21.3")) {
                //return custom2.Item(itemID, modID, itemGroup);
            } else if (minecraftVersion.equals("1.20.5") || minecraftVersion.equals("1.20.6")) {
                return custom0.Item(itemID, modID, itemGroup);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Item: " + e.getMessage(), e);
        }

        return null;
    }

    public static Item Item(String itemID, String modID) {
        return Item(itemID, modID, null);
    }

    public static BlockItem Block(String blockID, String modID, RegistryKey<ItemGroup> itemGroup) {
        if (blockID == null || modID == null) {
            throw new IllegalArgumentException("blockID or modID is null");
        }

        try {
            if (minecraftVersion.equals("1.21") || minecraftVersion.equals("1.21.1")) {
                return custom1.Block(blockID, modID, itemGroup);
            } else if (minecraftVersion.equals("1.21.2") || minecraftVersion.equals("1.21.3")) {
                //return custom2.Block(blockID, modID, itemGroup);
            } else if (minecraftVersion.equals("1.20.5") || minecraftVersion.equals("1.20.6")) {
                return custom0.Block(blockID, modID, itemGroup);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Block: " + e.getMessage(), e);
        }

        return null;
    }

    public static BlockItem Block(String blockID, String modID) {
        return Block(blockID, modID, null);
    }

    public static Item Weapon(String weaponID, String modID, ToolMaterial material, RegistryKey<ItemGroup> itemGroup) {
        if (weaponID == null || modID == null || material == null) {
            throw new IllegalArgumentException("weaponID, modID, or material is null");
        }

        try {
            if (minecraftVersion.equals("1.21") || minecraftVersion.equals("1.21.1")) {
                return custom1.Weapon(weaponID, modID, material, itemGroup);
            } else if (minecraftVersion.equals("1.21.2") || minecraftVersion.equals("1.21.3")) {
                //return custom2.Weapon(weaponID, modID, material, itemGroup);
            }else if (minecraftVersion.equals("1.20.5") || minecraftVersion.equals("1.20.6")) {
                return custom0.Weapon(weaponID, modID, material, itemGroup);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Weapon: " + e.getMessage(), e);
        }

        return null;
    }

    public static Item Weapon(String weaponID, String modID, ToolMaterial material) {
        return Weapon(weaponID, modID, material, null);
    }

    public static Item Armor(String armorID, String modID, RegistryEntry<ArmorMaterial> armorType, ArmorItem.Type armorPart, RegistryKey<ItemGroup> itemGroup) {
        if (armorID == null || modID == null || armorType == null || armorPart == null) {
            throw new IllegalArgumentException("armorID, modID, armorType, or armorPart is null");
        }

        try {
            if (minecraftVersion.equals("1.21") || minecraftVersion.equals("1.21.1")) {
                return custom1.Armor(armorID, modID, armorType, armorPart, itemGroup);
            } else if (minecraftVersion.equals("1.21.2") || minecraftVersion.equals("1.21.3")) {
                //return custom2.Armor(armorID, modID, armorType, armorPart, itemGroup);
            }else if (minecraftVersion.equals("1.20.5") || minecraftVersion.equals("1.20.6")) {
                return custom0.Armor(armorID, modID, armorType, armorPart, itemGroup);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Armor: " + e.getMessage(), e);
        }

        return null;
    }

    public static Item Armor(String armorID, String modID, RegistryEntry<ArmorMaterial> armorType, ArmorItem.Type armorPart) {
        return Armor(armorID, modID, armorType, armorPart, null);
    }
}