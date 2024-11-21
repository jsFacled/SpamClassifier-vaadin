package com.ml.spam.dictionary;

import java.io.InputStream;

/*
Lee el archivo JSON desde resources/static.
Inicializa el diccionario y exporta el estado a un nuevo archivo para verificar.
 */
public class DictionaryMain {
    public static void main(String[] args) {
        System.out.println("Inicializando el diccionario desde JSON...");

        SpamDictionary dictionary = SpamDictionary.getInstance();
        SpamDictionaryService service = new SpamDictionaryService(dictionary);

        // Leer archivo JSON desde resources/static
        try (InputStream inputStream = DictionaryMain.class.getClassLoader()
                .getResourceAsStream("static/spam_vocabulary_initialized.json")) {
            if (inputStream == null) {
                throw new RuntimeException("Archivo JSON no encontrado.");
            }
            service.initializeDictionary(inputStream);
            System.out.println("Diccionario inicializado correctamente.");

            // Exportar el diccionario a un nuevo archivo JSON
            service.exportToJson("spam_vocabulary_initialized_export.json");
            System.out.println("Diccionario exportado correctamente.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
