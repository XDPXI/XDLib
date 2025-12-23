package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.registry.ModBlocks;
import dev.xdpxi.xdlib.registry.ModItems;
import dev.xdpxi.xdlib.util.Log;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod("realevents")
public class Main {
    public Main(IEventBus modEventBus) {
        Log.info("[RealEvents/Main] - Initializing...");

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);

        Log.info("[RealEvents/Main] - Initialized successfully!");
    }
}