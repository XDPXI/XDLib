package dev.xdpxi.xdlib.config;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

import static dev.xdpxi.xdlib.XDsLibraryClient.LOGGER;

public class pluginManager {
    public static void start() {
        File pluginsDir = new File("config" + File.separator + "xdlib" + File.separator + "plugins");

        if (!pluginsDir.exists() || !pluginsDir.isDirectory()) {
            LOGGER.warn("[XDLib] - Plugins directory does not exist: " + pluginsDir.getAbsolutePath());
            return;
        }

        LOGGER.info("[XDLib] - Reading Plugins...");
        File[] pluginFiles = pluginsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));

        if (pluginFiles != null && pluginFiles.length > 0) {
            LOGGER.info("[XDLib] - Running Plugins:");
            for (File pluginFile : pluginFiles) {
                try {
                    LOGGER.info("[XDLib] - Loading plugin: " + pluginFile.getName());
                    loadJar(pluginFile);
                } catch (Exception e) {
                    LOGGER.error("[XDLib] - Failed to load plugin: " + pluginFile.getName(), e);
                }
            }
        } else {
            LOGGER.warn("[XDLib] - No plugin files found in directory: " + pluginsDir.getAbsolutePath());
        }
    }

    private static void loadJar(File jarFile) throws Exception {
        try (URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{jarFile.toURI().toURL()})) {
            String mainClassName = loadMainClassName(classLoader, jarFile);
            if (mainClassName != null) {
                loadAndInvokeMainClass(mainClassName, classLoader, jarFile);
            }
        } catch (Exception e) {
            LOGGER.error("[XDLib] - Error processing JAR file: " + jarFile.getName(), e);
        }
    }

    private static String loadMainClassName(URLClassLoader classLoader, File jarFile) {
        try (InputStream inputStream = classLoader.getResourceAsStream("xdlib.yml")) {
            if (inputStream != null) {
                Yaml yaml = new Yaml();
                Map<String, Object> config = yaml.load(inputStream);
                return (String) config.get("main");
            } else {
                LOGGER.error("[XDLib] - xdlib.yml not found in plugin: " + jarFile.getName());
            }
        } catch (Exception e) {
            LOGGER.error("[XDLib] - Error reading xdlib.yml in plugin: " + jarFile.getName(), e);
        }
        return null;
    }

    private static void loadAndInvokeMainClass(String mainClassName, URLClassLoader classLoader, File jarFile) {
        try {
            Class<?> mainClass = classLoader.loadClass(mainClassName);
            Method onLoadMethod = mainClass.getMethod("onLoad");
            onLoadMethod.invoke(null);
            LOGGER.info("[XDLib] - Successfully loaded plugin: " + jarFile.getName());
        } catch (ClassNotFoundException e) {
            LOGGER.error("[XDLib] - Main class not found in plugin JAR: " + jarFile.getName(), e);
        } catch (NoSuchMethodException e) {
            LOGGER.error("[XDLib] - onLoad method not found in main class: " + mainClassName, e);
        } catch (Exception e) {
            LOGGER.error("[XDLib] - Error invoking onLoad method in plugin: " + jarFile.getName(), e);
        }
    }
}