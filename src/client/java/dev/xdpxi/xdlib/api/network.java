package dev.xdpxi.xdlib.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class network {
    public static final Logger LOGGER = LoggerFactory.getLogger("xdlib");

    public static String getRequest(String URL) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            int responseCode = connection.getResponseCode();
            LOGGER.info("[XDLib] - Response Code: {}", responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    String inputLine;
                    StringBuilder content = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    LOGGER.info("[XDLib] - Response Content: {}", content);
                    return content.toString();
                }
            } else {
                LOGGER.warn("[XDLib] - GET request not worked, Response Code: {}", responseCode);
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("[XDLib] - {}", String.valueOf(e));
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public void postRequest(String URL, String jsonInputString) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            int responseCode = connection.getResponseCode();
            LOGGER.info("[XDLib] - Response Code: {}", responseCode);
        } catch (Exception e) {
            LOGGER.error("[XDLib] - {}", String.valueOf(e));
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}