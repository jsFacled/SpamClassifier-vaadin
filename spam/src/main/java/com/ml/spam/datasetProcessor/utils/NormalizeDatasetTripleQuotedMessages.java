package com.ml.spam.datasetProcessor.utils;
import com.ml.spam.config.FilePathsConfig;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Elimina las triple comillas y coloca cada  mensaje entero en una fila
 */
public class NormalizeDatasetTripleQuotedMessages {

    public static void main(String[] args) {

        //Path inputPath = Paths.get("spam/src/main/resources/static/datasets/joined/joined_messages_triplecomillas_ham.txt");
        // Path outputPath = Paths.get("corpus_joined_ham_normalized.txt");


        Path inputPath = Paths.get("spam/src/main/resources/static/datasets/joined/joined_messages_triplecomillas_spam.txt");
        Path outputPath = Paths.get("corpus_joined_spam_normalized.txt");

        try {
            String content = Files.readString(inputPath);
            String[] bloques = content.split("\"\"\"");
            List<String> mensajes = new ArrayList<>();

            for (String bloque : bloques) {
                //String limpio = bloque.trim().replace("\n", " ");

                String limpio = bloque.replaceAll("\\s+", " ").trim();


                if (!limpio.isEmpty()) {
                    mensajes.add(limpio);
                }
            }

            Files.write(outputPath, mensajes);
            System.out.println("Corpus normalizado generado en: " + outputPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
