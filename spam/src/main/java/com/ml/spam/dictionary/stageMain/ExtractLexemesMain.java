package com.ml.spam.dictionary.stageMain;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ExtractLexemesMain {

    private static final String INPUT_PATH = "F:\\JAVA GENERAL\\MACHINE LEARNING JAVA\\Código-ejemplos-intellij\\Clasificador Spam\\SpamClassifier-vaadin\\spam\\src\\main\\resources\\static\\dictionary\\lexemesRepository\\structured_lexemes_repository.json";
    private static final String OUTPUT_PATH = "lexemes_with_words.json";

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> fullMap;

        Map<String, Set<String>> result = new TreeMap<>();

        try {
            fullMap = mapper.readValue(new File(INPUT_PATH), Map.class);

            for (Object chartBlock : fullMap.values()) {
                Map<String, List<String>> lexemes = (Map<String, List<String>>) chartBlock;
                for (Map.Entry<String, List<String>> entry : lexemes.entrySet()) {
                    result.computeIfAbsent(entry.getKey(), k -> new TreeSet<>()).addAll(entry.getValue());
                }
            }

            // Convertir Set a List para serializar
            Map<String, List<String>> outputMap = new TreeMap<>();
            for (Map.Entry<String, Set<String>> entry : result.entrySet()) {
                outputMap.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }

            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(OUTPUT_PATH), outputMap);
            System.out.println("✅ Archivo generado en: " + OUTPUT_PATH);

        } catch (IOException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
}
