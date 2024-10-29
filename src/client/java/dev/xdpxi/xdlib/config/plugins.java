package dev.xdpxi.xdlib.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "plugins")
public class plugins implements ConfigData{
    @ConfigEntry.Gui.RequiresRestart
    @Comment("Enable Unverified Plugins (Restart Required)")
    public boolean unverifiedPlugins = false;

    @ConfigEntry.Gui.RequiresRestart
    @Comment("Enter Developer Mode (Restart Required)")
    public boolean devMode = false;
}