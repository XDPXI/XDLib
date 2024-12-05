package dev.xdpxi.xdlib.api;

/**
 * A utility class to detect the mod loader being used in the environment.
 * It checks if the mod is being loaded by Fabric, NeoForge, or an unknown loader.
 */
public class Loader {

    /**
     * Detects the mod loader being used.
     * <p>
     * It first checks for the presence of the Fabric mod loader class,
     * then for the NeoForge mod loader class. If neither are found,
     * it returns "Unknown".
     * </p>
     *
     * @return a String representing the mod loader ("Fabric", "NeoForge", or "Unknown")
     */
    public static String getModLoader() {
        try {
            Class.forName("net.fabricmc.loader.api.FabricLoader");
            return "Fabric";
        } catch (ClassNotFoundException ignored) { }

        try {
            Class.forName("net.neoforged.fml.loading.FMLLoader");
            return "NeoForge";
        } catch (ClassNotFoundException ignored) { }

        return "Unknown";
    }
}