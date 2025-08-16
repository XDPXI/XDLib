package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.util.Log;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;

@Plugin(id = "xdlib", name = "XD's Library", version = Main.version, description = "This is a library for many uses and is included as an player counter for XDPXI mods and modpacks!", url = "https://modrinth.com/plugin/xdlib", authors = {"XDPXI"})
public final class Main {
    public static final String version = "4.0.0-beta.8";

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        CommonClass.init();

        Thread updateThread = new Thread(new UpdateCheckerVelocity(), "Update thread");
        updateThread.setDaemon(true);
        updateThread.start();

        Log.info("[XDLib/Main] - Loaded!");
    }
}
