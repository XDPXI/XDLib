package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.util.Log;
import net.fabricmc.api.ClientModInitializer;

public class Client implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Log.info("[XDLib/Client] - Loading...");

        Log.info("[XDLib/Client] - Loaded!");
    }
}