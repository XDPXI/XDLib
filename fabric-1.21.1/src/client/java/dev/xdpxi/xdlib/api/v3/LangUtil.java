package dev.xdpxi.xdlib.api.v3;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.LanguageManager;

import java.util.Objects;

/**
 * Utility class for managing Minecraft's language settings.
 * Provides methods to get and set the current language.
 */
public class LangUtil {

    /**
     * Retrieves the current language code set in Minecraft.
     *
     * @return A string representing the current language code.
     */
    public static String getLang() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.getLanguageManager().getLanguage();
    }

    /**
     * Sets the language in Minecraft to the specified language code.
     * If the new language is different from the current one, it reloads the game resources.
     *
     * @param lang The language code to set. For example, "en_us" for English (US).
     */
    public static void setLang(String lang) {
        MinecraftClient client = MinecraftClient.getInstance();
        LanguageManager languageManager = client.getLanguageManager();
        if (!Objects.equals(getLang(), lang)) {
            languageManager.setLanguage(lang);
            client.reloadResources();
        }
    }
}
