package com.ml.spam.dictionary;

import java.io.InputStream;

/*
 * Clase principal para inicializar el diccionario desde un archivo JSON.
 *
 * Responsabilidades:
 * - Leer el archivo `initial_spam_vocabulary.json` desde el sistema de recursos.
 * - Utilizar `SpamDictionaryService` para inicializar las categorías del diccionario.
 * - Mostrar el contenido del diccionario en la consola después de la inicialización.
 *
 * Esta clase actúa como punto de entrada exclusivo para la etapa de inicialización.
 */

public class DictionaryInitializationMain {
    public static void main(String[] args) {
        System.out.println("Inicializando el diccionario desde JSON...");

        // Instanciar el servicio y el diccionario
        SpamDictionary dictionary = SpamDictionary.getInstance();
        SpamDictionaryService service = new SpamDictionaryService(dictionary);

        try (InputStream inputStream = DictionaryInitializationMain.class.getClassLoader()
                .getResourceAsStream("static/initial_spam_vocabulary.json")) {

            if (inputStream == null) {
                throw new RuntimeException("Archivo JSON no encontrado.");
            }

            // Inicializar el diccionario
            service.initializeFromJson(inputStream);
            System.out.println("Diccionario inicializado correctamente.");

            // Mostrar el contenido del diccionario
            service.displayDictionary();

        } catch (Exception e) {
            System.err.println("Error durante la inicialización: " + e.getMessage());
        }
    }
}
