package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.util.Log;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class Main {

    public Main(ModContainer modContainer, IEventBus modEventBus) {
        Log.info("[XDLib] - Initializing...");

        Common.init();

        Log.info("[XDLib] - Testing register API (v7)...");
        dev.xdpxi.xdlib.api.v7.Register.init();

        Log.info("[XDLib] - Initialized successfully!");
    }
}