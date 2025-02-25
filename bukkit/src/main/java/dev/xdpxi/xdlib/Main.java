package dev.xdpxi.xdlib;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private final Plugin plugin;

    public Main(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEnable() {
        CommonClass.init();

        Thread updateThread = new Thread(new UpdateCheckerBukkit(plugin), "Update thread");
        updateThread.setDaemon(true);
        updateThread.start();

        Logger.info("[XDLib/Main] - Loaded!");
    }

    @Override
    public void onDisable() {
        Logger.info("[XDLib/Main] - Disabling...");
    }
}
