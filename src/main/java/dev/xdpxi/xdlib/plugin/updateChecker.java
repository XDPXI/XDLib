package dev.xdpxi.xdlib.plugin;

import dev.xdpxi.xdlib.api.plugin.chatUtils;
import org.bukkit.ChatColor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class updateChecker {
    private final xdlib plugin;
    private static boolean update = false;

    public updateChecker(xdlib plugin) {
        this.plugin = plugin;
    }

    public void checkForUpdate() {
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
            String currentVersion = plugin.getDescription().getVersion() + "-bukkit";

            if (isVersionLower(currentVersion, latestVersion)) {
                plugin.getLogger().info("[XDLib] - An update is available!");
                chatUtils.sendMessageToOps(ChatColor.GOLD + "[XDLib] - An update is available!");
                update = true;
            } else {
                plugin.getLogger().info("[XDLib] - No update available!");
                update = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String parseLatestVersion(String jsonResponse) {
        JSONArray versions = new JSONArray(jsonResponse);
        if (versions.length() > 0) {
            JSONObject latestVersionInfo = versions.getJSONObject(0);
            return latestVersionInfo.getString("version_number");
        }
        return null;
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

    public static boolean isUpdate() {
        return update;
    }
}