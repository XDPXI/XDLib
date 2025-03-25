package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.api.v5.Configuration;
import dev.xdpxi.xdlib.api.v5.Register;
import dev.xdpxi.xdlib.util.Config;
import dev.xdpxi.xdlib.util.Log;
import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer {
    public static final String MOD_ID = "xdlib";

    @Override
    public void onInitialize() {
        Log.info("[XDLib/Main] - Loading...");

        Log.info("[XDLib/Main] - Loading configuration...");
        Configuration.ConfigLoader.loadConfig(Config.class);

        Log.info("[XDLib/Main] - Checking for Updates...");
        UpdateChecker.checkForUpdate();

        Log.info("[XDLib/Main] - Initializing Register API...");
        Register.init();

        Log.info("[XDLib/Main] - Loaded!");
    }
}