package dev.xdpxi.xdlib;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.*;
import java.net.URL;
import javax.imageio.ImageIO;

public class javaWarning implements PreLaunchEntrypoint {
    private static final Logger LOGGER = LoggerFactory.getLogger("XDLib");

    @Override
    public void onPreLaunch() {
        warn();
    }

    public static void warn() {
        LOGGER.info("[XDLib] - Checking Java version...");

        FlatDarkLaf.setup();

        String javaVersion = System.getProperty("java.version");
        int majorVersion = getMajorJavaVersion(javaVersion);

        if (majorVersion < 21) {
            LOGGER.error("[XDLib] - Java version is below 21, showing warning...");

            try {
                URL iconUrl = new URL("https://raw.githubusercontent.com/XDPXI/XDLib/refs/heads/main/assets/r-icon.png");
                Image icon = ImageIO.read(iconUrl);
                
                JOptionPane optionPane = new JOptionPane(
                        "You are using Java " + majorVersion + ", please use Java 21 or above!",
                        JOptionPane.ERROR_MESSAGE
                );
                JDialog dialog = optionPane.createDialog("Java Version Error");
                dialog.setIconImage(icon);
                dialog.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,
                        "You are using Java " + majorVersion + ", please use Java 21 or above!",
                        "Java Version Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

            LOGGER.error("[XDLib] - Closing application...");
            throw new RuntimeException("XDLib: Incompatible Java version. Required: Java 21 or above, Found: Java " + majorVersion);
        } else {
            LOGGER.info("[XDLib] - Java version is 21 or above, continuing...");
        }
    }

    private static int getMajorJavaVersion(String version) {
        String[] parts = version.split("\\.");
        if (parts[0].equals("1")) {
            return Integer.parseInt(parts[1]);
        } else {
            return Integer.parseInt(parts[0]);
        }
    }
}