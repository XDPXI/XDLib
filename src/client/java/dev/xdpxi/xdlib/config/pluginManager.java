package dev.xdpxi.xdlib.config;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dev.xdpxi.xdlib.api.files;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static dev.xdpxi.xdlib.XDsLibraryClient.LOGGER;

public class pluginManager {
    private static final File PLUGINS_DIR = new File("config" + File.separator + "xdlib" + File.separator + "plugins");
    private static final File LOADED_PLUGINS_FILE = new File("config" + File.separator + "xdlib" + File.separator + "plugins.json");
    private static final Gson GSON = new Gson();
    private static final Set<String> loadedPlugins = new HashSet<>();

    private static boolean checkDir() {
        return PLUGINS_DIR.exists() && PLUGINS_DIR.isDirectory();
    }

    public static void start() throws IOException {
        if (!checkDir()) {
            LOGGER.warn("[XDLib] - Plugins directory does not exist: " + PLUGINS_DIR.getAbsolutePath());
            return;
        }

        files.deleteFile(LOADED_PLUGINS_FILE.toPath());

        loadPreviouslyLoadedPlugins();

        LOGGER.info("[XDLib] - Reading Plugins...");
        File[] pluginFiles = PLUGINS_DIR.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));

        if (pluginFiles != null && pluginFiles.length > 0) {
            LOGGER.info("[XDLib] - Running Plugins:");
            for (File pluginFile : pluginFiles) {
                String pluginName = pluginFile.getName();
                if (!loadedPlugins.contains(pluginName)) {
                    try {
                        LOGGER.info("[XDLib] - Loading plugin: " + pluginName);
                        loadJar(pluginFile);
                        loadedPlugins.add(pluginName);
                        saveLoadedPlugins();
                    } catch (Exception e) {
                        LOGGER.error("[XDLib] - Failed to load plugin: " + pluginName, e);
                    }
                } else {
                    LOGGER.info("[XDLib] - Plugin already loaded: " + pluginName);
                }
            }
        } else {
            LOGGER.warn("[XDLib] - No plugin files found in directory: " + PLUGINS_DIR.getAbsolutePath());
        }
    }

    private static void loadPreviouslyLoadedPlugins() {
        if (LOADED_PLUGINS_FILE.exists()) {
            try (Reader reader = Files.newBufferedReader(LOADED_PLUGINS_FILE.toPath())) {
                JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
                for (JsonElement element : jsonArray) {
                    loadedPlugins.add(element.getAsString());
                }
            } catch (Exception e) {
                LOGGER.error("[XDLib] - Error reading loaded plugins file", e);
            }
        }
    }

    private static void saveLoadedPlugins() {
        JsonArray jsonArray = new JsonArray();
        for (String plugin : loadedPlugins) {
            jsonArray.add(plugin);
        }
        try (Writer writer = Files.newBufferedWriter(LOADED_PLUGINS_FILE.toPath())) {
            GSON.toJson(jsonArray, writer);
        } catch (Exception e) {
            LOGGER.error("[XDLib] - Error saving loaded plugins file", e);
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