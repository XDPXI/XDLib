package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.api.Logger;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class Main {
    public Main(IEventBus eventBus) {
        CommonClass.init();

        dev.xdpxi.xdlib.api.Logger.info("[XDLib] - Checking for updates...");
        UpdateCheckerNeoForge.checkForUpdate();

        Logger.info("[XDLib] - Loaded!");
    }
}