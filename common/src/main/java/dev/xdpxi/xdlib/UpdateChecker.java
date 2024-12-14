package dev.xdpxi.xdlib;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.xdpxi.xdlib.api.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateChecker {
    private static final String[] PRE_RELEASE_ORDER = {"alpha", "beta", "rc"};

    public static String parseLatestVersion(String jsonResponse) {
        JsonArray versions = JsonParser.parseString(jsonResponse).getAsJsonArray();
        if (!versions.isEmpty()) {
            JsonObject latestVersionInfo = versions.get(0).getAsJsonObject();
            String version = latestVersionInfo.get("version_number").getAsString();
            return version.split("\\+")[0];
        }
        return null;
    }

    public static boolean isVersionLower(String currentVersion, String latestVersion) {
        if (currentVersion.isEmpty() || latestVersion.isEmpty()) {
            Logger.error("[XDLib] - Version strings cannot be empty.");
            return false;
        }

        return compareVersions(currentVersion, latestVersion) < 0;
    }

    public static int compareVersions(String v1, String v2) {
        Version parsedV1 = parseVersion(v1);
        Version parsedV2 = parseVersion(v2);

        int mainComparison = compareMainVersions(parsedV1.mainVersion, parsedV2.mainVersion);
        if (mainComparison != 0) {
            return mainComparison;
        }

        return comparePreReleases(parsedV1.preRelease, parsedV2.preRelease);
    }

    private static int compareMainVersions(String[] mainV1, String[] mainV2) {
        for (int i = 0; i < Math.max(mainV1.length, mainV2.length); i++) {
            int partV1 = i < mainV1.length ? Integer.parseInt(mainV1[i]) : 0;
            int partV2 = i < mainV2.length ? Integer.parseInt(mainV2[i]) : 0;

            if (partV1 != partV2) {
                return Integer.compare(partV1, partV2);
            }
        }
        return 0;
    }

    private static int comparePreReleases(String pre1, String pre2) {
        if (pre1 == null) return 1;
        if (pre2 == null) return -1;

        String[] parts1 = splitPreRelease(pre1);
        String[] parts2 = splitPreRelease(pre2);

        int labelComparison = Integer.compare(
                getPreReleaseRank(parts1[0]),
                getPreReleaseRank(parts2[0])
        );
        if (labelComparison != 0) {
            return labelComparison;
        }

        int num1 = parts1[1] != null ? Integer.parseInt(parts1[1]) : 0;
        int num2 = parts2[1] != null ? Integer.parseInt(parts2[1]) : 0;
        return Integer.compare(num1, num2);
    }

    private static int getPreReleaseRank(String label) {
        for (int i = 0; i < PRE_RELEASE_ORDER.length; i++) {
            if (PRE_RELEASE_ORDER[i].equalsIgnoreCase(label)) {
                return i;
            }
        }
        return PRE_RELEASE_ORDER.length;
    }

    private static String[] splitPreRelease(String pre) {
        Pattern pattern = Pattern.compile("([a-zA-Z]+)(\\d*)");
        Matcher matcher = pattern.matcher(pre);
        if (matcher.matches()) {
            String label = matcher.group(1).toLowerCase();
            String number = matcher.group(2);
            return new String[]{label, number};
        }
        return new String[]{pre, null};
    }

    private static Version parseVersion(String version) {
        String[] parts = version.split("-", 2);
        String[] mainVersion = parts[0].split("\\.");
        String preRelease = parts.length > 1 ? parts[1] : null;
        return new Version(mainVersion, preRelease);
    }

    private static class Version {
        String[] mainVersion;
        String preRelease;

        Version(String[] mainVersion, String preRelease) {
            this.mainVersion = mainVersion;
            this.preRelease = preRelease;
        }
    }
}