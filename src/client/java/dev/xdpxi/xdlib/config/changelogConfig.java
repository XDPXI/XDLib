package dev.xdpxi.xdlib.config;

import org.tomlj.Toml;
import org.tomlj.TomlTable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class changelogConfig {
    private static final String CONFIG_FILE_NAME = "version.toml";
    private final File configFile;

    public changelogConfig() {
        File configDir = new File("config\\xdlib");

        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        configFile = new File(configDir, CONFIG_FILE_NAME);

        if (!configFile.exists()) {
            write(new ConfigData());
        }
    }

    public void write(ConfigData configData) {
        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
            }
            String tomlString = String.format("version = %d", configData.getVersion());
            try (FileWriter writer = new FileWriter(configFile)) {
                writer.write(tomlString);
            }
        } catch (IOException ignored) {
        }
    }

    public ConfigData read() {
        try (FileReader reader = new FileReader(configFile)) {
            TomlTable table = Toml.parse(reader);
            int version = table.getLong("version").intValue();
            ConfigData configData = new ConfigData();
            configData.setVersion(version);
            return configData;
        } catch (IOException ignored) {
            return null;
        }
    }

    public static class ConfigData {
        private int version = 0;

        public int getVersion() {
            return version;
        }

        public void setVersion(int var) {
            version = var;
        }
    }
}