package com.ml.spam.datasetProcessor.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RemoveLabelsOnly {

    public static void main(String[] args) {
       Path inputPath = Paths.get("spam/src/main/resources/static/datasets/joined/normalized/joined_messages_labels_nornalized.txt");  // entrada
        Path outputPath = Paths.get("static/datasets/joined/normalized/joined_messages_labels_nornalized_NoLabel.txt");            // salida sin labels

       // Path inputPath = Paths.get("spam/src/main/resources/static/datasets/joined/normalized/joined_messages_triplecomillas_ham_normalized.txt");  // entrada
     //   Path outputPath = Paths.get("joined_messages_triplecomillas_ham_normalized_NoLabel.txt");            // salida sin labels

      //  Path inputPath = Paths.get("spam/src/main/resources/static/datasets/joined/normalized/joined_messages_triplecomillas_spam_normalized.txt");  // entrada
      //  Path outputPath = Paths.get("joined_messages_triplecomillas_spam_normalized_NoLabel.txt");            // salida sin labels

        try (BufferedReader reader = Files.newBufferedReader(inputPath);
             BufferedWriter writer = Files.newBufferedWriter(outputPath)) {

            String line;
            while ((line = reader.readLine()) != null) {
                int ultimaComa = line.lastIndexOf(',');
                if (ultimaComa > 0) {
                    String posibleLabel = line.substring(ultimaComa + 1).trim().toLowerCase();
                    if (posibleLabel.equals("spam") || posibleLabel.equals("ham")) {
                        String mensaje = line.substring(0, ultimaComa).trim();
                        writer.write(mensaje);
                        writer.newLine();
                    } else {
                        // si no es spam ni ham, se guarda la línea completa
                        writer.write(line);
                        writer.newLine();
                    }
                } else {
                    // línea sin coma → se guarda tal cual
                    writer.write(line);
                    writer.newLine();
                }
            }

            System.out.println("✅ Labels 'spam' y 'ham' eliminados. Archivo generado: " + outputPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
