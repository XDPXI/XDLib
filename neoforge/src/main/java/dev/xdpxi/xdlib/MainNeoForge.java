package dev.xdpxi.xdlib;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class MainNeoForge {
    public MainNeoForge(IEventBus eventBus) {
        CommonClass.init();
    }
}