package dev.xdpxi.xdlib;

import static dev.xdpxi.xdlib.Common.log;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;

@Plugin(
    id = "xdlib",
    name = "XD's Library",
    version = Constants.VERSION + "-velocity",
    description =
        "This is a library for many uses and is included as an player counter for XDPXI mods and"
            + " modpacks!",
    url = "https://modrinth.com/plugin/xdlib",
    authors = {"XDPXI"})
public class Main {
  @Subscribe
  public void onProxyInitialization(ProxyInitializeEvent event) {
    log.info("[XDLib] - Enabling...");

    Common.init();

    log.info("[XDLib] - Enabled!");
  }
}
