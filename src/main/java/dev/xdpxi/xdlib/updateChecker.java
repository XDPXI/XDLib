package dev.xdpxi.xdlib;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static dev.xdpxi.xdlib.XDsLibrary.LOGGER;

public class updateChecker {
    private static final ModContainer modContainer = FabricLoader.getInstance().getModContainer("xdlib").orElse(null);
    private static String currentVersion = modContainer.getMetadata().getVersion().getFriendlyString() + "-fabric";
    private static boolean isUpdate = false;

    public static void checkForUpdate() {
        currentVersion = "0.0.0";

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://api.modrinth.com/v2/project/xdlib/version").openConnection();
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

            if (isVersionLower(currentVersion, latestVersion)) {
                LOGGER.warn("[XDLib] - An update is available!");
                isUpdate = true;
            } else {
                LOGGER.info("[XDLib] - No update available!");
                isUpdate = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String parseLatestVersion(String jsonResponse) {
        JsonArray versions = JsonParser.parseString(jsonResponse).getAsJsonArray();
        if (versions.size() > 0) {
            JsonObject latestVersionInfo = versions.get(0).getAsJsonObject();
            return latestVersionInfo.get("version_number").getAsString();
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

    public static boolean isUpdate() {
        return isUpdate;
    }
}