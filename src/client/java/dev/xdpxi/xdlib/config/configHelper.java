package dev.xdpxi.xdlib.config;

import dev.xdpxi.xdlib.DiscordRPCHandler;
import dev.xdpxi.xdlib.sodium.CustomOptions;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class configHelper {
    public static final Logger LOGGER = LoggerFactory.getLogger("xdlib");
    private static boolean discordRPC = true;
    private static boolean sodiumIntegration = true;

    public static void registerConfig() {
        AutoConfig.register(ZLibsConfig.class, CustomConfigSerializer::new);
    }

    public static ZLibsConfig getConfig() {
        return AutoConfig.getConfigHolder(ZLibsConfig.class).getConfig();
    }

    public static void registerSaveListener(boolean drpc, boolean si) {
        discordRPC = drpc;
        sodiumIntegration = si;

        ConfigHolder<ZLibsConfig> holder = AutoConfig.getConfigHolder(ZLibsConfig.class);
        holder.registerSaveListener((configHolder, config) -> {
            discordRPC = drpc;
            sodiumIntegration = si;
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
                config.compatibility.discordRPC,
                config.compatibility.sodiumIntegration,
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
        if (sodiumIntegration) {
            CustomOptions.integrate();
        }
    }

    public static boolean isChangelogScreenDisabled() { return getConfig().main.disableChangelog; }

    public static boolean isTitlePopupsDisabled() {
        return getConfig().main.disableTitlePopups;
    }

    public static boolean isDiscordRPCEnabled() {
        return getConfig().compatibility.discordRPC;
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
    }
}