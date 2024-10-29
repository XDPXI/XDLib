package dev.xdpxi.xdlib.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.xdpxi.xdlib.DiscordRPCHandler;
import dev.xdpxi.xdlib.api.files;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class configHelper {
    public static final Logger LOGGER = LoggerFactory.getLogger("xdlib");
    private static boolean discordRPC = true;

    private static void migration() throws IOException {
        Path configDir = FabricLoader.getInstance().getConfigDir().resolve("xdlib");
        Path configFile = configDir.resolve("config.json");
        Path migrateOldConfig = FabricLoader.getInstance().getConfigDir().resolve("xdlib.json");
        Path migrateOldChangelog = FabricLoader.getInstance().getConfigDir().resolve("xdlib-changelog.json");
        if (Files.exists(configFile)) {
            files.deleteFile(configFile);
        }
        if (Files.exists(migrateOldConfig)) {
            try {
                Files.move(migrateOldConfig, configFile, StandardCopyOption.REPLACE_EXISTING);
                if (Files.exists(migrateOldConfig)) {
                    files.deleteFile(migrateOldConfig);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (Files.exists(migrateOldChangelog)) {
            try {
                files.deleteFile(migrateOldChangelog);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void registerConfig() throws IOException {
        Path migrateOldConfig = FabricLoader.getInstance().getConfigDir().resolve("xdlib.json");
        Path configDir = FabricLoader.getInstance().getConfigDir().resolve("xdlib");
        Path tempDir = configDir.resolve("temp");
        Path configFile = configDir.resolve("config.json");
        Path oldConfigFile = tempDir.resolve("config.json");
        files.createFolder(configDir);

        if (Files.exists(migrateOldConfig)) {
            migration();
        }

        if (Files.exists(configFile)) {
            Files.createDirectories(tempDir);
            Files.move(configFile, oldConfigFile, StandardCopyOption.REPLACE_EXISTING);
        }

        AutoConfig.register(ZLibsConfig.class, CustomConfigSerializer::new);
        CustomConfigSerializer.loadAndReplaceConfig();
        ZLibsConfig config = AutoConfig.getConfigHolder(ZLibsConfig.class).getConfig();
        if (Files.exists(configFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (Reader reader = Files.newBufferedReader(configFile)) {
                config = gson.fromJson(reader, ZLibsConfig.class);
            } catch (IOException e) {
                throw new RuntimeException("Failed to reload config after file move: " + e.getMessage(), e);
            }
            AutoConfig.getConfigHolder(ZLibsConfig.class).setConfig(config);
        }

        Path pluginsDir = FabricLoader.getInstance().getConfigDir().resolve("xdlib");
        pluginsDir = pluginsDir.resolve("plugins");
        Files.createDirectories(pluginsDir);

        if (config.main.disablePlugins) {
            pluginReader.start();
        }
    }

    public static ZLibsConfig getConfig() {
        return AutoConfig.getConfigHolder(ZLibsConfig.class).getConfig();
    }

    public static void registerSaveListener(boolean drpc, boolean si) {
        discordRPC = drpc;

        ConfigHolder<ZLibsConfig> holder = AutoConfig.getConfigHolder(ZLibsConfig.class);
        holder.registerSaveListener((configHolder, config) -> {
            discordRPC = drpc;
            applyConfig();
            return ActionResult.SUCCESS;
        });
    }

    public static ConfigSettings titleScreenMixinConfig() {
        ZLibsConfig config = AutoConfig.getConfigHolder(ZLibsConfig.class).getConfig();
        return new ConfigSettings(config.main.changelogEveryStartup);
    }

    public static ConfigSettings zlibsClientConfig() {
        ZLibsConfig config = AutoConfig.getConfigHolder(ZLibsConfig.class).getConfig();
        return new ConfigSettings(
                config.main.discordRPC,
                config.lastServer.serverName,
                config.lastServer.serverAddress
        );
    }

    public static void updateTitleScreenMixinConfig(boolean changelogFirstStartup, boolean changelogEveryStartup) {
        ZLibsConfig config = getConfig();
        config.main.changelogEveryStartup = changelogEveryStartup;
        AutoConfig.getConfigHolder(ZLibsConfig.class).save();
    }

    public static void applyConfig() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (discordRPC) {
            if (osName.contains("win")) {
                DiscordRPCHandler.init();
                ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
                    DiscordRPCHandler.shutdown();
                });
            }
        } else {
            DiscordRPCHandler.shutdown();
        }
    }

    public static boolean isChangelogScreenDisabled() { return getConfig().main.disableChangelog; }

    public static boolean isTitlePopupsDisabled() {
        return getConfig().main.disableTitlePopups;
    }

    public static boolean isDiscordRPCEnabled() {
        return getConfig().main.discordRPC;
    }

    public static class ConfigSettings {
        public boolean changelogEveryStartup;
        public boolean discordRPC;
        public boolean sodiumIntegration;
        public String configServerName;
        public String configServerAddress;

        public ConfigSettings(boolean changelogEveryStartup) {
            this.changelogEveryStartup = changelogEveryStartup;
        }

        public ConfigSettings(
                boolean discordRPC,
                boolean sodiumIntegration,
                String configServerName,
                String configServerAddress
        ) {
            this.discordRPC = discordRPC;
            this.sodiumIntegration = sodiumIntegration;
            this.configServerName = configServerName;
            this.configServerAddress = configServerAddress;
        }

        public ConfigSettings(boolean discordRPC, String serverName, String serverAddress) {
        }
    }
}