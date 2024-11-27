package dev.xdpxi.xdlib.config.clothConfig;

import dev.xdpxi.xdlib.config.configManager;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class modConfigScreen {
    public Screen getModConfigScreen(Screen parent) {
        configManager.ConfigData configData = configManager.configData;

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.of("XDLib Configuration"));

        builder.getOrCreateCategory(Text.of("Main"))
                .addEntry(createBooleanToggle(builder, "Enable Custom Modmenu Badges", configData.isCustomBadges(), configData::setCustomBadges, true))
                .addEntry(createBooleanToggle(builder, "Enable Title Screen Warnings", configData.isTitlePopups(), configData::setTitlePopups, false));

        return builder.build();
    }

    private BooleanListEntry createBooleanToggle(ConfigBuilder builder, String text, boolean initialValue, Consumer<Boolean> consumer, boolean defaultValue) {
        return builder.entryBuilder()
                .startBooleanToggle(Text.of(text), initialValue)
                .setDefaultValue(defaultValue)
                .setSaveConsumer(consumer)
                .build();
    }
}