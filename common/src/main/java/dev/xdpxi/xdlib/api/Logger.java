package dev.xdpxi.xdlib.api;

import org.apache.logging.log4j.LogManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A utility class for logging various log levels using Log4j.
 * Provides methods for logging messages at different severity levels: debug, info, warn, and error.
 */
public class Logger {
    /**
     * A map to hold loggers for each package name.
     */
    private static final Map<String, org.apache.logging.log4j.Logger> PACKAGE_LOGGERS = new ConcurrentHashMap<>();

    /**
     * The logger instance used for logging messages.
     * This is initialized with an empty string as the logger name.
     */
    public static org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger("default");

    /**
     * Sets up the logger for a specific package with a custom modID.
     * This method should be called before any log messages are logged to initialize the logger for the package.
     *
     * @param packageName The name of the package.
     * @param modID       The mod ID to be used as the logger name.
     */
    public static void setup(String packageName, String modID) {
        PACKAGE_LOGGERS.put(packageName, LogManager.getLogger(modID));
    }

    /**
     * Gets the logger for the calling class's package.
     * If no specific logger is set up, a default logger for the root package is returned.
     *
     * @return The logger instance for the calling class's package.
     */
    private static Logger getLogger() {
        String callingPackage = new Throwable().getStackTrace()[2].getClassName();
        String packageName = callingPackage.substring(0, callingPackage.lastIndexOf('.'));
        return (Logger) PACKAGE_LOGGERS.getOrDefault(packageName, LogManager.getLogger("default"));
    }

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