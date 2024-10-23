package dev.xdpxi.xdlib.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "lastServer")
public class lastServer implements ConfigData {
    @Comment("Server Name of Last Played Server")
    public String serverName = "";

    @Comment("Server IP Address of Last Played Server")
    public String serverAddress = "";
}