package dev.xdpxi.xdlib.fabric;

import dev.xdpxi.xdlib.Main;
import net.fabricmc.api.ModInitializer;

public final class MainFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Main.init();
    }
}