package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.api.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        CommonClass.init();

        Logger.info("[XDLib] - Checking for updates...");
        UpdateCheckerBukkit.checkForUpdate();

        Logger.info("[XDLib] - Loaded!");
    }

    @Override
    public void onDisable() {
        Logger.info("[XDLib] - Disabled!");
    }
}