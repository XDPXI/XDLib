package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.api.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;

public class UpdateCheckerBukkit {
    private static String getModVersion() {
        Plugin plugin = JavaPlugin.getPlugin(Main.class);
        return plugin.getDescription().getVersion();
    }

    public static void checkForUpdate() {
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

                String latestVersion = UpdateChecker.parseLatestVersion(response.toString());
                String version = getModVersion();

                Logger.info("[XDLib] - Latest Version: " + latestVersion);
                Logger.info("[XDLib] - Current Version: " + version);

                if (latestVersion != null && UpdateChecker.compareVersions(version, latestVersion) < 0) {
                    Logger.warn("[XDLib] - An update is available! Latest version: " + latestVersion);
                } else {
                    Logger.info("[XDLib] - No update available.");
                }
            }
        } catch (Exception e) {
            Logger.error("[XDLib] - Error checking for updates: " + e.getMessage());
            e.printStackTrace();
        }
    }
}