package dev.xdpxi.xdlib;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.xdpxi.xdlib.config.configManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    private static final configManager CONFIG_MANAGER = new configManager();

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return CONFIG_MANAGER::getModConfigScreen;
    }
}