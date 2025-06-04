package com.ml.spam.dictionary.stageMain.lexemesMain;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ExtractLexemesWithDetailsMain {

    private static final String INPUT_PATH = "F:\\JAVA GENERAL\\MACHINE LEARNING JAVA\\Código-ejemplos-intellij\\Clasificador Spam\\SpamClassifier-vaadin\\spam\\src\\main\\resources\\static\\dictionary\\lexemesRepository\\structured_lexemes_repository.json";
    private static final String OUTPUT_PATH = "lexeme_words_detailed.json";

    // ** Para testear ** //
    //private static final String INPUT_PATH = "F:\\JAVA GENERAL\\MACHINE LEARNING JAVA\\Código-ejemplos-intellij\\Clasificador Spam\\SpamClassifier-vaadin\\spam\\src\\main\\resources\\static\\dictionary\\temporary\\structured_lexemes_repository_test.json";
    //private static final String OUTPUT_PATH = "F:\\JAVA GENERAL\\MACHINE LEARNING JAVA\\Código-ejemplos-intellij\\Clasificador Spam\\SpamClassifier-vaadin\\spam\\src\\main\\resources\\static\\dictionary\\temporary\\lexeme_words_detailed_test.json";

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> fullMap;

        Map<String, Set<String>> mergedLexemes = new TreeMap<>();

        try {
            fullMap = mapper.readValue(new File(INPUT_PATH), Map.class);

            // Paso 1: fusionar todos los bloques charSize en un único Map<String, Set<String>>
            for (Object chartBlock : fullMap.values()) {
                Map<String, List<String>> lexemeBlock = (Map<String, List<String>>) chartBlock;
                for (Map.Entry<String, List<String>> entry : lexemeBlock.entrySet()) {
                    mergedLexemes.computeIfAbsent(entry.getKey(), k -> new TreeSet<>()).addAll(entry.getValue());
                }
            }

            // Paso 2: convertir a Map<String, Map<String, Object>> con count y words
            Map<String, Map<String, Object>> detailedOutput = new TreeMap<>();

            for (Map.Entry<String, Set<String>> entry : mergedLexemes.entrySet()) {
                Map<String, Object> lexemeInfo = new LinkedHashMap<>();
                List<String> words = new ArrayList<>(entry.getValue());
                lexemeInfo.put("count", words.size());
                lexemeInfo.put("words", words);
                detailedOutput.put(entry.getKey(), lexemeInfo);
            }

            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(OUTPUT_PATH), detailedOutput);
            System.out.println("✅ Archivo generado correctamente: " + OUTPUT_PATH);

        } catch (IOException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
}
