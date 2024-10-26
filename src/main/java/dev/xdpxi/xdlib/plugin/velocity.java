package dev.xdpxi.xdlib.plugin;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

@Plugin(id = "xdlib", name = "XDLib", version = "2.4.0", description = "This is a library for many uses and is included as an player counter for XDPXI's mods and modpacks!", url = "https://xdpxi.vercel.app/mc/xdlib", authors = {"XDPXI"})
public class velocity {
    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("[XDLib] - Enabled!");
    }
}