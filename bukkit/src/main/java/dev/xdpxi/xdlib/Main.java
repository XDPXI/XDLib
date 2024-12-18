package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.api.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        CommonClass.init();

        Thread updateThread = new Thread(new UpdateCheckerBukkit(), "Update thread");
        updateThread.setDaemon(true);
        updateThread.start();

        Logger.info("[XDLib/Main] - Loaded!");
    }

    @Override
    public void onDisable() {
        Logger.info("[XDLib/Main] - Disabled!");
    }
}