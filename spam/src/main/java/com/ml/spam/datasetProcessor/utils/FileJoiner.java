package com.ml.spam.datasetProcessor.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utilidad para combinar múltiples archivos de texto o CSV en un solo archivo.
 */
public class FileJoiner {

    public enum Format {
        CSV,
        TEXT
    }

    /**
     * Une los archivos indicados en un único archivo de salida.
     *
     * @param inputs  rutas de entrada
     * @param output  ruta del archivo combinado
     * @param format  formato de los archivos
     * @param shuffle si se deben mezclar las líneas antes de exportar
     */
    public static void joinFiles(List<Path> inputs, Path output, Format format, boolean shuffle) throws IOException {
        if (format == Format.CSV) {
            joinCsvFiles(inputs, output, shuffle);
        } else {
            joinTextFiles(inputs, output, shuffle);
        }
    }

    private static void joinCsvFiles(List<Path> inputs, Path output, boolean shuffle) throws IOException {
        List<String[]> allRows = new ArrayList<>();
        String header = null;
        int expectedColumns = -1;

        for (Path file : inputs) {
            if (!Files.exists(file)) {
                continue;
            }
            try (BufferedReader reader = Files.newBufferedReader(file)) {
                String firstLine = reader.readLine();
                if (firstLine == null) {
                    continue;
                }
                if (header == null) {
                    header = firstLine;
                    expectedColumns = header.split(",").length;
                } else {
                    if (firstLine.split(",").length != expectedColumns) {
                        throw new IllegalArgumentException("Encabezado incompatible en " + file);
                    }
                }
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] cols = line.split(",");
                    if (cols.length != expectedColumns) {
                        throw new IllegalArgumentException("Fila con columnas incorrectas en " + file + ": " + line);
                    }
                    allRows.add(cols);
                }
            }
        }

        if (shuffle) {
            Collections.shuffle(allRows);
        }

        Files.createDirectories(output.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            if (header != null) {
                writer.write(header);
                writer.newLine();
            }
            for (String[] row : allRows) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
        }
    }

    private static void joinTextFiles(List<Path> inputs, Path output, boolean shuffle) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Path file : inputs) {
            if (!Files.exists(file)) {
                continue;
            }
            lines.addAll(Files.readAllLines(file));
        }
        if (shuffle) {
            Collections.shuffle(lines);
        }
        Files.createDirectories(output.getParent());
        Files.write(output, lines, StandardCharsets.UTF_8);
    }
}