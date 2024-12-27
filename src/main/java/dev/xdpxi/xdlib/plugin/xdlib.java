package dev.xdpxi.xdlib.plugin;

import dev.xdpxi.xdlib.api.plugin.pluginManager;
import org.bukkit.command.*;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;

public final class xdlib extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("[XDLib] - Enabling...");

        setConfig();

        updateChecker checker = new updateChecker(this);
        checker.checkForUpdate();

        getLogger().info("[XDLib] - Enabled!");
    }

    private void setConfig() {
        saveDefaultConfig();

        boolean enabled = getConfig().getBoolean("enabled");
        if (!enabled) {
            getLogger().info("[XDLib] - Plugin Disabled in Config!");
            pluginManager.disablePlugin("xdlib");
        }

        getLogger().info("[XDLib] - Config Loaded!");
    }

    @Override
    public void onDisable() {
        getLogger().info("[XDLib] - Disabled!");
    }
}