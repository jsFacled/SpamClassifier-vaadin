package com.ml.spam.dictionary.stageMain;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * DetectRepeatedLexemesForWordsMain
 *
 * Este programa analiza el archivo structured_lexemes_repository.json
 * y detecta todas las palabras que están asignadas a más de un lexema.
 *
 * Genera como salida un archivo JSON (repeated_words_with_lexemes.json)
 * con el siguiente formato:
 *
 * {
 *   "palabra_repetida": ["lexema1", "lexema2", ...],
 *   ...
 * }
 *
 * Uso principal: auditar inconsistencias en la asignación de palabras
 * dentro del repositorio de lexemas, útil para mantenimiento o limpieza.
 *
 * INPUT_PATH: ruta absoluta del archivo JSON estructurado.
 * OUTPUT_PATH: nombre del archivo de salida con las repeticiones detectadas.
 */

public class DetectRepeatedLexemesForWordsMain {

    private static final String INPUT_PATH = "F:/JAVA GENERAL/MACHINE LEARNING JAVA/Código-ejemplos-intellij/Clasificador Spam/SpamClassifier-vaadin/spam/src/main/resources/static/dictionary/lexemesRepository/structured_lexemes_repository.json";
    private static final String OUTPUT_PATH = "repeated_words_with_lexemes.json";

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> fullMap;
        Map<String, Set<String>> wordToLexemes = new TreeMap<>();

        try {
            fullMap = mapper.readValue(new File(INPUT_PATH), Map.class);

            for (Object chartBlock : fullMap.values()) {
                Map<String, List<String>> lexemeBlock = (Map<String, List<String>>) chartBlock;
                for (Map.Entry<String, List<String>> entry : lexemeBlock.entrySet()) {
                    String lexeme = entry.getKey();
                    for (String word : entry.getValue()) {
                        wordToLexemes.computeIfAbsent(word, k -> new TreeSet<>()).add(lexeme);
                    }
                }
            }

            // Filtrar solo las palabras que están en más de un lexema
            Map<String, List<String>> repeatedWords = new TreeMap<>();
            for (Map.Entry<String, Set<String>> entry : wordToLexemes.entrySet()) {
                if (entry.getValue().size() > 1) {
                    repeatedWords.put(entry.getKey(), new ArrayList<>(entry.getValue()));
                }
            }

            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(OUTPUT_PATH), repeatedWords);
            System.out.println("✅ Archivo generado correctamente: " + OUTPUT_PATH);

        } catch (IOException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
}
