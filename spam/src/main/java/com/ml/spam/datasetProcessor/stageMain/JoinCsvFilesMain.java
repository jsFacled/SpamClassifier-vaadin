package com.ml.spam.datasetProcessor.stageMain;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Une múltiples archivos con formato mensaje,label directamente en memoria y genera un único archivo final sin duplicados.
 */
public class JoinCsvFilesMain {

    public static void main(String[] args) throws Exception {
        String[] defaults = {
                "spam/src/main/resources/static/datasets/normalized/ia_triplecomillas_ham_normalized.txt",
                "spam/src/main/resources/static/datasets/normalized/ia_triplecomillas_spam_normalized.txt",
                "spam/src/main/resources/static/datasets/normalized/labeled_ia_messages_normalized.csv",
                "spam/src/main/resources/static/datasets/normalized/labeled_test_messages_normalized.csv",
                "spam/src/main/resources/static/datasets/normalized/labeled_train_messages_normalized.csv",
                "spam/src/main/resources/static/datasets/normalized/original_triplecomillas_spam_normalized.txt"
        };

        List<String> inputPaths = new ArrayList<>();
        String outputPath;
        if (args.length >= 2) {
            for (int i = 0; i < args.length - 1; i++) {
                inputPaths.add(args[i]);
            }
            outputPath = args[args.length - 1];
        } else {
            Collections.addAll(inputPaths, defaults);
            outputPath = "spam/src/main/resources/static/datasets/joined/full_joined_normalized_noduplicates.csv";
        }

        // Leer todos los archivos y eliminar duplicados en memoria
        Set<String> uniqueLines = new LinkedHashSet<>();
        int totalOriginalLines = 0;

        for (String pathStr : inputPaths) {
            Path path = Paths.get(pathStr);
            if (Files.exists(path)) {
                try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        totalOriginalLines++;
                        String normalized = line.trim().replaceAll("\\s+", " ");
                        uniqueLines.add(normalized);
                    }
                }
            }
        }

        // Escribir archivo final sin duplicados
        Path finalOutput = Paths.get(outputPath);
        Files.createDirectories(finalOutput.getParent());

        // Mezclar todas las filas
        List<String> shuffledLines = new ArrayList<>(uniqueLines);
        Collections.shuffle(shuffledLines, new Random(42)); // Seed fijo para reproducibilidad

        try (BufferedWriter writer = Files.newBufferedWriter(finalOutput, StandardCharsets.UTF_8)) {
            for (String line : shuffledLines) {
                writer.write(line);
                writer.newLine();
            }
        }

        System.out.println("✅ Dataset final sin duplicados generado: " + outputPath);
        System.out.println("📊 Líneas originales: " + totalOriginalLines);
        System.out.println("📉 Líneas únicas: " + uniqueLines.size());
    }
}
