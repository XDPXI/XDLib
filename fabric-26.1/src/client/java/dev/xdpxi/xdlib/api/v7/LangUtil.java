package dev.xdpxi.xdlib.api.v7;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.LanguageManager;

import java.util.Objects;

/**
 * Utility class for managing Minecraft's language settings.
 * Provides methods to get and set the current language.
 */
public final class LangUtil {

    private LangUtil() {
        throw new UnsupportedOperationException(
                "LangUtil is a utility class and cannot be instantiated."
        );
    }

    /**
     * Retrieves the current language code set in Minecraft.
     *
     * @return A string representing the current language code, or {@code "en_us"} if unavailable.
     */
    public static String getLang() {
        Minecraft client = Minecraft.getInstance();
        return (client != null)
                ? client.getLanguageManager().getSelected()
                : "en_us";
    }

    /**
     * Sets the Minecraft language to the specified language code.
     * If the new language is different from the current one, game resources are reloaded.
     *
     * @param lang The language code to set (e.g., {@code "en_us"} for English).
     */
    public static void setLang(String lang) {
        Minecraft client = Minecraft.getInstance();
        if (client == null) return;

        LanguageManager languageManager = client.getLanguageManager();
        if (!Objects.equals(getLang(), lang)) {
            languageManager.setSelected(lang);
            client.reloadResourcePacks();
        }
    }
}
