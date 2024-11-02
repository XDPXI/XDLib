package dev.xdpxi.xdlib.config.clothConfig;

import dev.xdpxi.xdlib.config.configManager;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.StringListEntry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.function.Consumer;

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
                .addEntry(createBooleanToggle(builder, "Show Changelog On Every Startup", configData.isChangelogEveryStartup(), configData::setChangelogEveryStartup, false))
                .addEntry(createBooleanToggle(builder, "Disable Plugins", configData.isDisablePlugins(), newVersion -> {
                    configData.setDisablePlugins(newVersion);
                    updatePluginsCategory(builder, newVersion);
                    write(configData);
                }, false))
                .addEntry(createBooleanToggle(builder, "Disable Changelog", configData.isDisableChangelog(), configData::setDisableChangelog, false))
                .addEntry(createBooleanToggle(builder, "Disable Title Screen Warnings", configData.isDisableTitlePopups(), configData::setDisableTitlePopups, false));

        updatePluginsCategory(builder, configData.isDisablePlugins());

        builder.getOrCreateCategory(Text.of("Compatibility"))
                .addEntry(createBooleanToggle(builder, "Add Custom Modmenu Badges", configData.isCustomBadges(), configData::setCustomBadges, true))
                .addEntry(createBooleanToggle(builder, "Enable Discord RPC", configData.isDiscordRPC(), configData::setDiscordRPC, true));

        return builder.build();
    }

    private void write(configManager.ConfigData configData) {
        configManagerInstance.write(configData);
    }

    private BooleanListEntry createBooleanToggle(ConfigBuilder builder, String text, boolean initialValue, Consumer<Boolean> consumer, boolean defaultValue) {
        return builder.entryBuilder()
                .startBooleanToggle(Text.of(text), initialValue)
                .setDefaultValue(defaultValue)
                .setSaveConsumer(consumer)
                .build();
    }

    private StringListEntry createStringField(ConfigBuilder builder, String text, String initialValue, Consumer<String> consumer, String defaultValue) {
        return builder.entryBuilder()
                .startStrField(Text.of(text), initialValue)
                .setDefaultValue(defaultValue)
                .setSaveConsumer(consumer)
                .build();
    }

    private void updatePluginsCategory(ConfigBuilder builder, boolean isDisabled) {
        if (!isDisabled) {
            var pluginsCategory = builder.getOrCreateCategory(Text.of("Plugins"));
            pluginsCategory.getEntries().clear();

            pluginsCategory.addEntry(builder.entryBuilder()
                    .startSubCategory(Text.of("Loaded Plugins"))
                    .build());

            pluginsCategory.addEntry(createBooleanToggle(builder, "Enable Unverified Plugins", configManager.configData.isUnverifiedPlugins(), newVersion -> configManager.configData.setUnverifiedPlugins(newVersion), false));
            pluginsCategory.addEntry(createBooleanToggle(builder, "Enter Dev Mode", configManager.configData.isDevMode(), newVersion -> configManager.configData.setDevMode(newVersion), false));
        }
    }
}