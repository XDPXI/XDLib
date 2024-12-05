package dev.xdpxi.xdlib.fabric;

import dev.xdpxi.xdlib.Main;
import dev.xdpxi.xdlib.api.Logger;
import net.fabricmc.api.ModInitializer;

public final class MainFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Main.init();

        Logger.info("[XDLib] - Checking for updates...");
        UpdateCheckerFabric.checkForUpdate();

        Logger.info("[XDLib] - Loaded!");
    }
}