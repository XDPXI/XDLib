package dev.xdpxi.xdlib.api.v7.config;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dev.xdpxi.xdlib.Common.log;

public class ConfigManager {

    private static final String CONFIG_DIR = "config";

    /**
     * Loads a config from disk into a new instance of the given class.
     * If no file exists, saves defaults and returns the default instance.
     * Fields missing from the file keep their default values.
     * <p>
     * Supported field types: boolean, int, long, float, double, String, List
     */
    public static <T extends ConfigData> T load(Class<T> clazz) throws IOException {
        Config annotation = requireAnnotation(clazz);
        String modId = annotation.name();
        File configFile = getConfigFile(modId);

        T instance = newInstance(clazz);

        if (!configFile.exists()) {
            save(instance);
            return instance;
        }

        Map<String, Object> data;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8))) {
            LoaderOptions options = new LoaderOptions();
            Yaml yaml = new Yaml(new SafeConstructor(options));
            data = yaml.load(reader);
        } catch (IOException e) {
            log.error("[XDLib] - Failed to read config '{}', using defaults", modId);
            return instance;
        }

        if (data == null || data.isEmpty()) {
            return instance;
        }

        for (Field field : declaredConfigFields(clazz)) {
            Object raw = data.get(field.getName());
            if (raw == null) continue;
            try {
                applyField(instance, field, raw);
            } catch (Exception e) {
                log.warn("[XDLib] - Config '{}': could not load field '{}', keeping default", modId, field.getName());
            }
        }

        log.debug("[XDLib] - Loaded config '{}'", modId);
        return instance;
    }

    /**
     * Saves the config instance to config/modid.yml atomically.
     * Comments from @Comment are written as # lines above each field.
     */
    public static <T extends ConfigData> void save(T config) throws IOException {
        Class<?> clazz = config.getClass();
        Config annotation = requireAnnotation(clazz);
        String modId = annotation.name();
        File configFile = getConfigFile(modId);

        StringBuilder yaml = new StringBuilder();
        for (Field field : declaredConfigFields(clazz)) {
            Comment comment = field.getAnnotation(Comment.class);
            if (comment != null) {
                for (String line : comment.value().split("\n", -1)) {
                    yaml.append("# ").append(line).append("\n");
                }
            }

            Object value;
            try {
                value = field.get(config);
            } catch (IllegalAccessException e) {
                continue;
            }

            yaml.append(field.getName()).append(": ").append(serializeValue(value)).append("\n");
        }

        File tempFile = new File(configFile.getParent(), configFile.getName() + ".tmp");
        try (FileWriter writer = new FileWriter(tempFile, StandardCharsets.UTF_8)) {
            writer.write(yaml.toString());
        } catch (IOException e) {
            if (tempFile.exists()) tempFile.delete();
            throw new IOException("Failed to write config '" + modId + "'", e);
        }

        try {
            Files.move(tempFile.toPath(), configFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            if (tempFile.exists()) tempFile.delete();
            throw new IOException("Failed to save config '" + modId + "'", e);
        }

        log.debug("[XDLib] - Saved config '{}'", modId);
    }

    // -------------------------------------------------------------------------

    private static void applyField(Object instance, Field field, Object raw) throws IllegalAccessException {
        Class<?> type = field.getType();
        if (type == boolean.class || type == Boolean.class) {
            field.set(instance, raw instanceof Boolean b ? b : Boolean.parseBoolean(String.valueOf(raw)));
        } else if (type == int.class || type == Integer.class) {
            field.set(instance, raw instanceof Number n ? n.intValue() : Integer.parseInt(String.valueOf(raw)));
        } else if (type == long.class || type == Long.class) {
            field.set(instance, raw instanceof Number n ? n.longValue() : Long.parseLong(String.valueOf(raw)));
        } else if (type == float.class || type == Float.class) {
            field.set(instance, raw instanceof Number n ? n.floatValue() : Float.parseFloat(String.valueOf(raw)));
        } else if (type == double.class || type == Double.class) {
            field.set(instance, raw instanceof Number n ? n.doubleValue() : Double.parseDouble(String.valueOf(raw)));
        } else if (type == String.class) {
            field.set(instance, String.valueOf(raw));
        } else if (List.class.isAssignableFrom(type) && raw instanceof List<?> list) {
            field.set(instance, new ArrayList<>(list));
        }
    }

    private static String serializeValue(Object value) {
        if (value == null) return "null";
        if (value instanceof String s) return serializeString(s);
        if (value instanceof Boolean || value instanceof Number) return String.valueOf(value);
        if (value instanceof List<?> list) {
            if (list.isEmpty()) return "[]";
            StringBuilder sb = new StringBuilder();
            for (Object item : list) {
                sb.append("\n  - ").append(item instanceof String s ? serializeString(s) : item);
            }
            return sb.toString();
        }
        return String.valueOf(value);
    }

    private static String serializeString(String s) {
        if (s.isEmpty()) return "''";
        boolean needsQuotes = s.contains(":") || s.contains("#") || s.contains("'") ||
                s.startsWith(" ") || s.endsWith(" ") ||
                s.equals("true") || s.equals("false") || s.equals("null") ||
                s.equals("yes") || s.equals("no") ||
                s.matches("-?\\d+(\\.\\d+)?([eE][+-]?\\d+)?");
        if (!needsQuotes) return s;
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private static List<Field> declaredConfigFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        for (Field f : clazz.getDeclaredFields()) {
            if (f.isSynthetic() || Modifier.isStatic(f.getModifiers())) continue;
            f.setAccessible(true);
            fields.add(f);
        }
        return fields;
    }

    private static Config requireAnnotation(Class<?> clazz) {
        Config ann = clazz.getAnnotation(Config.class);
        if (ann == null) throw new IllegalArgumentException(clazz.getName() + " must be annotated with @Config");
        return ann;
    }

    @SuppressWarnings("unchecked")
    private static <T> T newInstance(Class<T> clazz) throws IOException {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IOException("Cannot instantiate config class " + clazz.getName() +
                    " — ensure it has a public no-arg constructor", e);
        }
    }

    private static File getConfigFile(String modId) throws IOException {
        File dir = new File(CONFIG_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Failed to create config directory: " + CONFIG_DIR);
        }
        return new File(dir, modId + ".yml");
    }
}
