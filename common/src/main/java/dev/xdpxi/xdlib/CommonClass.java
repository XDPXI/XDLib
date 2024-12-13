package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.api.Logger;

public class CommonClass {
    public static void init() {
        Logger.setup("dev.xdpxi.xdlib", Constants.MOD_ID);
        Logger.info("[XDLib] - Loading...");
    }
}