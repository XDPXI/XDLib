package dev.xdpxi.xdlib.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class configManager {
    public static final String CONFIG_FILE_NAME = "config.json";
    public final File configFile;
    public final Gson gson;
    public ConfigData configData;

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

    public Screen getModConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.of("XDLib Configuration"));

        builder.getOrCreateCategory(Text.of("General"))
                .addEntry(builder.entryBuilder()
                        .startIntField(Text.of("Version"), configData.getVersion())
                        .setDefaultValue(0)
                        .setSaveConsumer(newVersion -> {
                            configData.setVersion(newVersion);
                            write(configData);
                        })
                        .build()
                );

        return builder.build();
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