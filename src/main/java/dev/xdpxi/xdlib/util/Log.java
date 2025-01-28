package dev.xdpxi.xdlib.util;

import dev.xdpxi.xdlib.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.MOD_ID);

    public static void debug(String message, Object... args) {
        LOGGER.debug(message, args);
    }

    public static void info(String message, Object... args) {
        LOGGER.info(message, args);
    }

    public static void warn(String message, Object... args) {
        LOGGER.warn(message, args);
    }

    public static void error(String message, Object... args) {
        LOGGER.error(message, args);
    }

    public static void error(String message, Throwable throwable) {
        LOGGER.error(message, throwable);
    }
}