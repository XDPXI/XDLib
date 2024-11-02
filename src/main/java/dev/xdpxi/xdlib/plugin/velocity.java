package dev.xdpxi.xdlib.plugin;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(id = "xdlib", name = "XDLib", version = "2.4.2", description = "This is a library for many uses and is included as an player counter for XDPXI's mods and modpacks!", url = "https://xdpxi.vercel.app/mc/xdlib", authors = {"XDPXI"})
public class velocity {
    @Inject
    private Logger logger;

    private final ProxyServer proxyServer;
    private final Path dataDirectory;
    private final String currentVersion;

    public velocity(ProxyServer proxyServer, @DataDirectory Path dataDirectory) {
        this.proxyServer = proxyServer;
        this.dataDirectory = dataDirectory;
        this.currentVersion = this.getClass().getAnnotation(Plugin.class).version();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("[XDLib] - Enabled!");

        updateCheckerVelocity updateChecker = new updateCheckerVelocity(proxyServer, (Plugin) dataDirectory, currentVersion);
        updateChecker.checkForUpdate();
    }
}