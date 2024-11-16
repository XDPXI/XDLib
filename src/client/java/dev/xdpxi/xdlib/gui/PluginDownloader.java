package dev.xdpxi.xdlib.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import net.fabricmc.loader.api.FabricLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.net.URI;
import java.nio.file.Path;

public class PluginDownloader {
    private static final JDialog frame = new JDialog();
    private static final JProgressBar progressBar;
    private static final Path configDir = FabricLoader.getInstance().getConfigDir().resolve("xdlib");
    private static final Path pluginsDir = configDir.resolve("plugins");
    private static final Path pluginsTemp = pluginsDir.resolve("plugins.tmp");
    private static boolean disposed = false;
    private static String fileContent = "";

    static {
        try {
            FlatDarkLaf.setup();
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf.");
        }

        frame.setUndecorated(true);
        frame.setResizable(false);
        frame.setSize(400, 50);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setShape(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 20, 20));
        frame.setBackground(new Color(0, 0, 0, 0));

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(60, 63, 65));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setOpaque(false);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("Downloading Plugins...");
        progressBar.setBackground(new Color(60, 63, 65));
        progressBar.setForeground(new Color(3, 169, 244));
        mainPanel.add(progressBar, BorderLayout.CENTER);

        frame.add(mainPanel, BorderLayout.CENTER);
    }

    public static void display(String url) {
        fileContent = url;
        if (disposed) throw new IllegalStateException("Pre-launch window has been disposed!");
        frame.setVisible(true);
        startDownload();
    }

    private static void startDownload() {
        SwingWorker<Void, Integer> downloader = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try (BufferedReader reader = new BufferedReader(new FileReader(String.valueOf(pluginsTemp)))) {
                    String fileURL;
                    while ((fileURL = reader.readLine()) != null) {
                        fileURL = fileContent;
                        String savePath = "plugin.zip";
                        try (BufferedInputStream in = new BufferedInputStream(URI.create(fileURL).toURL().openStream());
                             FileOutputStream fileOutputStream = new FileOutputStream(savePath)) {

                            byte[] dataBuffer = new byte[1024];
                            int bytesRead;
                            int totalBytesRead = 0;
                            int fileSize = 1024 * 1024 * 10;

                            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                                totalBytesRead += bytesRead;
                                int progress = (int) (((double) totalBytesRead / fileSize) * 100);
                                publish(progress);
                                fileOutputStream.write(dataBuffer, 0, bytesRead);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                int progress = chunks.get(chunks.size() - 1);
                progressBar.setValue(progress);
                progressBar.setString("Downloading Plugins... " + progress + "%");
            }

            @Override
            protected void done() {
                progressBar.setString("Download Complete!");
            }
        };
        downloader.execute();
    }

    public static void remove() {
        if (disposed) return;
        frame.setVisible(false);
        frame.dispose();
        disposed = true;
    }

    public static void main(String[] args) {
        display("");
    }
}