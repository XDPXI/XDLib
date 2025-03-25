package dev.xdpxi.xdlib.api.v5;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.xdpxi.xdlib.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Config management utility for XDLib.
 */
public class Configuration {
    /**
     * Annotation to define setup information for configuration classes.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Setup {
        String name();

        String file();
    }

    /**
     * Annotation to mark fields as belonging to a specific configuration category.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Category {
        String value();
    }

    /**
     * Annotation to indicate that a field requires a restart to apply changes.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface RestartRequired {
        boolean value() default true;
    }

    /**
     * Marker interface for configuration classes.
     */
    public interface Config {
    }

    /**
     * Handles loading and saving configuration files.
     */
    public static class ConfigLoader {
        private static final Gson GSON = new Gson();

        /**
         * Loads the configuration from the file specified in the {@link Setup} annotation.
         *
         * @param configClass The configuration class.
         */
        public static void loadConfig(Class<? extends Config> configClass) {
            Setup setup = configClass.getAnnotation(Setup.class);
            if (setup == null) {
                throw new IllegalArgumentException("[XDLib/Config] - Config class must be annotated with @Setup");
            }

            File configFile = new File(setup.file());
            String extension = getFileExtension(configFile);

            if (!configFile.exists()) {
                Log.warn("[XDLib/Config] - Config file not found, creating default...");
                try {
                    createDefaultConfig(configClass, configFile);
                } catch (Exception e) {
                    Log.error("[XDLib/Config] - Failed to create default config: " + e.getMessage());
                }
                return;
            }

            if ("json".equals(extension)) {
                Log.info("[XDLib/Config] - Loading JSON config from " + configFile.getAbsolutePath());
                try (FileReader reader = new FileReader(configFile)) {
                    Map<String, Object> configValues = GSON.fromJson(reader, new TypeToken<Map<String, Object>>() {
                    }.getType());
                    applyConfigValues(configClass, configValues);
                } catch (Exception e) {
                    Log.error("[XDLib/Config] - Failed to load config: " + e.getMessage());
                }
            } else {
                Log.error("[XDLib/Config] - Unsupported config format: " + extension);
            }
        }

        /**
         * Creates a default configuration file based on the configuration class.
         */
        private static void createDefaultConfig(Class<? extends Config> configClass, File configFile) throws IOException, IllegalAccessException {
            File parentDir = configFile.getParentFile();
            if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
                throw new IOException("[XDLib/Config] - Failed to create directory: " + parentDir.getAbsolutePath());
            }

            Map<String, Object> defaultConfig = getDefaultConfigValues(configClass);
            try (FileWriter writer = new FileWriter(configFile)) {
                GSON.toJson(defaultConfig, writer);
                Log.info("[XDLib/Config] - Default config created at " + configFile.getAbsolutePath());
            }
        }

        /**
         * Retrieves default values for the fields in the configuration class.
         */
        private static Map<String, Object> getDefaultConfigValues(Class<? extends Config> configClass) throws IllegalAccessException {
            Map<String, Object> configValues = new HashMap<>();
            for (Field field : configClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(Category.class)) {
                    field.setAccessible(true);
                    configValues.put(field.getName(), getDefaultValue(field));
                }
            }
            return configValues;
        }

        /**
         * Determines the default value for a given field based on its type.
         */
        private static Object getDefaultValue(Field field) {
            Class<?> fieldType = field.getType();
            if (fieldType == boolean.class) return false;
            if (fieldType == int.class) return 0;
            if (fieldType == double.class) return 0.0;
            if (fieldType == float.class) return 0.0f;
            if (fieldType == long.class) return 0L;
            if (fieldType == String.class) return "";
            if (fieldType == List.class) return new ArrayList<>();
            if (fieldType == Map.class) return new HashMap<>();
            return null;
        }

        /**
         * Applies configuration values to the fields of the configuration class.
         */
        private static void applyConfigValues(Class<? extends Config> configClass, Map<String, Object> configValues) {
            for (Field field : configClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(Category.class)) {
                    Object value = configValues.get(field.getName());
                    if (value != null && isTypeCompatible(field, value)) {
                        try {
                            field.setAccessible(true);
                            field.set(null, value);
                        } catch (IllegalAccessException e) {
                            Log.error("[XDLib/Config] - Failed to set field " + field.getName() + ": " + e.getMessage());
                        }
                    } else {
                        Log.warn("[XDLib/Config] - Skipping incompatible value for field: " + field.getName());
                    }
                }
            }
        }

        /**
         * Checks if the value matches the expected field type.
         */
        private static boolean isTypeCompatible(Field field, Object value) {
            Class<?> fieldType = field.getType();
            return (fieldType.isInstance(value)) ||
                    (fieldType == int.class && value instanceof Number) ||
                    (fieldType == double.class && value instanceof Number) ||
                    (fieldType == float.class && value instanceof Number) ||
                    (fieldType == long.class && value instanceof Number) ||
                    (fieldType == boolean.class && value instanceof Boolean) ||
                    (fieldType == String.class && value instanceof String) ||
                    (fieldType == List.class && value instanceof List) ||
                    (fieldType == Map.class && value instanceof Map);
        }

        /**
         * Retrieves the file extension of a given file.
         */
        private static String getFileExtension(File file) {
            String name = file.getName();
            int lastDot = name.lastIndexOf('.');
            return (lastDot == -1) ? "" : name.substring(lastDot + 1).toLowerCase();
        }

        /**
         * Saves the current configuration to a file.
         */
        public static void saveConfig(Class<? extends Config> configClass, String configFilePath) {
            File configFile = new File(configFilePath);
            String extension = getFileExtension(configFile);

            if ("json".equals(extension)) {
                try {
                    Map<String, Object> configValues = getDefaultConfigValues(configClass);
                    try (FileWriter writer = new FileWriter(configFile)) {
                        GSON.toJson(configValues, writer);
                        Log.info("[XDLib/Config] - Config saved to " + configFilePath);
                    }
                } catch (Exception e) {
                    Log.error("[XDLib/Config] - Failed to save config: " + e.getMessage());
                }
            } else {
                Log.error("[XDLib/Config] - Unsupported config format: " + extension);
            }
        }
    }
}