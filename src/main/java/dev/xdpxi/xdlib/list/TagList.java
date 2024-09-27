package dev.xdpxi.xdlib.list;

import dev.xdpxi.xdlib.XDsLibrary;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class TagList {
    public static class Blocks {
        public static final TagKey<Block> EXAMPLE_TAG = TagKey.of(RegistryKeys.BLOCK, XDsLibrary.id("example"));

        public static final TagKey<Block> INCORRECT_FOR_EXAMPLE_TOOL =
                TagKey.of(RegistryKeys.BLOCK, XDsLibrary.id("incorrect_for_example_tool"));
    }
}