package dev.xdpxi.xdlib.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.xdpxi.xdlib.api.mod.loader;
import net.minecraft.client.gui.screen.Screen;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class configManager {
    public static final String CONFIG_FILE_NAME = "config.json";
    private static final boolean clothConfig = loader.isModLoaded("cloth-config");
    public static ConfigData configData;
    public final File configFile;
    public final Gson gson;

    public configManager() {
        File configDir = new File("config" + File.separator + "xdlib");
        gson = new GsonBuilder().setPrettyPrinting().create();

        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        configFile = new File(configDir, CONFIG_FILE_NAME);

        if (!configFile.exists()) {
            write(new ConfigData());
        } else {
            configData = read();
        }
    }

    public static void registerConfig() {
        new configManager();
    }

    public void write(ConfigData configData) {
        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
            }
            try (FileWriter writer = new FileWriter(configFile)) {
                gson.toJson(configData, writer);
            }
        } catch (IOException ignored) {
        }
    }

    public ConfigData read() {
        try (FileReader reader = new FileReader(configFile)) {
            return gson.fromJson(reader, ConfigData.class);
        } catch (IOException ignored) {
            return null;
        }
    }

    public Screen getModConfigScreen(ConfigData configData, Screen parent) {
        if (clothConfig) {
            return getModConfigScreen(configManager.configData, parent);
        }
        return parent;
    }

    public static class ConfigData {
        private boolean customBadges = true;
        private boolean titlePopups = false;

        public boolean isCustomBadges() {
            return customBadges;
        }

        public void setCustomBadges(boolean customBadges) {
            this.customBadges = customBadges;
        }

        public boolean isTitlePopups() {
            return titlePopups;
        }

        public void setTitlePopups(boolean titlePopups) {
            this.titlePopups = titlePopups;
        }
    }
}