package com.ml.spam.datasetProcessor.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class FileJoiner {

    public void joinCsvFiles(List<String> inputPaths, String outputFilePath) throws IOException {
        Path outputPath = Paths.get(outputFilePath);
        Path parent = outputPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            for (String pathStr : inputPaths) {
                Path path = Paths.get(pathStr);
                if (Files.exists(path)) {
                    List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
                    for (String line : lines) {
                        if (!line.trim().isEmpty()) {
                            writer.write(line);
                            writer.newLine();
                        }
                    }
                }
            }
        }
    }

    public void joinTextFiles(List<String> inputPaths, String outputFilePath) throws IOException {
        Path outputPath = Paths.get(outputFilePath);
        Path parent = outputPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            for (String pathStr : inputPaths) {
                Path path = Paths.get(pathStr);
                if (Files.exists(path)) {
                    List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
                    StringBuilder currentMessage = new StringBuilder();
                    boolean insideMessage = false;

                    for (String line : lines) {
                        line = line.trim();
                        if (line.startsWith("\"\"\"")) {
                            insideMessage = true;
                            currentMessage = new StringBuilder();
                            currentMessage.append(line);
                        } else if (line.endsWith("\"\"\"") && insideMessage) {
                            currentMessage.append(" ").append(line);
                            writer.write(currentMessage.toString().trim());
                            writer.newLine();
                            insideMessage = false;
                        } else if (insideMessage) {
                            currentMessage.append(" ").append(line);
                        }
                    }
                }
            }
        }
    }

    public void joinFiles(List<String> inputPaths, String outputFilePath, boolean isCsv) throws IOException {
        if (isCsv) {
            joinCsvFiles(inputPaths, outputFilePath);
        } else {
            joinTextFiles(inputPaths, outputFilePath);
        }
    }
}
