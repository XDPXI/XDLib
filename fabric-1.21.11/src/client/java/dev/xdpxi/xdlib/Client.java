package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.api.v7.WindowProvider;
import net.fabricmc.api.ClientModInitializer;

public class Client implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        WindowProvider.init();
    }
}
