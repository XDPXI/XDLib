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

public class UpdateChecker {
    private static final String MOD_ID = "xdlib";
    private static final ModContainer modContainer = FabricLoader.getInstance().getModContainer(MOD_ID).orElse(null);
    private static final String MODRINTH_API_URL = "https://api.modrinth.com/v2/project/%s/version";
    private static final String PROJECT_SLUG = "xdlib";

    public static void checkForUpdate() {
        if (modContainer == null) {
            Log.error("[XDLib/Updater] - Mod container not found for ID: " + MOD_ID);
            return;
        }

        try {
            String apiUrl = String.format(MODRINTH_API_URL, PROJECT_SLUG);
            URL url = URI.create(apiUrl).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String response = in.lines().reduce("", (a, b) -> a + b);
                Version latestVersion = parseLatestVersion(response);
                Version currentVersion = modContainer.getMetadata().getVersion();

                if (latestVersion != null && currentVersion.compareTo(latestVersion) < 0) {
                    Log.warn("[XDLib/Updater] - An update is available! Current: {}, Latest: {}", currentVersion, latestVersion);
                } else {
                    Log.info("[XDLib/Updater] - No update available!");
                }
            }

            connection.disconnect();
        } catch (Exception e) {
            Log.error("[XDLib/Updater] - Failed to check for update: " + e.getMessage(), e);
        }
    }

    private static Version parseLatestVersion(String jsonResponse) {
        try {
            JsonArray versions = JsonParser.parseString(jsonResponse).getAsJsonArray();
            for (int i = 0; i < versions.size(); i++) {
                JsonObject versionInfo = versions.get(i).getAsJsonObject();
                boolean stable = versionInfo.get("version_type").getAsString().equals("release");
                if (stable) {
                    String versionNumber = versionInfo.get("version_number").getAsString();
                    return Version.parse(versionNumber);
                }
            }
        } catch (Exception e) {
            Log.error("Failed to parse latest version: " + e.getMessage(), e);
        }
        return null;
    }

    public static void main(String[] args) {
        checkForUpdate();
    }
}