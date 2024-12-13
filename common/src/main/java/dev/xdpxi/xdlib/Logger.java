package dev.xdpxi.xdlib;

public class Logger {
    public static void debug(String log) {
        Constants.LOG.debug("{}", log);
    }

    public static void info(String log) {
        Constants.LOG.info("{}", log);
    }

    public static void warn(String log) {
        Constants.LOG.warn("{}", log);
    }

    public static void error(String log) {
        Constants.LOG.error("{}", log);
    }
}