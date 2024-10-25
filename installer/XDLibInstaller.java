import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.border.AbstractBorder;

public class XDLibInstaller {
    private static final String FABRIC_API_BASE_URL = "https://cdn.modrinth.com/data/P7dR8mSH/versions/";
    private static final String QUILT_API_BASE_URL = "https://cdn.modrinth.com/data/qvIfYCYJ/versions/";

    private static final String CLOTH_CONFIG_BASE_URL = "https://cdn.modrinth.com/data/9s6osm5g/versions/";
    private static final String CLOTH_CONFIG_URL_2_FABRIC = "JbVSQUVw/cloth-config-16.0.141-fabric.jar";
    private static final String CLOTH_CONFIG_URL_2_NEO = "uYoucpky/cloth-config-16.0.141-neoforge.jar";
    private static final String CLOTH_CONFIG_URL_FABRIC = "HpMb5wGb/cloth-config-15.0.140-fabric.jar";
    private static final String CLOTH_CONFIG_URL_NEO = "izKINKFg/cloth-config-15.0.140-neoforge.jar";

    private static final String CONNECTER_121 = "https://cdn.modrinth.com/data/u58R1TMW/versions/PHaoQIFN/connector-2.0.0-beta.1%2B1.21-full.jar";
    private static final String CONNECTER_1211 = "https://cdn.modrinth.com/data/u58R1TMW/versions/x3AHjKpg/connector-2.0.0-beta.3%2B1.21.1-full.jar";

    private static final String FORGIFIED_121 = "https://cdn.modrinth.com/data/Aqlf1Shp/versions/K5REyk0w/forgified-fabric-api-0.101.2%2B2.0.10%2B1.21.jar";
    private static final String FORGIFIED_1211 = "https://cdn.modrinth.com/data/Aqlf1Shp/versions/2bxEYAKi/forgified-fabric-api-0.104.0%2B2.0.15%2B1.21.1.jar";

    private static final String CONNECTEREXTRAS_121 = "https://cdn.modrinth.com/data/FYpiwiBR/versions/WUktoh8L/ConnectorExtras-1.12.0%2B1.21.jar";
    private static final String CONNECTEREXTRAS_1211 = "https://cdn.modrinth.com/data/FYpiwiBR/versions/dgLCqZyo/ConnectorExtras-1.12.1%2B1.21.1.jar";

    private static final String FABRIC_QUILT_21_URL = "https://github.com/ZtrolixGit/ZtrolixLibs/raw/main/installer/versions/1.21/fabric.jar";
    private static final String NEO_21_URL = "https://github.com/ZtrolixGit/ZtrolixLibs/raw/main/installer/versions/1.21/neo.jar";
    private static final String BUKKIT_21_URL = "https://github.com/ZtrolixGit/ZtrolixLibs/raw/main/installer/versions/1.21/bukkit.jar";
    private static final String VELO_URL = "https://github.com/ZtrolixGit/ZtrolixLibs/raw/main/installer/versions/1.21/velo.jar";
    private static final String BUNGEE_URL = "https://github.com/ZtrolixGit/ZtrolixLibs/raw/main/installer/versions/1.21/bungee.jar";

