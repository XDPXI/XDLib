package dev.xdpxi.xdlib.api;

public class convert {
    public static String toString(String convert) {
        return convert;
    }

    public static String toString(int convert) {
        return String.valueOf(convert);
    }

    public static String toString(boolean convert) {
        return String.valueOf(convert);
    }

    public static String toString(float convert) {
        return String.valueOf(convert);
    }

    public static int toInt(String convert) {
        return Integer.parseInt(convert);
    }

    public static int toInt(int convert) {
        return convert;
    }

    public static int toInt(boolean convert) {
        return convert ? 1 : 0;
    }

    public static int toInt(float convert) {
        return (int) convert;
    }

    public static boolean toBoolean(String convert) {
        return Boolean.parseBoolean(convert);
    }

    public static boolean toBoolean(int convert) {
        return convert != 0;
    }

    public static boolean toBoolean(boolean convert) {
        return convert;
    }

    public static boolean toBoolean(float convert) {
        return convert != 0.0f;
    }

    public static float toFloat(String convert) {
        return Float.parseFloat(convert);
    }

    public static float toFloat(int convert) {
        return (float) convert;
    }

    public static float toFloat(boolean convert) {
        return convert ? 1.0f : 0.0f;
    }

    public static float toFloat(float convert) {
        return convert;
    }
}