package dev.xdpxi.xdlib.plugin;

import net.md_5.bungee.api.plugin.Plugin;

public final class bungee extends Plugin {

    @Override
    public void onEnable() {
        getLogger().info("[XDLib] - Enabling...");

        updateCheckerBungee checker = new updateCheckerBungee(this);
        checker.checkForUpdate();

        getLogger().info("[XDLib] - Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("[XDLib] - Disabled!");
    }
}