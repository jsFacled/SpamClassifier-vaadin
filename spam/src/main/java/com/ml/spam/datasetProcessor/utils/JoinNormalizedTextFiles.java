package com.ml.spam.datasetProcessor.utils;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Une varios archivos de texto (uno por línea) en un único archivo combinado.
 * No espera encabezado ni validación de columnas.
 */
public class JoinNormalizedTextFiles {

    public static void main(String[] args) throws Exception {
        String basePath = "spam/src/main/resources/";

        String file1 = "static/datasets/joined/normalized/joined_messages_labels_nornalized_NoLabel.txt";
        String file2 = "static/datasets/joined/normalized/joined_messages_triplecomillas_ham_normalized_NoLabel.txt";
        String file3 = "static/datasets/joined/normalized/joined_messages_triplecomillas_spam_normalized_NoLabel.txt";

        String outputRelative = "static/mlDatasets/mix_combined_messages_fullNormalized.csv";
        String outputAbsolute = basePath + outputRelative;

        List<String> allLines = new ArrayList<>();
        for (String file : List.of(file1, file2, file3)) {
            Path path = Paths.get(basePath + file);
            if (!Files.exists(path)) {
                System.out.println("[WARNING] No se encontró el archivo: " + file);
                continue;
            }
            List<String> lines = Files.readAllLines(path);
            allLines.addAll(lines);
            System.out.println("[INFO] " + file + " → líneas: " + lines.size());
        }

        // Crear directorio si no existe
        Files.createDirectories(Paths.get(outputAbsolute).getParent());

        // Escribir archivo combinado
        Files.write(Paths.get(outputAbsolute), allLines, StandardCharsets.UTF_8);
        System.out.println("[INFO] Archivo combinado generado: " + outputAbsolute);
        System.out.println("[INFO] Total de líneas: " + allLines.size());
    }
}
