package dev.xdpxi.xdlib.api.v5;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

/**
 * Utility class for interacting with the Fabric mod loader. Provides methods to check if a mod is
 * loaded and retrieve its version.
 */
public final class Loader {

  private Loader() {
    throw new UnsupportedOperationException(
        "Loader is a utility class and cannot be instantiated.");
  }

  /**
   * Checks whether a mod with the given ID is loaded.
   *
   * @param modID The ID of the mod to check.
   * @return {@code true} if the mod is loaded, {@code false} otherwise.
   */
  public static boolean isModLoaded(String modID) {
    return FabricLoader.getInstance().isModLoaded(modID);
  }

  /**
   * Retrieves the version of a loaded mod by its ID.
   *
   * @param modID The ID of the mod whose version is to be retrieved.
   * @return A string representing the mod's version, or {@code null} if the mod is not found.
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
