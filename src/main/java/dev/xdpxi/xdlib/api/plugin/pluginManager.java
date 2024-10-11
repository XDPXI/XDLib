package dev.xdpxi.xdlib.api.plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class pluginManager {
    public static void disablePlugin(String disablePlugin) {
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        Plugin plugin = pluginManager.getPlugin(disablePlugin);
        if (plugin != null && plugin.isEnabled()) {
            pluginManager.disablePlugin(plugin);
        }
    }
}