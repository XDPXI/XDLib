package dev.xdpxi.xdlib.api.v3;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

/**
 * The {@code Loader} class provides utility methods to interact with the Fabric mod loader.
 * It allows checking if a mod is loaded and retrieving the version of a mod.
 */
public class Loader {

    /**
     * Checks if a mod is loaded by its mod ID.
     *
     * @param modID The ID of the mod to check.
     * @return {@code true} if the mod is loaded, {@code false} otherwise.
     */
    public static boolean isModLoaded(String modID) {
        return FabricLoader.getInstance().isModLoaded(modID);
    }

    /**
     * Retrieves the version of a mod by its mod ID.
     * If the mod is loaded, it returns the friendly version string of the mod.
     * If the mod is not loaded, it returns {@code null}.
     *
     * @param modID The ID of the mod whose version is to be retrieved.
     * @return The friendly version string of the mod, or {@code null} if the mod is not loaded.
     */
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