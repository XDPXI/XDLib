package dev.xdpxi.xdlib;

import org.bukkit.plugin.java.JavaPlugin;

public final class MainBukkit extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("[XDLib] - Enabling...");

        UpdateCheckerBukkit checker = new UpdateCheckerBukkit(this);
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