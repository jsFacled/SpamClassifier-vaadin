package com.ml.spam.datasetProcessor;

import com.ml.spam.dictionary.SpamDictionary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CsvMessageProcessor {
    private final SpamDictionary dictionary;

    public CsvMessageProcessor(SpamDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public void processCsv(String csvPath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Parsear línea del CSV (suponiendo que es "mensaje,etiqueta")
                String[] parts = line.split(",", 2);
                if (parts.length < 2) continue;

                String message = parts[0];
                String label = parts[1];
                boolean isSpam = label.equalsIgnoreCase("spam");

                // Procesar el mensaje y actualizar el diccionario
                processMessage(message, isSpam);
            }
        }
    }

    private void processMessage(String message, boolean isSpam) {
        // Tokenizar el mensaje y actualizar el diccionario
        String[] words = message.split("\\s+");
        for (String word : words) {
            if (dictionary.contains(word)) {
                dictionary.updateFrequency(word, isSpam);
            } else {
                dictionary.addNewWord(word, isSpam);
            }
        }
    }
}
