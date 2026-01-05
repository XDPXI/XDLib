package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.util.Log;
import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer {

    @Override
    public void onInitialize() {
        Log.info("[XDLib] - Initializing...");

        Common.init();

        Log.info("[XDLib] - Testing register API (v5)...");
        dev.xdpxi.xdlib.api.v5.Register.init();

        Log.info("[XDLib] - Testing register API (v6)...");
        dev.xdpxi.xdlib.api.v6.Register.init();

        Log.info("[XDLib] - Testing register API (v7)...");
        dev.xdpxi.xdlib.api.v7.Register.init();

        Log.info("[XDLib] - Initialized successfully!");
    }
}
