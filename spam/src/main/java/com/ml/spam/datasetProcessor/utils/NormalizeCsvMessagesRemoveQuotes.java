package com.ml.spam.datasetProcessor.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Normaliza mensajes desde un CSV con formato: "mensaje",spam o mensaje,ham
 * - Quita comillas externas si existen
 * - Guarda cada mensaje limpio en una sola línea
 * - No le quita el label
 * - Elimina duplicados exactos
 */
public class NormalizeCsvMessagesRemoveQuotes {

    public static void main(String[] args) {
        Path inputPath = Paths.get("spam/src/main/resources/static/datasets/joined/joined_messages_label.csv");
        Path outputPath = Paths.get("joined_messages_labels_normalized_unique.txt");

        Set<String> mensajesUnicos = new LinkedHashSet<>();

        try (BufferedReader reader = Files.newBufferedReader(inputPath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                int ultimaComa = line.lastIndexOf(',');
                if (ultimaComa > 0) {
                    String mensaje = line.substring(0, ultimaComa).trim();
                    String label = line.substring(ultimaComa + 1).trim();

                    if (mensaje.startsWith("\"") && mensaje.endsWith("\"") && mensaje.length() > 1) {
                        mensaje = mensaje.substring(1, mensaje.length() - 1).trim();
                    }

                    if (mensaje.startsWith("'") && mensaje.endsWith("'") && mensaje.length() > 1) {
                        mensaje = mensaje.substring(1, mensaje.length() - 1).trim();
                    }

                    mensajesUnicos.add(mensaje + "," + label);
                }
            }

            Files.write(outputPath, mensajesUnicos, StandardCharsets.UTF_8);
            System.out.println("✅ Archivo normalizado y sin duplicados: " + outputPath);
            System.out.println("Total líneas únicas: " + mensajesUnicos.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
