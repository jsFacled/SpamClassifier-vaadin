package com.ml.spam.dictionary;

import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.service.SpamDictionaryService;

/**
 * Clase principal para inicializar el diccionario desde un archivo JSON.
 *
 * Responsabilidades:
 * - Leer el archivo `initial_spam_vocabulary_base_only.json` desde el sistema de recursos.
 * - Utilizar `SpamDictionaryService` para inicializar las categorías del diccionario.
 * - Exportar el diccionario a un archivo JSON persistido.
 * - Mostrar el contenido del diccionario en la consola.
 */
public class BaseDictionaryBuilderMain {

    // Rutas para el JSON base y el archivo exportado
    private static final String INITIAL_JSON_PATH = "static/initial_spam_vocabulary_base_only.json";
    private static final String PERSISTED_JSON_PATH = "spam/src/main/resources/static/persisted_initialized_spam_vocabulary_frequenciesZero.json";

    public static void main(String[] args) {
        System.out.println("=== Construcción del Diccionario Base ===");

        // Instanciar el diccionario y servicio
        SpamDictionary dictionary = SpamDictionary.getInstance();
        SpamDictionaryService service = new SpamDictionaryService(dictionary);

        try {
            // Inicializar el diccionario desde el JSON base
            System.out.println("Inicializando el diccionario desde: " + INITIAL_JSON_PATH);
            service.initializeFromJson(INITIAL_JSON_PATH);

            // Mostrar el contenido del diccionario inicializado
            System.out.println("\n=== Contenido del Diccionario Inicializado ===");
            service.displayDictionary();

            // Exportar el diccionario inicializado a un archivo persistido
            System.out.println("\nExportando el diccionario inicializado a: " + PERSISTED_JSON_PATH);
            service.exportToJson(PERSISTED_JSON_PATH);

            System.out.println("\n=== Operación completada con éxito ===");

        } catch (Exception e) {
            System.err.println("Error durante la inicialización del diccionario: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
