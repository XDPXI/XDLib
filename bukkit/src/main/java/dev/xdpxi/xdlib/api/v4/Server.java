package dev.xdpxi.xdlib.api.v4;

import dev.xdpxi.xdlib.Main;

public class Server {
    public static String getModVersion() {
        return Main.plugin.getDescription().getVersion();
    }
}