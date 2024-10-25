package dev.xdpxi.xdlib;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class updateChecker {
    private static final HttpClient client = HttpClient.newHttpClient();
    private final Map<String, Map<String, String>> updates = new HashMap<>();
    private final String minecraftVersion;

    public updateChecker() {
        this.minecraftVersion = FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion().getFriendlyString();

        FabricLoader.getInstance().getAllMods().forEach(mod -> {
            String modId = mod.getMetadata().getId();
            String currentVersion = mod.getMetadata().getVersion().getFriendlyString();

            searchForModOnModrinth(modId, mod, currentVersion);
        });
    }

    private boolean isSameMinecraftVersion(JsonObject version) {
        JsonArray compatibleVersions = version.getAsJsonArray("game_versions");
        for (JsonElement compatibleVersion : compatibleVersions) {
            if (compatibleVersion.getAsString().equals(minecraftVersion)) {
                return true;
            }
        }
        return false;
    }

    private void searchForModOnModrinth(String modId, ModContainer mod, String currentVersion) {
        String searchUrl = "https://api.modrinth.com/v2/search?query=" + modId;

        HttpRequest request = HttpRequest.newBuilder(URI.create(searchUrl)).build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    JsonArray projects = JsonParser.parseString(response).getAsJsonObject().getAsJsonArray("hits");
                    for (JsonElement projectElement : projects) {
                        JsonObject project = projectElement.getAsJsonObject();
                        String slug = project.get("slug").getAsString();

                        checkModVersion(slug, mod, currentVersion);
                    }
                })
                .exceptionally(error -> null);
    }

    private String normalizeVersion(String version) {
        return version.split("-")[0];
    }

    private void checkModVersion(String slug, ModContainer mod, String currentVersion) {
        String versionUrl = "https://api.modrinth.com/v2/project/" + slug + "/version";
        HttpRequest request = HttpRequest.newBuilder(URI.create(versionUrl)).build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    JsonArray versions = JsonParser.parseString(response).getAsJsonArray();
                    boolean updateFound = false;

                    for (JsonElement versionElement : versions) {
                        JsonObject version = versionElement.getAsJsonObject();
                        String latestVersion = version.get("version_number").getAsString();
                        JsonArray files = version.getAsJsonArray("files");

                        if (isUpdateAvailable(currentVersion, latestVersion) && matchesCurrentFile(files, mod)) {
                            System.out.println("Mod " + mod.getMetadata().getId() + " has an update available! Current: " + currentVersion + " Latest: " + latestVersion);
                            addUpdate(mod.getMetadata().getId(), currentVersion, latestVersion);
                            updateFound = true;
                            break;
                        }
                    }

                    writeUpdatesToFile();
                })
                .exceptionally(error -> null);
    }

    private boolean matchesCurrentFile(JsonArray files, ModContainer mod) {
        String currentFilename = null;

        try {
            Path modPath = mod.getRoot();
            currentFilename = modPath.getFileName().toString();
            System.out.println("Root: " + currentFilename);
        } catch (Exception e) {
            currentFilename = mod.getMetadata().getId() + "-" + mod.getMetadata().getVersion().getFriendlyString() + ".jar"; // Fallback
            System.err.println("Failed to get root path for mod: " + mod.getMetadata().getId());
        }

        for (JsonElement fileElement : files) {
            JsonObject file = fileElement.getAsJsonObject();
            String filename = file.get("filename").getAsString();

            if (filename.equals(currentFilename)) {
                return true;
            }
        }
        return false;
    }

    private void writeUpdatesToFile() {
        if (updates.isEmpty()) {
            return;
        }

        Path path = FabricLoader.getInstance().getConfigDir().resolve("xdlib").resolve("updates.json");

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(updates, writer);
        } catch (IOException ignored) { }
    }

    private void addUpdate(String modId, String current, String latest) {
        Map<String, String> versionInfo = new HashMap<>();
        versionInfo.put("current", current);
        versionInfo.put("latest", latest);
        updates.put(modId, versionInfo);
    }

    private boolean isUpdateAvailable(String currentVersion, String latestVersion) {
        String normalizedCurrent = normalizeVersion(currentVersion);
        String normalizedLatest = normalizeVersion(latestVersion);

        return compareVersions(normalizedCurrent, normalizedLatest) < 0;
    }

    private int compareVersions(String v1, String v2) {
        String[] v1Parts = v1.split("\\.");
        String[] v2Parts = v2.split("\\.");

        int length = Math.max(v1Parts.length, v2Parts.length);

        for (int i = 0; i < length; i++) {
            int v1Part = i < v1Parts.length ? Integer.parseInt(v1Parts[i]) : 0;
            int v2Part = i < v2Parts.length ? Integer.parseInt(v2Parts[i]) : 0;

            if (v1Part < v2Part) return -1;
            if (v1Part > v2Part) return 1;
        }

        return 0;
    }
}