package dev.xdpxi.xdlib.config.clothConfig;

import dev.xdpxi.xdlib.config.configManager;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class getModConfigScreen {
    private final configManager configManagerInstance;

    public getModConfigScreen(configManager configManager) {
        this.configManagerInstance = configManager;
    }

    public Screen getModConfigScreen(Screen parent) {
        configManager.ConfigData configData = configManager.configData;

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.of("XDLib Configuration"));

        builder.getOrCreateCategory(Text.of("Main"))
                .addEntry(builder.entryBuilder()
                        .startBooleanToggle(Text.of("Add Custom Modmenu Badges"), configData.isCustomBadges())
                        .setDefaultValue(true)
                        .setSaveConsumer(newVersion -> {
                            configData.setCustomBadges(newVersion);
                            write(configData);
                        })
                        .build()
                )
                .addEntry(builder.entryBuilder()
                        .startBooleanToggle(Text.of("Show Changelog On Every Startup"), configData.isChangelogEveryStartup())
                        .setDefaultValue(false)
                        .setSaveConsumer(newVersion -> {
                            configData.setChangelogEveryStartup(newVersion);
                            write(configData);
                        })
                        .build()
                )
                .addEntry(builder.entryBuilder()
                        .startBooleanToggle(Text.of("Enable Discord RPC"), configData.isDiscordRPC())
                        .setDefaultValue(true)
                        .setSaveConsumer(newVersion -> {
                            configData.setDiscordRPC(newVersion);
                            write(configData);
                        })
                        .build()
                )
                .addEntry(builder.entryBuilder()
                        .startBooleanToggle(Text.of("Disable Plugins"), configData.isDisablePlugins())
                        .setDefaultValue(false)
                        .setSaveConsumer(newVersion -> {
                            configData.setDisablePlugins(newVersion);
                            write(configData);
                        })
                        .build()
                )
                .addEntry(builder.entryBuilder()
                        .startBooleanToggle(Text.of("Disable Changelog"), configData.isDisableChangelog())
                        .setDefaultValue(false)
                        .setSaveConsumer(newVersion -> {
                            configData.setDisableChangelog(newVersion);
                            write(configData);
                        })
                        .build()
                )
                .addEntry(builder.entryBuilder()
                        .startBooleanToggle(Text.of("Disable Title Screen Warnings"), configData.isDisableTitlePopups())
                        .setDefaultValue(false)
                        .setSaveConsumer(newVersion -> {
                            configData.setDisableTitlePopups(newVersion);
                            write(configData);
                        })
                        .build()
                );

        builder.getOrCreateCategory(Text.of("Plugins"))
                .addEntry(builder.entryBuilder()
                        .startBooleanToggle(Text.of("Enable Unverified Plugins"), configData.isUnverifiedPlugins())
                        .setDefaultValue(false)
                        .setSaveConsumer(newVersion -> {
                            configData.setUnverifiedPlugins(newVersion);
                            write(configData);
                        })
                        .build()
                )
                .addEntry(builder.entryBuilder()
                        .startBooleanToggle(Text.of("Enter Dev Mode"), configData.isDevMode())
                        .setDefaultValue(false)
                        .setSaveConsumer(newVersion -> {
                            configData.setDevMode(newVersion);
                            write(configData);
                        })
                        .build()
                );

        builder.getOrCreateCategory(Text.of("Last Played"))
                .addEntry(builder.entryBuilder()
                        .startStrField(Text.of("Server Name"), configData.getServerName())
                        .setDefaultValue("")
                        .setSaveConsumer(newVersion -> {
                            configData.setServerName(newVersion);
                            write(configData);
                        })
                        .build()
                )
                .addEntry(builder.entryBuilder()
                        .startStrField(Text.of("Server IP"), configData.getServerAddress())
                        .setDefaultValue("")
                        .setSaveConsumer(newVersion -> {
                            configData.setServerAddress(newVersion);
                            write(configData);
                        })
                        .build()
                );

        return builder.build();
    }

    private void write(configManager.ConfigData configData) {
        configManagerInstance.write(configData);
    }
}