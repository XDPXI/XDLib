package dev.xdpxi.xdlib.api;

import dev.xdpxi.xdlib.XDsLibraryClient;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class files {
    public static void deleteFolder(Path dir) throws IOException {
        Files.walkFileTree(dir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path directory, IOException exc) throws IOException {
                Files.delete(directory);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void deleteFile(Path file) throws IOException {
        try {
            if (Files.exists(file)) {
                try {
                    Files.delete(file);
                } catch (IOException e) {
                    XDsLibraryClient.LOGGER.error("ERROR DELETING FILE: {}", String.valueOf(e));
                }
            }
            if (Files.exists(file)) {
                file.toFile().delete();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void createFolder(Path folder) throws IOException {
        Files.createDirectories(folder);
    }

    public static void createFile(File file) throws IOException {
        file.createNewFile();
    }

    public static void writeToFile(File file, String content) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();
    }

    public static String readFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void compressFile(String filePath, String zipFilePath) {
        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zipOut = new ZipOutputStream(fos);
             FileInputStream fis = new FileInputStream(filePath)) {

            ZipEntry zipEntry = new ZipEntry(Paths.get(filePath).getFileName().toString());
            zipOut.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                zipOut.write(buffer, 0, len);
            }
            zipOut.closeEntry();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void uncompressFile(String zipFilePath, String outputDir) {
        String filePath;
        try (FileInputStream fis = new FileInputStream(zipFilePath);
             ZipInputStream zipIn = new ZipInputStream(fis)) {

            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                Path path = Paths.get(outputDir);
                Path normalizedPath = path.resolve(entry.getName()).normalize();
                if (!normalizedPath.startsWith(path)) {
                    throw new IOException("Bad zip entry: " + entry.getName());
                }
                filePath = normalizedPath.toString();
                if (!entry.isDirectory()) {
                    try (FileOutputStream fos = new FileOutputStream(filePath)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zipIn.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                } else {
                    Files.createDirectories(Paths.get(filePath));
                }
                zipIn.closeEntry();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean exists(String filePath) {
        Path path = Paths.get(filePath);
        return Files.exists(path);
    }
}