package com.ztrolix.zlibs.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "compatibility")
public class compatibility implements ConfigData {
    @Comment("Enable Discord RPC")
    public boolean discordRPC;

    public compatibility() {
        String osName = System.getProperty("os.name").toLowerCase();
        discordRPC = osName.contains("win");
    }
    @Comment("Add ZtrolixLibs to Sodium Video Settings (Not Working)")
    public boolean sodiumIntegration = false;
}