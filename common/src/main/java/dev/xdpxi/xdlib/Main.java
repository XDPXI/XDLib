package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.api.Logger;

public final class Main {
    public static final String MOD_ID = "xdlib";

    public static void init() {
        Logger.setup(MOD_ID);
        Logger.info("[XDLib] - Started!");
    }
}