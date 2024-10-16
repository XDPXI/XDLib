package dev.xdpxi.xdlib.api.configClass;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.xdpxi.xdlib.api.config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.stream.Collectors;

public class modmenu implements ModMenuApi {
    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        return config.getAllRegisteredConfigs().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> parent -> createConfigScreen(parent, entry.getValue())
                ));
    }

    private Screen createConfigScreen(Screen parent, config<? extends Configurable> config) {
        return new OptionsScreen(parent, MinecraftClient.getInstance().options) {
            @Override
            public void init() {
                int yOffset = this.height / 6 + 24;

                this.addDrawableChild(ButtonWidget.builder(Text.of("Save config"), (button) -> {
                    config.saveConfig();
                    this.client.getToastManager().add(
                            SystemToast.create(this.client, SystemToast.Type.WORLD_BACKUP, Text.of("config Saved!"), null)
                    );
                }).dimensions(this.width / 2 - 100, yOffset, 200, 20).build());

                this.addDrawableChild(ButtonWidget.builder(Text.of("Reset config"), (button) -> {
                    config.loadConfig();  // Assuming loadConfig resets it
                    this.client.getToastManager().add(
                            SystemToast.create(this.client, SystemToast.Type.WORLD_BACKUP, Text.of("config Reset!"), null)
                    );
                }).dimensions(this.width / 2 - 100, yOffset + 25, 200, 20).build());

                this.addDrawableChild(ButtonWidget.builder(Text.of("Back"), (button) -> {
                    this.client.setScreen(parent);
                }).dimensions(this.width / 2 - 100, yOffset + 50, 200, 20).build());
            }

            public void onClose() {
                super.close();
                config.saveConfig();
            }
        };
    }
}