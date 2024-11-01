package dev.xdpxi.xdlib.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.xdpxi.xdlib.api.files;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

public class CustomConfigSerializer extends GsonConfigSerializer<ZLibsConfig> {
    private final Gson gson;

    public CustomConfigSerializer(Config config, Class<ZLibsConfig> clazz) {
        super(config, clazz);
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            Path configDir = FabricLoader.getInstance().getConfigDir().resolve("xdlib");
            Path tempDir = configDir.resolve("temp");
            Path configFile = configDir.resolve("config.json");
            Path oldConfigFile = tempDir.resolve("config.json");

            try {
                if (Files.exists(oldConfigFile)) {
                    Files.move(oldConfigFile, configFile, StandardCopyOption.REPLACE_EXISTING);
                    files.deleteFolder(tempDir);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to move config back on shutdown: " + e.getMessage(), e);
            }
        });
    }

    public static void loadAndReplaceConfig() {
        try {
            Path configDir = FabricLoader.getInstance().getConfigDir().resolve("xdlib");
            Path configFile = configDir.resolve("config.json");
            Path tempDir = configDir.resolve("temp");
            Path oldConfigFile = tempDir.resolve("config.json");

            if (Files.exists(oldConfigFile)) {
                if (Files.exists(configFile)) {
                    Files.delete(configFile);
                }
                Files.move(oldConfigFile, configFile, StandardCopyOption.REPLACE_EXISTING);
            } else {
                System.out.println("No old config file found.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config: " + e.getMessage(), e);
        }
    }

    @Override
    public void serialize(ZLibsConfig config) {
        try {
            Path configDir = FabricLoader.getInstance().getConfigDir().resolve("xdlib");
            Files.createDirectories(configDir);

            Path tempDir = configDir.resolve("temp");
            Files.createDirectories(tempDir);

            Path configFile = configDir.resolve("config.json");
            Path oldConfigFile = tempDir.resolve("config.json");

            if (Files.exists(configFile)) {
                Files.move(configFile, oldConfigFile, StandardCopyOption.REPLACE_EXISTING);
            }

            String json = gson.toJson(config);
            Files.write(configFile, json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize config: " + e.getMessage(), e);
        }
    }
}