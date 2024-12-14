package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.api.Logger;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class Main {
    public Main() {
        CommonClass.init();

        UpdateCheckerForge.checkForUpdate();

        Logger.info("[XDLib/Main] - Loaded!");
    }
}