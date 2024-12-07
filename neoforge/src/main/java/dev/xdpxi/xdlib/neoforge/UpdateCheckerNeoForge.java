package dev.xdpxi.xdlib.neoforge;

import dev.xdpxi.xdlib.Main;
import dev.xdpxi.xdlib.UpdateChecker;
import dev.xdpxi.xdlib.api.Logger;
import net.neoforged.fml.ModList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;

public class UpdateCheckerNeoForge {
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

                String version = ModList.get().getModContainerById(Main.MOD_ID)
                        .map(container -> container.getModInfo().getVersion().toString())
                        .orElse("Mod not found!");
                String latestVersion = UpdateChecker.parseLatestVersion(response.toString());
                String currentVersion = UpdateChecker.cleanVersion(version);

                if (latestVersion != null && UpdateChecker.isVersionLower(currentVersion, latestVersion)) {
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