package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.api.v5.Register;
import dev.xdpxi.xdlib.util.Log;
import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer {
    public static final String MOD_ID = "xdlib";

    @Override
    public void onInitialize() {
        Log.info("[XDLib/Main] - Loading...");

        UpdateChecker.checkForUpdate();
        Register.init();

        Log.info("[XDLib/Main] - Loaded!");
    }
}