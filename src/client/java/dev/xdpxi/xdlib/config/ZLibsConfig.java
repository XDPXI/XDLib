package dev.xdpxi.xdlib.config;

import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;

@Config(name = "xdlib")
public class ZLibsConfig extends PartitioningSerializer.GlobalData {
    @ConfigEntry.Category("main")
    @ConfigEntry.Gui.TransitiveObject
    public main main = new main();

    @ConfigEntry.Category("plugins")
    @ConfigEntry.Gui.TransitiveObject
    public plugins plugins = new plugins();

    @ConfigEntry.Category("lastServer")
    @ConfigEntry.Gui.TransitiveObject
    public lastServer lastServer = new lastServer();
}