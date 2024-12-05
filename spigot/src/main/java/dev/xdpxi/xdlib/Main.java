package dev.xdpxi.xdlib;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("[XDLib] - Enabling...");

        UpdateChecker checker = new UpdateChecker(this);
        checker.checkForUpdate();

        if (checker.isUpdateAvailable()) {
            getLogger().warning("[XDLib] - Please update to the latest version!");
        }

        getLogger().info("[XDLib] - Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("[XDLib] - Disabled!");
    }
}