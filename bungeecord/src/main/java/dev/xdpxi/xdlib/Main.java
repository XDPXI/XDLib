package dev.xdpxi.xdlib;

import static dev.xdpxi.xdlib.Common.log;

import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {
  @Override
  public void onEnable() {
    log.info("[XDLib] - Enabling...");

    Common.init();

    log.info("[XDLib] - Enabled!");
  }

  @Override
  public void onDisable() {
    log.info("[XDLib] - Disabling...");

    log.info("[XDLib] - Disabled!");
  }
}
