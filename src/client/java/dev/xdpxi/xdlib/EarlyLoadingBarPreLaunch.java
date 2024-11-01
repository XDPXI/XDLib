package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.api.files;
import dev.xdpxi.xdlib.gui.PreLaunchWindow;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

import java.nio.file.Path;

public class EarlyLoadingBarPreLaunch implements PreLaunchEntrypoint {
    private final Path configDir = FabricLoader.getInstance().getConfigDir().resolve("xdlib");
    private final Path pluginsDir = configDir.resolve("plugins");
    private final Path pluginsTemp = pluginsDir.resolve("plugins.tmp");

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    public boolean isModLoaded(String modID) {
        return FabricLoader.getInstance().isModLoaded(modID);
    }

    public boolean isNoEarlyLoaders() {
        return !(isModLoaded("early-loading-screen") ||
                isModLoaded("early_loading_bar") ||
                isModLoaded("earlyloadingscreen") ||
                isModLoaded("mindful-loading-info") ||
                isModLoaded("neoforge") ||
                isModLoaded("connector") ||
                isModLoaded("mod-loading-screen"));
    }

    public boolean isPluginDownload() {
        return files.exists(String.valueOf(pluginsTemp));
    }

    @Override
    public void onPreLaunch() {
        /*
        if (isWindows() && FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && isPluginDownload()) {
            PluginDownloader.display(files.readFile(String.valueOf(pluginsTemp)));
        }
        */

        if (isWindows() && FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && isNoEarlyLoaders()) {
            PreLaunchWindow.display();
        }
    }
}