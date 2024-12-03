package dev.xdpxi.xdlib.api;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Utility class for performing common file operations such as creation, deletion, reading, and compression.
 */
public class Files {
    /**
     * Deletes the specified folder and its contents recursively.
     *
     * @param dir the path to the directory to be deleted.
     * @throws IOException if an I/O error occurs during the operation.
     */
    public static void deleteFolder(Path dir) throws IOException {
        if (java.nio.file.Files.notExists(dir)) return;
        java.nio.file.Files.walkFileTree(dir, new SimpleFileVisitor<>() {
            @Override
            public @NotNull FileVisitResult visitFile(Path file, @NotNull BasicFileAttributes attrs) throws IOException {
                java.nio.file.Files.deleteIfExists(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public @NotNull FileVisitResult postVisitDirectory(Path directory, IOException exc) throws IOException {
                java.nio.file.Files.deleteIfExists(directory);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Deletes a specified file.
     *
     * @param file the path to the file to be deleted.
     * @throws IOException if the file cannot be deleted.
     */
    public static void deleteFile(Path file) throws IOException {
        if (java.nio.file.Files.exists(file)) {
            try {
                java.nio.file.Files.delete(file);
            } catch (IOException e) {
                Logger.error("Error deleting file: " + e.getMessage());
                if (!file.toFile().delete()) {
                    throw new IOException("Failed to delete file: " + file);
                }
            }
        }
    }

    /**
     * Creates a directory, including any necessary but nonexistent parent directories.
     *
     * @param folder the path to the directory to be created.
     * @throws IOException if an I/O error occurs.
     */
    public static void createFolder(Path folder) throws IOException {
        java.nio.file.Files.createDirectories(folder);
    }

    /**
     * Creates a new empty file.
     *
     * @param file the path to the file to be created.
     * @throws IOException if an I/O error occurs.
     */
    public static void createFile(Path file) throws IOException {
        java.nio.file.Files.createFile(file);
    }

    /**
     * Writes the specified content to a file.
     *
     * @param file    the file to write to.
     * @param content the content to write.
     * @throws IOException if an I/O error occurs.
     */
    public static void writeToFile(File file, String content) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }

    /**
     * Reads the content of a file as a string.
     *
     * @param filePath the path to the file to be read.
     * @return the content of the file as a string.
     * @throws IOException if an I/O error occurs.
     */
    public static String readFile(String filePath) throws IOException {
        return java.nio.file.Files.readString(Paths.get(filePath));
    }

    /**
     * Compresses a file into a ZIP archive.
     *
     * @param filePath    the path to the file to be compressed.
     * @param zipFilePath the path to the output ZIP file.
     * @throws IOException if an I/O error occurs.
     */
    public static void compressFile(String filePath, String zipFilePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zipOut = new ZipOutputStream(fos);
             FileInputStream fis = new FileInputStream(filePath)) {

            ZipEntry zipEntry = new ZipEntry(Paths.get(filePath).getFileName().toString());
            zipOut.putNextEntry(zipEntry);

            byte[] buffer = new byte[4096];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                zipOut.write(buffer, 0, len);
            }
        }
    }

    /**
     * Extracts a ZIP file to the specified output directory.
     *
     * @param zipFilePath the path to the ZIP file to be extracted.
     * @param outputDir   the path to the output directory.
     * @throws IOException if an I/O error occurs.
     */
    public static void uncompressFile(String zipFilePath, String outputDir) throws IOException {
        Path outputPath = Paths.get(outputDir);

        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                Path resolvedPath = outputPath.resolve(entry.getName()).normalize();
                if (!resolvedPath.startsWith(outputPath)) {
                    throw new IOException("Invalid entry in ZIP: " + entry.getName());
                }
                if (entry.isDirectory()) {
                    java.nio.file.Files.createDirectories(resolvedPath);
                } else {
                    java.nio.file.Files.createDirectories(resolvedPath.getParent());
                    try (OutputStream fos = java.nio.file.Files.newOutputStream(resolvedPath)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = zipIn.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks whether a file exists at the specified path.
     *
     * @param filePath the path to check.
     * @return {@code true} if the file exists; {@code false} otherwise.
     */
    public static boolean exists(String filePath) {
        return java.nio.file.Files.exists(Paths.get(filePath));
    }
}