package dev.xdpxi.xdlib.neoforge;

import dev.xdpxi.xdlib.Main;
import dev.xdpxi.xdlib.api.Logger;
import net.neoforged.fml.common.Mod;

@Mod(Main.MOD_ID)
public final class MainNeoForge {
    public MainNeoForge() {
        Main.init();

        Logger.info("[XDLib] - Checking for updates...");
        UpdateCheckerNeoForge.checkForUpdate();

        Logger.info("[XDLib] - Loaded!");
    }
}