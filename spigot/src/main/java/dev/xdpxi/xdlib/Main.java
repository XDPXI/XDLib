package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.util.Log;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    public static Plugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        CommonClass.init();

        Thread updateThread = new Thread(new UpdateCheckerSpigot(), "Update thread");
        updateThread.setDaemon(true);
        updateThread.start();

        Log.info("[XDLib/Main] - Loaded!");
    }

    @Override
    public void onDisable() {
        Log.info("[XDLib/Main] - Disabling...");
    }
}
