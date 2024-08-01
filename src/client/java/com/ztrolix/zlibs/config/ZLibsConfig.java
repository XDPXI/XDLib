package com.ztrolix.zlibs.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "ztrolix-libs")
public class ZLibsConfig implements ConfigData {
    public boolean modEnabled = true;
    public boolean injectToWorld = true;
    public boolean contributeToPlayerCount = true;
}