package dev.xdpxi.xdlib;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.xdpxi.xdlib.util.Log;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static dev.xdpxi.xdlib.Main.MOD_ID;

public class UpdateChecker {
    private static final ModContainer modContainer = FabricLoader.getInstance().getModContainer(MOD_ID).orElse(null);
    private static final String MODRINTH_API_URL = "https://api.modrinth.com/v3/project/xdlib/version";
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public static void checkForUpdate() {
        if (modContainer == null) {
            Log.error("[XDLib/Updater] - Mod container not found for ID: " + MOD_ID);
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                URL url = URI.create(MODRINTH_API_URL).toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "XDLib-UpdateChecker/1.0 (Minecraft Mod)");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader in = new BufferedReader(
                            new InputStreamReader(connection.getInputStream())
                    )) {
                        String response = in.lines().reduce("", (a, b) -> a + b);
                        Version latestVersion = parseLatestVersion(response);
                        Version currentVersion = modContainer.getMetadata().getVersion();

                        if (latestVersion != null && currentVersion.compareTo(latestVersion) < 0) {
                            Log.warn("[XDLib/Updater] - An update is available! Current: {}, Latest: {}", currentVersion, latestVersion);
                            Log.warn("[XDLib/Updater] - Download the new version from: {}", "https://modrinth.com/plugin/xdlib");
                        } else {
                            Log.info("[XDLib/Updater] - No update available!");
                        }
                    }
                } else {
                    Log.error("[XDLib/Updater] - Failed to check for update. HTTP Response Code: {}", responseCode);
                }

                connection.disconnect();
            } catch (Exception e) {
                Log.error("[XDLib/Updater] - Failed to check for update: " + e.getMessage(), e);
            }
        }, EXECUTOR);
    }

    private static Version parseLatestVersion(String jsonResponse) {
        try {
            JsonArray versions = JsonParser.parseString(jsonResponse).getAsJsonArray();
            if (!versions.isEmpty()) {
                JsonObject versionInfo = versions.get(0).getAsJsonObject();
                String versionNumber = versionInfo.get("version_number").getAsString();
                return Version.parse(versionNumber);
            }
        } catch (Exception e) {
            Log.error("Failed to parse latest version: " + e.getMessage(), e);
        }
        return null;
    }
}