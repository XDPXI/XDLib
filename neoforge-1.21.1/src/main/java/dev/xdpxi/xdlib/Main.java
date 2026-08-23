package dev.xdpxi.xdlib;

import static dev.xdpxi.xdlib.Common.log;

import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class Main {

  public Main() {
    log.info("[XDLib] - Initializing...");

    Common.init();

    log.info("[XDLib] - Testing register API (v7)...");
    dev.xdpxi.xdlib.api.v7.Register.init();

    log.info("[XDLib] - Initialized successfully!");
  }
}
