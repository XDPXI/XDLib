package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.api.Logger;
import net.minecraftforge.fml.ModList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;

public class UpdateCheckerForge {
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

                String version = ModList.get().getModContainerById(Constants.MOD_ID)
                        .map(container -> container.getModInfo().getVersion().toString())
                        .orElse("Mod not found!");
                String latestVersion = UpdateChecker.parseLatestVersion(response.toString());

                if (latestVersion != null && UpdateChecker.compareVersions(version, latestVersion) < 0) {
                    Logger.warn("[XDLib] - An update is available! Latest version: " + latestVersion);
                } else {
                    Logger.info("[XDLib] - No update available. Current version: " + version);
                }
            }
        } catch (Exception e) {
            Logger.error("[XDLib] - Error checking for updates: " + e.getMessage());
            e.printStackTrace();
        }
    }
}