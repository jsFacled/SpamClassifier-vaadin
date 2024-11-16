package com.ml.spam.dictionary;

import java.util.Arrays;
import java.util.List;

/**
 * Desde aquì se generará el diccionario principal
 *
 */

public class DictionaryMain {
    public static void main(String[] args) {
        System.out.println("Generando el diccionario de spam...");

        // Opción 1: Inicializar desde una lista de palabras
        List<String> initialWords = Arrays.asList(
                "compromiso", "urgencia", "dinero", "ganar", "gratis"
        );
        DictionaryInitializer.initializeFromList(initialWords);

        // Opción 2: Inicializar desde un archivo
        // String filePath = "spam_words.txt";
        // DictionaryInitializer.initializeFromFile(filePath);

        // Imprimir el contenido del diccionario para verificar
        SpamDictionary.getInstance().getWordSpam().forEach((word, data) -> {
            System.out.println(word + " -> " + data);
        });

        System.out.println("Diccionario generado exitosamente.");
    }
}
