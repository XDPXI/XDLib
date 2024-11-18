package dev.xdpxi.xdlib.api.mod.customClass;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.lang.reflect.Method;

public class custom2 {
    private static Method registerMethod;
    private static Method getKeyMethod;

    static {
        try {
            registerMethod = Registry.class.getDeclaredMethod("register", Registry.class, Identifier.class, Object.class);
            registerMethod.setAccessible(true);
            
            getKeyMethod = Registries.class.getDeclaredMethod("getKey");
            getKeyMethod.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ItemGroup(String itemGroupID, String modID, Item itemIconID, List<Item> itemsToAdd) {
        itemGroupID = itemGroupID.toLowerCase();
        modID = modID.toLowerCase();
        RegistryKey<ItemGroup> ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(modID, itemGroupID));

        ItemGroup ITEM_GROUP = FabricItemGroup.builder()
                .displayName(Text.translatable("itemGroup." + modID + "." + itemGroupID))
                .icon(() -> new ItemStack(itemIconID != null ? itemIconID : Items.STONE))
                .build();

        Registry.register(Registries.ITEM_GROUP, ITEM_GROUP_KEY, ITEM_GROUP);

        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP_KEY).register(itemGroup -> {
            for (Item item : itemsToAdd) {
                itemGroup.add(item);
            }
        });
    }

    public static void AddToItemGroup(String itemGroupID, String modID, List<Item> itemsToAdd) {
        ItemGroup(itemGroupID, modID, null, itemsToAdd);
    }

    public static Item Item(String itemID, String modID, RegistryKey<ItemGroup> itemGroup) {
        if (itemID == null || modID == null) {
            throw new IllegalArgumentException("itemID or modID is null");
        }
        itemID = itemID.toLowerCase();
        modID = modID.toLowerCase();
        Identifier identifier = Identifier.of(modID, itemID);

        Item.Settings settings = new Item.Settings();
        Item item = new Item(settings);
        Registry.register(Registries.ITEM, identifier, item);

        if (itemGroup != null) {
            ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.add(item));
        }

        return item;
    }

    public static Item Item(String itemID, String modID) {
        return Item(itemID, modID, null);
    }

    public static BlockItem Block(String blockID, String modID, RegistryKey<ItemGroup> itemGroup) {
        blockID = blockID.toLowerCase();
        modID = modID.toLowerCase();
        Identifier blockIdentifier = Identifier.of(modID, blockID);

        Block block = new Block(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(1.5F, 6.0F));
        Block registeredBlock = Registry.register(Registries.BLOCK, blockIdentifier, block);
        BlockItem blockItem = new BlockItem(registeredBlock, new Item.Settings());

        Registry.register(Registries.ITEM, blockIdentifier, blockItem);

        if (itemGroup != null) {
            ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.add(blockItem));
        }

        return blockItem;
    }

    public static BlockItem Block(String blockID, String modID) {
        return Block(blockID, modID, null);
    }
}