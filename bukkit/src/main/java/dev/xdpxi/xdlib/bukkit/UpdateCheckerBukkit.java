package dev.xdpxi.xdlib;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;

public class UpdateCheckerBukkit {
    private static final Gson gson = new Gson();
    private final MainBukkit plugin;
    private boolean updateAvailable = false;

    public UpdateCheckerBukkit(MainBukkit plugin) {
        this.plugin = plugin;
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public void checkForUpdate() {
        try {
            HttpURLConnection connection = (HttpURLConnection) URI.create("https://api.modrinth.com/v2/project/xdlib/version")
                    .toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                String latestVersion = parseLatestVersion(response.toString());
                String currentVersion = cleanVersion(plugin.getDescription().getVersion());

                if (latestVersion != null && isVersionLower(currentVersion, latestVersion)) {
                    plugin.getLogger().warning("[XDLib] - An update is available! Latest version: " + latestVersion);
                    updateAvailable = true;
                } else {
                    plugin.getLogger().info("[XDLib] - No update available.");
                    updateAvailable = false;
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("[XDLib] - Error checking for updates: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String parseLatestVersion(String jsonResponse) {
        JsonArray versions = JsonParser.parseString(jsonResponse).getAsJsonArray();
        if (versions.size() > 0) {
            JsonObject latestVersionInfo = versions.get(0).getAsJsonObject();
            return cleanVersion(latestVersionInfo.get("version_number").getAsString());
        }
        return null;
    }

    private String cleanVersion(String version) {
        return version.replaceAll("[^0-9.]", "");
    }

    private boolean isVersionLower(String currentVersion, String latestVersion) {
        String[] currentParts = currentVersion.split("\\.");
        String[] latestParts = latestVersion.split("\\.");

        for (int i = 0; i < Math.max(currentParts.length, latestParts.length); i++) {
            int currentPart = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;
            int latestPart = i < latestParts.length ? Integer.parseInt(latestParts[i]) : 0;

            if (currentPart < latestPart) {
                return true;
            } else if (currentPart > latestPart) {
                return false;
            }
        }
        return false;
    }
}