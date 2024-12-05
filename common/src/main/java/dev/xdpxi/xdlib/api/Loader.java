package dev.xdpxi.xdlib.api;

public class Loader {
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