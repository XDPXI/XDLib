package dev.xdpxi.xdlib.api.mod.plugins;

public interface PluginInitializer {
    void onLoad();
    void onUnload();
}