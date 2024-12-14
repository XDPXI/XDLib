package dev.xdpxi.xdlib.platform;

import dev.xdpxi.xdlib.platform.services.IPlatformHelper;

public class BukkitPlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "Bukkit";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return false;
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return false;
    }
}