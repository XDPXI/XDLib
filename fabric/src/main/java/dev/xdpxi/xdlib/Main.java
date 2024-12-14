package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.api.Logger;
import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer {
    @Override
    public void onInitialize() {
        CommonClass.init();

        UpdateCheckerFabric.checkForUpdate();

        Logger.info("[XDLib/Main] - Loaded!");
    }
}