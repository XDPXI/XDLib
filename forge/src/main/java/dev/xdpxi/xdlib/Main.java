package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.api.Logger;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class Main {
    public Main() {
        CommonClass.init();

        dev.xdpxi.xdlib.api.Logger.info("[XDLib] - Checking for updates...");
        UpdateCheckerForge.checkForUpdate();

        Logger.info("[XDLib] - Loaded!");
    }
}