package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.util.Log;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod("realevents")
public class Main {
    public Main(IEventBus modEventBus) {
        Log.info("[RealEvents/Main] - Initializing...");

        Common.init();

        Log.info("[RealEvents/Main] - Initialized successfully!");
    }
}