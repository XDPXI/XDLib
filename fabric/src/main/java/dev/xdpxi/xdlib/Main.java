package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.api.Logger;
import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer {
    @Override
    public void onInitialize() {
        CommonClass.init();

        dev.xdpxi.xdlib.api.Logger.info("[XDLib/UpdateChecker] - Checking for updates...");
        UpdateCheckerFabric.checkForUpdate();

        Logger.info("[XDLib/Main] - Loaded!");
    }
}