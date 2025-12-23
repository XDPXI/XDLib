package dev.xdpxi.xdlib.registry;

import dev.xdpxi.xdlib.util.EventManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("realevents");

    private static final DeferredBlock<Block> PRESENT;
    private static final DeferredBlock<Block> CHRISTMAS_TREE;

    static {
        EventManager.EventType currentEvent = EventManager.getCurrentEventType();

        // Register Christmas blocks only during Christmas
        if (currentEvent == EventManager.EventType.CHRISTMAS) {
            PRESENT = BLOCKS.register("present",
                    () -> new Block(Block.Properties.of()
                            .mapColor(MapColor.COLOR_RED)
                            .sound(SoundType.WOOL)
                            .strength(0.8f)
                            .noCollission()
                            .noOcclusion()));

            CHRISTMAS_TREE = BLOCKS.register("christmas_tree",
                    () -> new Block(Block.Properties.of()
                            .mapColor(MapColor.COLOR_GREEN)
                            .sound(SoundType.AZALEA_LEAVES)
                            .strength(0.2f)
                            .noCollission()
                            .noOcclusion()));
        } else {
            PRESENT = null;
            CHRISTMAS_TREE = null;
        }
    }

    // Getter methods for safe access
    public static Block getPresent() {
        return PRESENT != null ? PRESENT.get() : null;
    }

    public static Block getChristmasTree() {
        return CHRISTMAS_TREE != null ? CHRISTMAS_TREE.get() : null;
    }
}