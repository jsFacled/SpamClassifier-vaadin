package com.ml.spam.datasetProcessor.utils;

import java.io.*;
import java.nio.file.*;

/**
 * Normaliza mensajes desde un CSV con formato: "mensaje",spam o mensaje,ham
 * - Elimina el label (spam/ham)
 * - Quita comillas dobles externas si existen
 * - Guarda cada mensaje limpio en una línea
 */
public class NormalizeCsvMessagesRemoveLabelAndQuotes {

    public static void main(String[] args) {
        Path inputPath = Paths.get("spam/src/main/resources/static/datasets/joined/joined_messages_label.csv");
        Path outputPath = Paths.get("corpus_joined_sin_labels.txt");

        try (BufferedReader reader = Files.newBufferedReader(inputPath);
             BufferedWriter writer = Files.newBufferedWriter(outputPath)) {

            String line;
            while ((line = reader.readLine()) != null) {
                int ultimaComa = line.lastIndexOf(',');
                if (ultimaComa > 0) {
                    String mensaje = line.substring(0, ultimaComa).trim();

                    if (mensaje.startsWith("\"") && mensaje.endsWith("\"") && mensaje.length() > 1) {
                        mensaje = mensaje.substring(1, mensaje.length() - 1).trim();
                    }

                    writer.write(mensaje);
                    writer.newLine();
                }
            }

            System.out.println("✅ Archivo normalizado generado: " + outputPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
