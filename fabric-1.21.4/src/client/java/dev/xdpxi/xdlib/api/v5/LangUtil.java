package dev.xdpxi.xdlib.api.v5;

import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.LanguageManager;

/**
 * Utility class for managing Minecraft's language settings. Provides methods to get and set the
 * current language.
 */
public final class LangUtil {

  private LangUtil() {
    throw new UnsupportedOperationException(
        "LangUtil is a utility class and cannot be instantiated.");
  }

  /**
   * Retrieves the current language code set in Minecraft.
   *
   * @return A string representing the current language code, or {@code "en_us"} if unavailable.
   */
  public static String getLang() {
    MinecraftClient client = MinecraftClient.getInstance();
    return (client != null) ? client.getLanguageManager().getLanguage() : "en_us";
  }

  /**
   * Sets the Minecraft language to the specified language code. If the new language is different
   * from the current one, game resources are reloaded.
   *
   * @param lang The language code to set (e.g., {@code "en_us"} for English).
   */
  public static void setLang(String lang) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client == null) return;

    LanguageManager languageManager = client.getLanguageManager();
    if (!Objects.equals(getLang(), lang)) {
      languageManager.setLanguage(lang);
      client.reloadResources();
    }
  }
}
