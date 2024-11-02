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
        File pluginsDir = new File("config/xdlib/plugins");

        if (!pluginsDir.exists() || !pluginsDir.isDirectory()) {
            LOGGER.warn("[XDLib] - Plugins directory does not exist: " + pluginsDir.getAbsolutePath());
            return;
        }

        LOGGER.info("[XDLib] - Reading Plugins...");
        File[] pluginFiles = pluginsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));

        if (pluginFiles != null) {
            LOGGER.info("[XDLib] - Running Plugins:");
            for (File pluginFile : pluginFiles) {
                try {
                    LOGGER.info("[XDLib] - " + pluginFile);
                    loadJar(pluginFile);
                } catch (Exception e) {
                    LOGGER.error("[XDLib] - Failed to load plugin: " + pluginFile.getName(), e);
                }
            }
        }
    }

    private static void loadJar(File jarFile) throws Exception {
        String mainClassName = null;

        try (URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{jarFile.toURI().toURL()})) {
            InputStream inputStream = classLoader.getResourceAsStream("xdlib.yml");
            if (inputStream != null) {
                try {
                    Yaml yaml = new Yaml();
                    Map<String, Object> config = yaml.load(inputStream);
                    mainClassName = (String) config.get("main");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                LOGGER.error("[XDLib] - xdlib.yml not found for plugin: " + jarFile.getName());
                return;
            }

            if (mainClassName != null) {
                Class<?> mainClass = classLoader.loadClass(mainClassName);
                Method method = mainClass.getMethod("onLoad");
                method.invoke(null);
                LOGGER.info("[XDLib] - Successfully loaded plugin: " + jarFile.getName());
            }
        } catch (ClassNotFoundException e) {
            LOGGER.error("[XDLib] - Main class not found in JAR: " + jarFile.getName(), e);
        } catch (NoSuchMethodException e) {
            LOGGER.error("[XDLib] - onLoad method not found in class: " + mainClassName, e);
        } catch (Exception e) {
            LOGGER.error("[XDLib] - Error invoking method on plugin: " + jarFile.getName(), e);
        }
    }
}