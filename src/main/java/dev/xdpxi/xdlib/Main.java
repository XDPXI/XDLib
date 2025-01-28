package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.api.Register;
import dev.xdpxi.xdlib.util.Log;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {
    public static final String MOD_ID = "xdlib";

    @Override
    public void onInitialize() {
        Log.info("[XDLib] - Loading...");

        UpdateChecker.checkForUpdate();
        Register.init();

        Log.info("[XDLib] - Loaded!");
    }
}