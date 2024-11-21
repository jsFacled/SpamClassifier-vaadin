package com.ml.spam.dictionary;

import java.io.InputStream;

public class DictionaryMain {
    public static void main(String[] args) {
        System.out.println("Inicializando el diccionario...");

        SpamDictionary dictionary = SpamDictionary.getInstance();
        SpamDictionaryService service = new SpamDictionaryService(dictionary);

        // Leer archivo JSON
        try (InputStream inputStream = DictionaryMain.class.getClassLoader()
                .getResourceAsStream("static/initial_spam_vocabulary.json")) {

            if (inputStream == null) {
                throw new RuntimeException("Archivo JSON no encontrado.");
            }

            // Inicializar el diccionario
            service.initializeFromJson(inputStream);
            System.out.println("Diccionario inicializado correctamente.");

            // Imprimir contenido
            System.out.println("=== Palabras de Spam ===");
            dictionary.getOnlySpamWords().forEach((word, freq) ->
                    System.out.println(word + " -> " + freq));

            System.out.println("=== Símbolos Raros ===");
            dictionary.getOnlyRareSymbols().forEach((symbol, freq) ->
                    System.out.println(symbol + " -> " + freq));

            System.out.println("=== Stop Words ===");
            dictionary.getOnlyStopWords().forEach((stopWord, freq) ->
                    System.out.println(stopWord + " -> " + freq));

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
