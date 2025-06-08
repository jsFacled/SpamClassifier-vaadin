package com.ml.spam.datasetProcessor.utils;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Elimina las triple comillas, coloca cada mensaje en una sola línea,
 * le agrega la etiqueta correspondiente (",spam" o ",ham")
 * y elimina los mensajes duplicados.
 */
public class NormalizeDatasetTripleQuotedMessages {

    public static void main(String[] args) {

        // ----- HABILITÁ UNO SOLO SEGÚN NECESITES -----

         String inputPath = "spam/src/main/resources/static/datasets/joined/joined_messages_triplecomillas_ham.txt";
         String label = "ham";
         String outputPath = "joined_messages_triplecomillas_ham_normalized.txt";

      ///  String inputPath = "spam/src/main/resources/static/datasets/joined/joined_messages_triplecomillas_spam.txt";
       /// String label = "spam";
      ///  String outputPath = "joined_messages_triplecomillas_spam_normalized.txt";

        try {
            String content = Files.readString(Paths.get(inputPath));
            String[] bloques = content.split("\"\"\"");

            // Usar Set para evitar duplicados y mantener orden
            Set<String> mensajes = new LinkedHashSet<>();

            for (String bloque : bloques) {
                String limpio = bloque.replaceAll("\\s+", " ").trim();
                if (!limpio.isEmpty()) {
                    mensajes.add(limpio + "," + label);
                }
            }

            Files.write(Paths.get(outputPath), mensajes);
            System.out.println("Corpus normalizado generado en: " + outputPath);
            System.out.println("Total mensajes únicos: " + mensajes.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
