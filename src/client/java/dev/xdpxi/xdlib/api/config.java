package dev.xdpxi.xdlib.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import dev.xdpxi.xdlib.api.configClass.Configurable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

public class config<T extends Configurable> {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final TomlWriter TOML_WRITER = new TomlWriter();
    private static final Map<String, config<?>> registeredConfigs = new HashMap<>();
    private File CONFIG_FILE = new File("config/temp.tmp");
    private FileType fileType = FileType.JSON;
    private T configInstance;

    public static config<?> getConfig(String modId) {
        return registeredConfigs.get(modId);
    }

    public static Map<String, config<?>> getAllRegisteredConfigs() {
        return registeredConfigs;
    }

    public void registerConfig(String modId, String name, String format, T instance) {
        configInstance = instance;
        if (format.equalsIgnoreCase("toml")) {
            CONFIG_FILE = new File("config/" + modId + "/" + name + ".toml");
            fileType = FileType.TOML;
        } else {
            CONFIG_FILE = new File("config/" + modId + "/" + name + ".json");
            fileType = FileType.JSON;
        }

        if (!CONFIG_FILE.exists()) {
            saveConfig();
        } else {
            loadConfig();
        }

        registeredConfigs.put(modId, this);
    }

    public void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            switch (fileType) {
                case TOML:
                    TOML_WRITER.write(configInstance, writer);
                    break;
                case JSON:
                    GSON.toJson(configInstance, writer);
                    break;
            }
        } catch (IOException ignored) {
        }
    }

    public void loadConfig() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                switch (fileType) {
                    case TOML:
                        Toml toml = new Toml();
                        configInstance = (T) toml.read(reader).to(configInstance.getClass());
                        break;
                    case JSON:
                        configInstance = GSON.fromJson(reader, (Class<T>) getGenericType());
                        break;
                }
            } catch (IOException ignored) {
            }
        }
    }

    public T getConfig() {
        return configInstance;
    }

    private Class<?> getGenericType() {
        return (Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    private enum FileType {JSON, TOML}
}