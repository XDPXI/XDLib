package dev.xdpxi.xdlib.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "main")
public class main implements ConfigData {
    @Comment("Add Custom Badges to Modmenu")
    public boolean customBadges = true;

    @Comment("Show Changelog On Every Startup")
    public boolean changelogEveryStartup = false;

    @ConfigEntry.Gui.RequiresRestart
    @Comment("Disable Custom XDLib Plugins (Restart Required)")
    public boolean disablePlugins = false;

    @Comment("Disable Changelog Screen")
    public boolean disableChangelog = false;

    @Comment("Disable Title Screen Popups")
    public boolean disableTitlePopups = false;
}