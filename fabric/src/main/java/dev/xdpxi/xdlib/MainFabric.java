package dev.xdpxi.xdlib;

import net.fabricmc.api.ModInitializer;

public class MainFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CommonClass.init();
    }
}