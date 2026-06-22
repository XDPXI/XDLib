package dev.xdpxi.xdlib;

import org.bukkit.plugin.java.JavaPlugin;

import static dev.xdpxi.xdlib.Common.log;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        log.info("[XDLib] - Enabling...");

        Common.init();

        log.info("[XDLib] - Enabled!");
    }

    @Override
    public void onDisable() {
        log.info("[XDLib] - Disabling...");

        log.info("[XDLib] - Disabled!");
    }
}
