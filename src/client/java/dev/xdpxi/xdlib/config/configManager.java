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
    public final File configFile;
    public final Gson gson;
    private static final boolean clothConfig = loader.isModLoaded("cloth-config");
    public static ConfigData configData;

    public static void registerConfig() {
        new configManager();
    }

    public configManager() {
        File configDir = new File("config" + File.separator + "xdlib");
        File pluginDir = new File("config" + File.separator + "xdlib" + File.separator + "plugins");
        gson = new GsonBuilder().setPrettyPrinting().create();

        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        if (!pluginDir.exists()) {
            pluginDir.mkdirs();
        }
        configFile = new File(configDir, CONFIG_FILE_NAME);

        if (!configFile.exists()) {
            write(new ConfigData());
        } else {
            configData = read();
        }
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
        private boolean changelogEveryStartup = false;
        private boolean discordRPC = true;
        private boolean disablePlugins = false;
        private boolean disableChangelog = false;
        private boolean disableTitlePopups = false;
        private boolean unverifiedPlugins = false;
        private boolean devMode = false;

        public boolean isCustomBadges() {
            return customBadges;
        }

        public void setCustomBadges(boolean customBadges) {
            this.customBadges = customBadges;
        }

        public boolean isChangelogEveryStartup() {
            return changelogEveryStartup;
        }

        public void setChangelogEveryStartup(boolean changelogEveryStartup) {
            this.changelogEveryStartup = changelogEveryStartup;
        }

        public boolean isDiscordRPC() {
            return discordRPC;
        }

        public void setDiscordRPC(boolean discordRPC) {
            this.discordRPC = discordRPC;
        }

        public boolean isDisablePlugins() {
            return disablePlugins;
        }

        public void setDisablePlugins(boolean disablePlugins) {
            this.disablePlugins = disablePlugins;
        }

        public boolean isDisableChangelog() {
            return disableChangelog;
        }

        public void setDisableChangelog(boolean disableChangelog) {
            this.disableChangelog = disableChangelog;
        }

        public boolean isDisableTitlePopups() {
            return disableTitlePopups;
        }

        public void setDisableTitlePopups(boolean disableTitlePopups) {
            this.disableTitlePopups = disableTitlePopups;
        }

        public boolean isUnverifiedPlugins() {
            return unverifiedPlugins;
        }

        public void setUnverifiedPlugins(boolean unverifiedPlugins) {
            this.unverifiedPlugins = unverifiedPlugins;
        }

        public boolean isDevMode() {
            return devMode;
        }

        public void setDevMode(boolean devMode) {
            this.devMode = devMode;
        }
    }
}