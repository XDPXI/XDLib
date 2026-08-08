package dev.xdpxi.xdlib;

import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;

import static dev.xdpxi.xdlib.Common.log;

@Mod(Constants.MOD_ID)
public class Main {

    public Main(IEventBus modEventBus) {
        log.info("[XDLib] - Initializing...");

        Common.init();

        modEventBus.addListener(this::onRegister);

        log.info("[XDLib] - Initialized successfully!");
    }

    private void onRegister(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.BLOCK)) {
            return;
        }

        log.info("[XDLib] - Testing register API (v7)...");
        dev.xdpxi.xdlib.api.v7.Register.init();
    }
}
