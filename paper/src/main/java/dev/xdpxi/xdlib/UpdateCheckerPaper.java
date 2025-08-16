package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;

public class UpdateCheckerPaper implements Runnable {
    public void checkForUpdate() {
        Log.info("[XDLib/UpdateChecker] - Checking for updates...");
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
                String version = Main.plugin.getDescription().getVersion();

                Log.info("[XDLib/UpdateChecker] - Latest Version: " + latestVersion);
                Log.info("[XDLib/UpdateChecker] - Current Version: " + version);

                if (latestVersion != null && UpdateChecker.compareVersions(version, latestVersion) < 0) {
                    Log.warn("[XDLib/UpdateChecker] - An update is available! Latest version: " + latestVersion);
                } else {
                    Log.info("[XDLib/UpdateChecker] - No update available.");
                }
            }
        } catch (Exception e) {
            Log.error("[XDLib/UpdateChecker] - Error checking for updates: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        checkForUpdate();
    }
}