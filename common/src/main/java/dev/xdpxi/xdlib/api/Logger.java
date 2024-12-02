package dev.xdpxi.xdlib.api;

import dev.xdpxi.xdlib.Main;
import org.apache.logging.log4j.LogManager;

/**
 * A utility class for logging various log levels using Log4j.
 * Provides methods for logging messages at different severity levels: debug, info, warn, and error.
 */
public class Logger {

    /**
     * The logger instance used for logging messages.
     */
    public static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(Main.MOD_ID);

    /**
     * Logs a message at the DEBUG level.
     *
     * @param log The message to be logged.
     */
    public static void debug(String log) {
        LOGGER.debug(log);
    }

    /**
     * Logs a message at the INFO level.
     *
     * @param log The message to be logged.
     */
    public static void info(String log) {
        LOGGER.info(log);
    }

    /**
     * Logs a message at the WARN level.
     *
     * @param log The message to be logged.
     */
    public static void warn(String log) {
        LOGGER.warn(log);
    }

    /**
     * Logs a message at the ERROR level.
     *
     * @param log The message to be logged.
     */
    public static void error(String log) {
        LOGGER.error(log);
    }
}