package dev.xdpxi.xdlib.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.xdpxi.xdlib.api.mod.loader;
import dev.xdpxi.xdlib.config.clothConfig.getModConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class modMenuIntegration implements ModMenuApi {
    public static final configManager CONFIG_MANAGER = new configManager();
    private static final boolean clothConfig = loader.isModLoaded("cloth-config");

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (clothConfig) {
            return parent -> new getModConfigScreen(CONFIG_MANAGER).getModConfigScreen(parent);
        }
        return null;
    }
}