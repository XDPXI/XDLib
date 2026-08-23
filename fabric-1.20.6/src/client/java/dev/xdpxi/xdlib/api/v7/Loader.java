package dev.xdpxi.xdlib.api.v7;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

/**
 * Utility class for interacting with the Fabric mod loader. Provides methods to check mod loading
 * status and retrieve mod versions.
 */
public class Loader {

  /**
   * Checks if a mod with the given ID is loaded.
   *
   * @param modID The ID of the mod to check.
   * @return true if the mod is loaded, false otherwise.
   */
  public static boolean isModLoaded(String modID) {
    return FabricLoader.getInstance().isModLoaded(modID);
  }

  /**
   * Retrieves the version of a mod with the given ID.
   *
   * @param modID The ID of the mod to get the version for.
   * @return A string representing the mod's version, or null if the mod is not found.
   */
  public static String getModVersion(String modID) {
    FabricLoader loader = FabricLoader.getInstance();
    ModContainer modContainer = loader.getModContainer(modID).orElse(null);
    if (modContainer != null) {
      ModMetadata metadata = modContainer.getMetadata();
      return metadata.getVersion().getFriendlyString();
    }
    return null;
  }
}
