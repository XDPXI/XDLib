package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.util.Log;
import net.md_5.bungee.api.plugin.Plugin;

public final class Main extends Plugin {
    public static Plugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        CommonClass.init();

        Thread updateThread = new Thread(new UpdateCheckerBungee(), "Update thread");
        updateThread.setDaemon(true);
        updateThread.start();

        Log.info("[XDLib/Main] - Loaded!");
    }

    @Override
    public void onDisable() {
        Log.info("[XDLib/Main] - Disabling...");
    }
}
