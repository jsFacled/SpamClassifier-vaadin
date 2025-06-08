package com.ml.spam.datasetProcessor.utils;
import java.io.*;
import java.nio.file.*;
import java.util.*;


//Recibe un csv con mensajes en cada fila y agrega el valor del label
public class TokenDatasetBuilder {

    public static void main(String[] args) {
        String inputFile = "ruta/al/archivo/tokens_spam.txt";
        String outputFile = "ruta/de/salida/tokenized_dataset_spam.csv";
        int fixedLength = 50;

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputFile));
             BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.trim().split("\\s+");
                List<String> padded = new ArrayList<>();

                for (int i = 0; i < fixedLength; i++) {
                    if (i < tokens.length) {
                        padded.add(tokens[i]);
                    } else {
                        padded.add("0.0");
                    }
                }

                // Agregar label spam = 1.0
                padded.add("1.0");

                // Escribir línea
                writer.write(String.join(",", padded));
                writer.newLine();
            }

            System.out.println("✅ Dataset generado: " + outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
