package dev.xdpxi.xdlib.api.v7;

import net.neoforged.fml.ModList;

/**
 * Utility class for interacting with the NeoForge mod loader. Provides methods to check if a mod is
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
    return ModList.get().isLoaded(modID);
  }

  /**
   * Retrieves the version of a loaded mod by its ID.
   *
   * @param modID The ID of the mod whose version is to be retrieved.
   * @return A string representing the mod's version, or {@code null} if the mod is not found.
   */
  public static String getModVersion(String modID) {
    return ModList.get()
        .getModContainerById(modID)
        .map(container -> container.getModInfo().getVersion().toString())
        .orElse(null);
  }
}
