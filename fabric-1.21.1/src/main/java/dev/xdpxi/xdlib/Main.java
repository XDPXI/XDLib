package dev.xdpxi.xdlib;

import static dev.xdpxi.xdlib.Common.log;

import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer {

  @Override
  public void onInitialize() {
    log.info("[XDLib] - Initializing...");

    Common.init();

    log.info("[XDLib] - Testing register API (v3)...");
    dev.xdpxi.xdlib.api.v3.Register.init();

    log.info("[XDLib] - Testing register API (v6)...");
    dev.xdpxi.xdlib.api.v6.Register.init();

    log.info("[XDLib] - Testing register API (v7)...");
    dev.xdpxi.xdlib.api.v7.Register.init();

    log.info("[XDLib] - Initialized successfully!");
  }
}
