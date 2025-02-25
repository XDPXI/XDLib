package dev.xdpxi.xdlib;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.xdpxi.xdlib.util.Log;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class UpdateChecker {
    private static final ModContainer modContainer = FabricLoader.getInstance().getModContainer("xdlib").orElse(null);

    public static String textParser(String input) {
        return input.replaceAll("[-a-zA-Z]", "");
    }

    public static void checkForUpdate() {
        try {
            URL url = URI.create("https://api.modrinth.com/v2/project/xdlib/version").toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String latestVersion = parseLatestVersion(response.toString());
            assert latestVersion != null;
            latestVersion = textParser(latestVersion);
            String currentVersion = modContainer.getMetadata().getVersion().getFriendlyString();
            currentVersion = textParser(currentVersion);

            if (isVersionLower(currentVersion, latestVersion)) {
                Log.warn("[XDLib/Updater] - An update is available!");
            } else {
                Log.info("[XDLib/Updater] - No update available!");
            }
        } catch (Exception e) {
            Log.error("[XDLib/Updater] - Failed to check for update: " + e.getMessage());
        }
    }

    private static String parseLatestVersion(String jsonResponse) {
        JsonArray versions = JsonParser.parseString(jsonResponse).getAsJsonArray();
        for (int i = 0; i < versions.size(); i++) {
            JsonObject versionInfo = versions.get(i).getAsJsonObject();
            String versionNumber = versionInfo.get("version_number").getAsString();
            if (versionNumber.startsWith("3.")) {
                return versionNumber;
            }
        }
        return null;
    }

    private static boolean isVersionLower(String currentVersion, String latestVersion) {
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