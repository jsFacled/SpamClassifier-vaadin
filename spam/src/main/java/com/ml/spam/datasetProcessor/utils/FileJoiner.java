package com.ml.spam.datasetProcessor.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import com.ml.spam.utils.TripleQuoteUtils;

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
                    String content = Files.readString(path, StandardCharsets.UTF_8);

                    List<String> messages = TripleQuoteUtils.extractMessages(content);

                    if (messages.isEmpty()) {
                        for (String line : content.split("\\R")) {
                            String trimmed = line.trim();
                            if (!trimmed.isEmpty()) {
                                messages.add(trimmed);
                            }
                        }
                    }

                    for (String msg : messages) {
                        writer.write(msg.trim());
                        writer.newLine();
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