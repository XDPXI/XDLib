package dev.xdpxi.xdlib.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

public class Config {
    // Annotation for setup configuration
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Setup {
        String name();

        String file();
    }

    // Annotation for category in configuration
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Category {
        String value();
    }

    // Annotation for restart required fields
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface RestartRequired {
        boolean value() default true;
    }

    // This interface will be implemented by all configuration classes
    public interface Configuration {
    }

    // Main Config loader
    public static class ConfigLoader {

        private static final Gson gson = new Gson();

        /**
         * Loads configuration for a given class annotated with @Setup
         *
         * @param configClass The configuration class to load.
         * @throws Exception If an error occurs during the config loading process.
         */
        public static void loadConfig(Class<? extends Configuration> configClass) throws Exception {
            Setup setup = configClass.getAnnotation(Setup.class);
            if (setup == null) {
                throw new IllegalArgumentException("Config class must be annotated with @Setup");
            }

            String configFilePath = setup.file();
            File configFile = new File(configFilePath);
            String extension = getFileExtension(configFile);

            if (configFile.exists()) {
                switch (extension) {
                    case "json":
                        loadJsonConfig(configClass, configFile);
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported config file format: " + extension);
                }
            } else {
                createDefaultConfig(configClass, configFile, extension);
            }
        }

        /**
         * Loads a JSON configuration file and applies the values to the configuration class.
         *
         * @param configClass The configuration class to load values into.
         * @param configFile  The JSON config file.
         * @throws IOException            If an error occurs during file reading.
         * @throws IllegalAccessException If an error occurs while setting values on fields.
         */
        private static void loadJsonConfig(Class<? extends Configuration> configClass, File configFile)
                throws IOException, IllegalAccessException {
            System.out.println("Loading JSON config from " + configFile.getAbsolutePath());
            try (FileReader reader = new FileReader(configFile)) {
                Map<String, Object> configValues = gson.fromJson(reader, new TypeToken<Map<String, Object>>() {
                }.getType());
                applyConfigValues(configClass, configValues);
            }
        }

        /**
         * Creates a default config if it doesn't exist, based on the class annotations.
         *
         * @param configClass The configuration class to create a default config for.
         * @param configFile  The file where the config will be saved.
         * @param extension   The file extension (e.g., json).
         * @throws IOException            If an error occurs while writing the config file.
         * @throws IllegalAccessException If an error occurs while reading field values.
         */
        private static void createDefaultConfig(Class<? extends Configuration> configClass, File configFile,
                                                String extension) throws IOException, IllegalAccessException {
            System.out.println("Creating default config at " + configFile.getAbsolutePath());
            if ("json".equals(extension)) {
                createDefaultJsonConfig(configClass, configFile);
            } else {
                throw new IllegalArgumentException("Unsupported config file format: " + extension);
            }
        }

        /**
         * Creates a default JSON config file based on the class annotations.
         *
         * @param configClass The configuration class.
         * @param configFile  The file where the config will be saved.
         * @throws IOException            If an error occurs during file writing.
         * @throws IllegalAccessException If an error occurs while reading field values.
         */
        private static void createDefaultJsonConfig(Class<? extends Configuration> configClass, File configFile)
                throws IOException, IllegalAccessException {
            Map<String, Object> defaultConfig = getDefaultConfigValues(configClass);
            try (FileWriter writer = new FileWriter(configFile)) {
                gson.toJson(defaultConfig, writer);
            }
        }

        /**
         * Retrieves the default values for a configuration class.
         *
         * @param configClass The configuration class.
         * @return A map of field names and their default values.
         * @throws IllegalAccessException If an error occurs while reading field values.
         */
        private static Map<String, Object> getDefaultConfigValues(Class<? extends Configuration> configClass)
                throws IllegalAccessException {
            Map<String, Object> configValues = new HashMap<>();
            for (Field field : configClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(Category.class)) {
                    configValues.put(field.getName(), getDefaultValue(field));
                }
            }
            return configValues;
        }

        /**
         * Provides a default value based on the field type.
         *
         * @param field The field to get the default value for.
         * @return The default value for the field.
         */
        private static Object getDefaultValue(Field field) {
            Class<?> fieldType = field.getType();
            if (fieldType.equals(boolean.class)) {
                return false;
            } else if (fieldType.equals(int.class)) {
                return 0;
            } else if (fieldType.equals(String.class)) {
                return "";
            }
            return null;
        }

        /**
         * Applies the loaded configuration values to the fields of the configuration class.
         *
         * @param configClass  The configuration class.
         * @param configValues A map of field names to their respective values.
         * @throws IllegalAccessException If an error occurs while setting values on fields.
         */
        private static void applyConfigValues(Class<? extends Configuration> configClass,
                                              Map<String, Object> configValues) throws IllegalAccessException {
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
         * Gets the file extension from the given file.
         *
         * @param file The file to extract the extension from.
         * @return The file extension (e.g., json).
         */
        private static String getFileExtension(File file) {
            String name = file.getName();
            int lastDot = name.lastIndexOf('.');
            return (lastDot == -1) ? "" : name.substring(lastDot + 1).toLowerCase();
        }
    }
}