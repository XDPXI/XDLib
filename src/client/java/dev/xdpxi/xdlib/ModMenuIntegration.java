package dev.xdpxi.xdlib;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.xdpxi.xdlib.api.mod.loader;
import dev.xdpxi.xdlib.config.ZLibsConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

import java.lang.reflect.Method;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (loader.isModLoaded("cloth-config")) {
            try {
                Class<?> autoConfigClass = Class.forName("me.shedaniel.autoconfig.AutoConfig");
                Method getConfigScreenMethod = autoConfigClass.getMethod("getConfigScreen", Class.class, Screen.class);
                return (parent) -> {
                    try {
                        Object configScreen = getConfigScreenMethod.invoke(null, ZLibsConfig.class, parent);
                        Method getMethod = configScreen.getClass().getMethod("get");
                        Object result = getMethod.invoke(configScreen);
                        return (Screen) result;
                    } catch (Exception e) {
                        return null;
                    }
                };

            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}