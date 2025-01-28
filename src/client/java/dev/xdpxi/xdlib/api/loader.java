package dev.xdpxi.xdlib.api;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

public class loader {
    public static boolean isModLoaded(String modID) {
        return FabricLoader.getInstance().isModLoaded(modID);
    }

    public static String versionOfMod(String modID) {
        FabricLoader loader = FabricLoader.getInstance();
        ModContainer modContainer = loader.getModContainer(modID).orElse(null);
        if (modContainer != null) {
            ModMetadata metadata = modContainer.getMetadata();
            return metadata.getVersion().getFriendlyString();
        }
        return null;
    }
}