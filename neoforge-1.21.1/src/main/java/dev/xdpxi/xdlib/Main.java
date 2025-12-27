package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.util.Log;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class Main {
    public Main(IEventBus modEventBus) {
        Log.info("[XDLib] - Initializing...");

        Common.init();

        Log.info("[XDLib] - Initialized successfully!");
    }
}