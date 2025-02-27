package dev.xdpxi.xdlib.api.v4;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.xdpxi.xdlib.util.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration management utility for XDLib.
 */
public class Config {

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
    public interface Configuration {
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
         * @throws Exception If an error occurs during loading.
         */
        public static void loadConfig(Class<? extends Configuration> configClass) throws Exception {
            Setup setup = configClass.getAnnotation(Setup.class);
            if (setup == null) {
                throw new IllegalArgumentException("[XDLib/Config] - Config class must be annotated with @Setup");
            }

            File configFile = new File(setup.file());
            String extension = getFileExtension(configFile);

            if (configFile.exists()) {
                if ("json".equals(extension)) {
                    Logger.info("[XDLib/Config] - Loading JSON config from " + configFile.getAbsolutePath());
                    try (FileReader reader = new FileReader(configFile)) {
                        Map<String, Object> configValues = GSON.fromJson(reader, new TypeToken<Map<String, Object>>() {
                        }.getType());
                        applyConfigValues(configClass, configValues);
                    }
                } else {
                    throw new IllegalArgumentException("[XDLib/Config] - Unsupported config file format: " + extension);
                }
            } else {
                Logger.warn("[XDLib/Config] - Config file not found, creating default config...");
                createDefaultConfig(configClass, configFile);
            }
        }

        /**
         * Creates a default configuration file based on the configuration class.
         *
         * @param configClass The configuration class.
         * @param configFile  The file to save the default configuration to.
         * @throws IOException            If an error occurs during file creation.
         * @throws IllegalAccessException If a field cannot be accessed.
         */
        private static void createDefaultConfig(Class<? extends Configuration> configClass, File configFile) throws IOException, IllegalAccessException {
            File parentDir = configFile.getParentFile();
            if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
                throw new IOException("[XDLib/Config] - Failed to create directory: " + parentDir.getAbsolutePath());
            }

            Map<String, Object> defaultConfig = getDefaultConfigValues(configClass);
            try (FileWriter writer = new FileWriter(configFile)) {
                GSON.toJson(defaultConfig, writer);
                Logger.info("[XDLib/Config] - Default config created at " + configFile.getAbsolutePath());
            }
        }

        /**
         * Retrieves default values for the fields in the configuration class.
         *
         * @param configClass The configuration class.
         * @return A map of field names to default values.
         * @throws IllegalAccessException If a field cannot be accessed.
         */
        private static Map<String, Object> getDefaultConfigValues(Class<? extends Configuration> configClass) throws IllegalAccessException {
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
         *
         * @param field The field to determine the default value for.
         * @return The default value.
         */
        private static Object getDefaultValue(Field field) {
            Class<?> fieldType = field.getType();
            if (fieldType == boolean.class) {
                return false;
            } else if (fieldType == int.class) {
                return 0;
            } else if (fieldType == String.class) {
                return "";
            }
            return null;
        }

        /**
         * Applies configuration values to the fields of the configuration class.
         *
         * @param configClass  The configuration class.
         * @param configValues The map of configuration values.
         * @throws IllegalAccessException If a field cannot be accessed.
         */
        private static void applyConfigValues(Class<? extends Configuration> configClass, Map<String, Object> configValues) throws IllegalAccessException {
            for (Field field : configClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(Category.class)) {
                    Object value = configValues.get(field.getName());
                    if (value != null) {
                        field.setAccessible(true);
                        field.set(null, value);
                    }
                }
            }
        }

        /**
         * Retrieves the file extension of a given file.
         *
         * @param file The file.
         * @return The file extension.
         */
        private static String getFileExtension(File file) {
            String name = file.getName();
            int lastDot = name.lastIndexOf('.');
            return (lastDot == -1) ? "" : name.substring(lastDot + 1).toLowerCase();
        }

        /**
         * Saves the current configuration to a file.
         *
         * @param configClass    The configuration class.
         * @param configFilePath The file path to save the configuration to.
         * @throws IOException            If an error occurs during saving.
         * @throws IllegalAccessException If a field cannot be accessed.
         */
        public static void saveConfig(Class<? extends Configuration> configClass, String configFilePath) throws IOException, IllegalAccessException {
            File configFile = new File(configFilePath);
            String extension = getFileExtension(configFile);

            if ("json".equals(extension)) {
                Map<String, Object> configValues = getDefaultConfigValues(configClass);
                try (FileWriter writer = new FileWriter(configFile)) {
                    GSON.toJson(configValues, writer);
                    Logger.info("[XDLib/Config] - Config saved to " + configFilePath);
                }
            } else {
                throw new IllegalArgumentException("[XDLib/Config] - Unsupported config file format: " + extension);
            }
        }
    }
}