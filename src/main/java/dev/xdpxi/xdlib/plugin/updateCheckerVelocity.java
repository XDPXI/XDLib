package dev.xdpxi.xdlib.plugin;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class updateCheckerVelocity {
    private final ProxyServer proxyServer;
    private final Plugin plugin;
    private final String currentVersion;

    @Inject
    private Logger logger;

    public updateCheckerVelocity(ProxyServer proxyServer, Plugin plugin, String currentVersion) {
        this.proxyServer = proxyServer;
        this.plugin = plugin;
        this.currentVersion = currentVersion;
    }

    public static String textParser(String input) {
        return input.replaceAll("[-a-zA-Z]", "");
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
            latestVersion = textParser(latestVersion);
            String formattedCurrentVersion = textParser(currentVersion);

            if (isVersionLower(formattedCurrentVersion, latestVersion)) {
                logger.info("[XDLib] - An update is available!");
            } else {
                logger.info("[XDLib] - No update available!");
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
}