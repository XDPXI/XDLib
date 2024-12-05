package dev.xdpxi.xdlib;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.xdpxi.xdlib.api.Logger;

public class UpdateChecker {
    public static String parseLatestVersion(String jsonResponse) {
        JsonArray versions = JsonParser.parseString(jsonResponse).getAsJsonArray();
        if (!versions.isEmpty()) {
            JsonObject latestVersionInfo = versions.get(0).getAsJsonObject();
            return cleanVersion(latestVersionInfo.get("version_number").getAsString());
        }
        return null;
    }

    public static String cleanVersion(String version) {
        return version.replaceAll("[^0-9.]", "");
    }

    public static boolean isVersionLower(String currentVersion, String latestVersion) {
        if (currentVersion.isEmpty() || latestVersion.isEmpty()) {
            Logger.error("[XDLib] - Version strings cannot be empty.");
            return false;
        }

        String[] currentParts = currentVersion.split("\\.");
        String[] latestParts = latestVersion.split("\\.");

        for (int i = 0; i < Math.max(currentParts.length, latestParts.length); i++) {
            int currentPart = i < currentParts.length && !currentParts[i].isEmpty() ? Integer.parseInt(currentParts[i]) : 0;
            int latestPart = i < latestParts.length && !latestParts[i].isEmpty() ? Integer.parseInt(latestParts[i]) : 0;

            if (currentPart < latestPart) {
                return true;
            } else if (currentPart > latestPart) {
                return false;
            }
        }
        return false;
    }
}