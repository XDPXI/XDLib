package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.api.Logger;
import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer {
    @Override
    public void onInitialize() {
        CommonClass.init();

        Thread updateThread = new Thread(new UpdateCheckerFabric(), "Update thread");
        updateThread.setDaemon(true);
        updateThread.start();

        Logger.info("[XDLib/Main] - Loaded!");
    }
}