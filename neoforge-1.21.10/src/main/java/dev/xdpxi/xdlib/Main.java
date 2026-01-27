package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.util.Log;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class Main {
    public Main() {
        Log.info("[XDLib] - Initializing...");

        Common.init();

        Log.info("[XDLib] - Testing register API (v7)...");
        dev.xdpxi.xdlib.api.v7.Register.init();

        Log.info("[XDLib] - Initialized successfully!");
    }
}