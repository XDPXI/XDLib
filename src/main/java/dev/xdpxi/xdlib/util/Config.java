package dev.xdpxi.xdlib.util;

import dev.xdpxi.xdlib.api.v5.Configuration;

@Configuration.Setup(name = "MyConfig", file = "config/xdlib/config.json")
public class Config implements Configuration.Config {
}