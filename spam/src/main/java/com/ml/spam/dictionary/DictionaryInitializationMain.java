package com.ml.spam.dictionary;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/*
 * Clase principal para inicializar el diccionario desde un archivo JSON.
 *
 * Responsabilidades:
 * - Leer el archivo `initial_spam_vocabulary.json` desde el sistema de recursos.
 * - Utilizar `SpamDictionaryService` para inicializar las categorías del diccionario.
 * - Mostrar el contenido del diccionario en la consola después de la inicialización.
 *
 * Esta clase actúa como punto de entrada exclusivo para la etapa de inicialización.
 *
 * Luego persiste la inicialización creando otro archivo: persisted_initialized_spam_vocabulary.json.json
 */

public class DictionaryInitializationMain {
    public static void main(String[] args) throws IOException {
        String initialPath = "static/initial_spam_vocabulary.json";
        String persistedInitializedPath = "spam/src/main/resources/static/persisted_initialized_spam_vocabulary.json";


        System.out.println("* === * === 0000 Inicializando el diccionario desde JSON... * === * === 000");

        // Instanciar el servicio y el diccionario
        SpamDictionary dictionary = SpamDictionary.getInstance();
        SpamDictionaryService service = new SpamDictionaryService(dictionary);

        //Inicialización en los Map
        try (InputStream inputStream = DictionaryInitializationMain.class.getClassLoader()
                .getResourceAsStream(initialPath)) {

            if (inputStream == null) {
                throw new RuntimeException("Archivo JSON no encontrado.");
            }
            System.out.println("Archivo JSON preparado para ser inicializado");
            // Inicializar el diccionario
            service.initializeFromJson(inputStream);
            System.out.println("Diccionario inicializado correctamente.");

            // Mostrar el contenido del diccionario
            service.displayDictionary();

        } catch (Exception e) {
            System.err.println("Error durante la inicialización: " + e.getMessage());
        }

        //Persistencia en un json
        service.exportToJson(persistedInitializedPath);
        System.out.println(" * === * ===> Diccionario exportado correctamente a spam_vocabulary_initialized_persisted.json");





        // Mostrar el diccionario persistido
        service.displayJsonPersistedDictionary(persistedInitializedPath);

    }
}