    private static final String FABRIC_QUILT_URL = "https://github.com/ZtrolixGit/ZtrolixLibs/raw/main/installer/versions/1.20/fabric.jar";
    private static final String BUKKIT_URL = "https://github.com/ZtrolixGit/ZtrolixLibs/raw/main/installer/versions/1.20/bukkit.jar";
    private static final String NEOFORGE_URL = "https://github.com/ZtrolixGit/ZtrolixLibs/raw/main/installer/versions/1.20/neo.jar";
    private static final String FORGE_URL = "https://github.com/ZtrolixGit/ZtrolixLibs/raw/main/installer/versions/1.20/forge.jar";

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("XDLib Installer");
        frame.setSize(400, 250);
        frame.setUndecorated(true);
        frame.setShape(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 20, 20));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setCustomIcon(frame, "https://raw.githubusercontent.com/Ztrolix/ZtrolixLibs/main/assets/icon.png");

        addDraggableFeature(frame);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(60, 63, 65));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("XD's Library", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(new Color(230, 230, 230));
        titleLabel.setPreferredSize(new Dimension(frame.getWidth(), 40));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setOpaque(false);
        JButton minimizeButton = createOutlinedButton("_", new Color(255, 191, 0), 5);
        minimizeButton.addActionListener(e -> frame.setState(Frame.ICONIFIED));
        JButton closeButton = createOutlinedButton("X", new Color(255, 87, 51), 5);
        closeButton.addActionListener(e -> System.exit(0));
        controlPanel.add(minimizeButton);
        controlPanel.add(closeButton);
        topPanel.add(controlPanel, BorderLayout.EAST);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        String[] options = {"Select Loader", "Fabric", "Quilt", "Bukkit", "Velocity", "Bungeecord", "NeoForge", "Forge"};
        JComboBox<String> dropdown = new JComboBox<>(options);
        dropdown.setPreferredSize(new Dimension(370, 30));

        JPanel dropdownPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dropdownPanel.setOpaque(false);
        dropdownPanel.add(dropdown);
        centerPanel.add(dropdownPanel, BorderLayout.CENTER);

        JComboBox<String> versionDropdown = new JComboBox<>();
        versionDropdown.setPreferredSize(new Dimension(370, 30));

        JPanel versionDropdownPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        versionDropdownPanel.setOpaque(false);
        versionDropdownPanel.add(versionDropdown);
        centerPanel.add(versionDropdownPanel, BorderLayout.SOUTH);

        dropdown.addActionListener(e -> updateVersionDropdown((String) dropdown.getSelectedItem(), versionDropdown));
        dropdown.addActionListener(e -> updateLoaderDropdown(dropdown));

        JButton downloadButton = createOutlinedButton("Download and Install", new Color(158, 147, 211), 20);
        downloadButton.setPreferredSize(new Dimension(200, 30));

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);

        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedLoader = (String) dropdown.getSelectedItem();
                String selectedVersion = (String) versionDropdown.getSelectedItem();
                String downloadUrl = getDownloadUrl(selectedLoader, selectedVersion);
                String modUrl = getModUrl(selectedLoader, selectedVersion);
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(frame);

                if (result == JFileChooser.APPROVE_OPTION) {
                    Path downloadDirectory = fileChooser.getSelectedFile().toPath();
                    String folderName = selectedLoader.equals("Bukkit") ? "plugins" : "mods";
                    Path folderPath = downloadDirectory.resolve(folderName);

                    try {
                        Files.createDirectories(folderPath);
                        progressBar.setVisible(true);
                        new Thread(() -> {
                            try {
                                downloadFile(downloadUrl, folderPath, "XDLib-" + selectedLoader + "-" + selectedVersion + ".jar", progressBar);
                                if (modUrl != null) {
                                    downloadLibraryFile(modUrl, folderPath, selectedLoader + "-API" + ".jar", progressBar);
                                }
                                if (selectedLoader.equals("Fabric") || selectedLoader.equals("Quilt")) {
                                    if (selectedVersion.equals("1.21") || selectedVersion.equals("1.21.1")) {
                                        downloadFile(CLOTH_CONFIG_BASE_URL + CLOTH_CONFIG_URL_FABRIC, folderPath, "cloth-config-15.0.140-fabric.jar", progressBar);
                                    }
                                }
                                if (selectedLoader.equals("NeoForge")) {
                                    if (selectedVersion.equals("1.21")) {
                                        downloadFile(CLOTH_CONFIG_BASE_URL + CLOTH_CONFIG_URL_NEO, folderPath, "cloth-config-15.0.140-neoforge.jar", progressBar);
                                        downloadFile(CONNECTER_121, folderPath, "connector-2.0.0-beta.1+1.21-full.jar", progressBar);
                                        downloadFile(FORGIFIED_121, folderPath, "forgified-fabric-api-0.101.2+2.0.10+1.21.jar", progressBar);
                                        downloadFile(CONNECTEREXTRAS_121, folderPath, "ConnectorExtras-1.12.0+1.21.jar", progressBar);
                                    } else if (selectedVersion.equals("1.21.1")) {
                                        downloadFile(CLOTH_CONFIG_BASE_URL + CLOTH_CONFIG_URL_NEO, folderPath, "cloth-config-15.0.140-neoforge.jar", progressBar);
                                        downloadFile(CONNECTER_1211, folderPath, "connector-2.0.0-beta.3+1.21.1-full.jar", progressBar);
                                        downloadFile(FORGIFIED_1211, folderPath, "forgified-fabric-api-0.104.0+2.0.15+1.21.1.jar", progressBar);
                                        downloadFile(CONNECTEREXTRAS_1211, folderPath, "ConnectorExtras-1.12.1+1.21.1.jar", progressBar);
                                    }
                                }
                                JOptionPane.showMessageDialog(frame, "Download completed!");
                                progressBar.setVisible(false);
                                progressBar.setValue(0);
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(frame, "Download failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                                progressBar.setVisible(false);
                                progressBar.setValue(0);
                            }
                        }).start();
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "Failed to create directory: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        JPanel paddingPanel = new JPanel();
        paddingPanel.setPreferredSize(new Dimension(frame.getWidth(), 20));
        bottomPanel.add(paddingPanel, BorderLayout.NORTH);

        bottomPanel.add(centerPanel, BorderLayout.NORTH);
        bottomPanel.add(downloadButton, BorderLayout.CENTER);
        bottomPanel.add(progressBar, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void addDraggableFeature(JFrame frame) {
        final int[] dragStart = new int[2];

        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStart[0] = e.getX();
                dragStart[1] = e.getY();
            }
        });

        frame.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getXOnScreen() - dragStart[0];
                int y = e.getYOnScreen() - dragStart[1];
                frame.setLocation(x, y);
            }
        });
    }

    private static String getDownloadUrl(String loader, String version) {
        switch (loader) {
            case "Fabric":
            case "Quilt":
                if (version.equals("1.21") || version.equals("1.21.1")) {
                    return FABRIC_QUILT_21_URL;
                } else {
                    return FABRIC_QUILT_URL;
                }
            case "Velocity":
                return VELO_URL;
            case "Bungeecord":
                return BUNGEE_URL;
            case "Bukkit":
                if (!version.equals("1.19") || !version.equals("1.19.1") || !version.equals("1.19.2") || !version.equals("1.19.3") || !version.equals("1.19.4")) {
                    return BUKKIT_21_URL;
                } else {
                    return BUKKIT_URL;
                }
            case "NeoForge":
                if (!version.equals("1.20.4")) {
                    return NEO_21_URL;
                } else {
                    return NEOFORGE_URL;
                }
            case "Forge":
                return FORGE_URL;
            default:
                throw new IllegalArgumentException("Unknown loader: " + loader);
        }
    }

    private static String getModUrl(String loader, String version) {
        if (loader.equals("Quilt") || loader.equals("Fabric")) {
            return getModrinthUrl(loader, version);
        }
        return null;
    }

    private static String getModrinthUrl(String loader, String version) {
        switch (loader) {
            case "Fabric":
                switch (version) {
                    case "1.20":
                        return FABRIC_API_BASE_URL + "n2c5lxAo/fabric-api-0.83.0%2B1.20.jar";
                    case "1.20.1":
                        return FABRIC_API_BASE_URL + "P7uGFii0/fabric-api-0.92.2%2B1.20.1.jar";
                    case "1.20.2":
                        return FABRIC_API_BASE_URL + "8GVp7wDk/fabric-api-0.91.6%2B1.20.2.jar";
                    case "1.20.3":
                        return FABRIC_API_BASE_URL + "Yolngp3s/fabric-api-0.91.1%2B1.20.3.jar";
                    case "1.20.4":
                        return FABRIC_API_BASE_URL + "QVBohPm2/fabric-api-0.97.2%2B1.20.4.jar";
                    case "1.21":
                        return FABRIC_API_BASE_URL + "oGwyXeEI/fabric-api-0.102.0%2B1.21.jar";
                    case "1.21.1":
                        return FABRIC_API_BASE_URL + "iFnYBUfS/fabric-api-0.106.0%2B1.21.1.jar";
                    default:
                        return null;
                }
            case "Quilt":
                switch (version) {
                    case "1.20":
                        return QUILT_API_BASE_URL + "vTQynnGn/qfapi-7.2.2_qsl-6.1.2_fapi-0.88.1_mc-1.20.1.jar";
                    case "1.20.1":
                        return QUILT_API_BASE_URL + "zEhzQDsY/qfapi-7.6.0_qsl-6.2.0_fapi-0.92.2_mc-1.20.1.jar";
                    case "1.20.2":
                        return QUILT_API_BASE_URL + "zHVlrS0A/quilted-fabric-api-8.0.0-alpha.6%2B0.91.6-1.20.2.jar";
                    case "1.20.4":
                        return QUILT_API_BASE_URL + "AljqyvST/quilted-fabric-api-9.0.0-alpha.8%2B0.97.0-1.20.4.jar";
                    case "1.21":
                        return QUILT_API_BASE_URL + "WDWNp8qJ/quilted-fabric-api-11.0.0-alpha.3%2B0.102.0-1.21.jar";
                    default:
                        return null;
                }
            default:
                return null;
        }
    }

    private static void updateLoaderDropdown(JComboBox<String> dropdown) {
        String selectedLoader = (String) dropdown.getSelectedItem();
        System.out.println("Selected loader: " + selectedLoader);
    }

    private static void updateVersionDropdown(String selectedLoader, JComboBox<String> versionDropdown) {
        versionDropdown.removeAllItems();
        switch (selectedLoader) {
            case "Fabric":
                versionDropdown.addItem("1.21.1");
                versionDropdown.addItem("1.21");
                versionDropdown.addItem("1.20.4");
                versionDropdown.addItem("1.20.3");
                versionDropdown.addItem("1.20.2");
                versionDropdown.addItem("1.20.1");
                versionDropdown.addItem("1.20");
                break;
            case "Quilt":
                versionDropdown.addItem("1.21");
                versionDropdown.addItem("1.20.4");
                versionDropdown.addItem("1.20.2");
                versionDropdown.addItem("1.20.1");
                versionDropdown.addItem("1.20");
                break;
            case "Velocity":
                versionDropdown.addItem("1.21.3");
                versionDropdown.addItem("1.21.2");
                versionDropdown.addItem("1.21.1");
                versionDropdown.addItem("1.21");
                break;
            case "Bungeecord":
                versionDropdown.addItem("1.21.3");
                versionDropdown.addItem("1.21.2");
                versionDropdown.addItem("1.21.1");
                versionDropdown.addItem("1.21");
                break;
            case "Bukkit":
                versionDropdown.addItem("1.21.3");
                versionDropdown.addItem("1.21.2");
                versionDropdown.addItem("1.21.1");
                versionDropdown.addItem("1.21");
                versionDropdown.addItem("1.20.6");
                versionDropdown.addItem("1.20.5");
                versionDropdown.addItem("1.20.4");
                versionDropdown.addItem("1.20.3");
                versionDropdown.addItem("1.20.2");
                versionDropdown.addItem("1.20.1");
                versionDropdown.addItem("1.20");
                versionDropdown.addItem("1.19.4");
                versionDropdown.addItem("1.19.3");
                versionDropdown.addItem("1.19.2");
                versionDropdown.addItem("1.19.1");
                versionDropdown.addItem("1.19");
                break;
            case "NeoForge":
                versionDropdown.addItem("1.21.1");
                versionDropdown.addItem("1.21");
                versionDropdown.addItem("1.20.4");
                break;
            case "Forge":
                versionDropdown.addItem("1.20.1");
                break;
        }
    }

    private static void downloadFile(String fileURL, Path folderPath, String fileName, JProgressBar progressBar) throws IOException {
        URL url = new URL(fileURL);
        try (BufferedInputStream in = new BufferedInputStream(url.openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(folderPath.resolve(fileName).toString())) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            int totalBytesRead = 0;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                final int progress = (int) (((double) totalBytesRead / in.available()) * 100);
                SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
            }
        }
    }

    private static void downloadLibraryFile(String fileURL, Path folderPath, String fileName, JProgressBar progressBar) throws IOException {
        URL url = new URL(fileURL);
        try (BufferedInputStream in = new BufferedInputStream(url.openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(folderPath.resolve(fileName).toString())) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            int totalBytesRead = 0;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                final int progress = (int) (((double) totalBytesRead / in.available()) * 100);
                SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
            }
        }
    }

    private static JButton createRoundedButton(String text, Color outlineColor, int cornerRadius) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(70, 73, 75));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius); // Fill the button background
                g2d.setColor(outlineColor);
                g2d.setStroke(new BasicStroke(2)); // Outline width
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, cornerRadius, cornerRadius); // Draw rounded outline
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        return button;
    }

    private static JButton createOutlinedButton(String text, Color outlineColor, int cornerRadius) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(60, 63, 65));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
                g2d.setColor(outlineColor);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, cornerRadius, cornerRadius);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        return button;
    }

    private static void setCustomIcon(JFrame frame, String iconUrl) {
        try {
            URL url = new URL(iconUrl);
            BufferedImage icon = ImageIO.read(url);
            frame.setIconImage(icon);
        } catch (IOException e) {
            System.err.println("Failed to load icon: " + e.getMessage());
        }
    }

    static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        private final int thickness;

        public RoundedBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width - thickness, height - thickness, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = insets.top = insets.bottom = thickness;
            return insets;
        }
    }
}