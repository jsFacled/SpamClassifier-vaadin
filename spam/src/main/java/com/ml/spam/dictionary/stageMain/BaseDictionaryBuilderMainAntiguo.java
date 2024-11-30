package com.ml.spam.dictionary.stageMain;

/*
 * Clase principal para inicializar el diccionario desde un archivo JSON.
 *
 * Responsabilidades:
 * - Leer el archivo `initial_spam_vocabulary_base_only.json` desde el sistema de recursos.
 * - Utilizar `SpamDictionaryService` para inicializar las categorías del diccionario.
 * - Mostrar el contenido del diccionario en la consola después de la inicialización.
 *
 * Esta clase actúa como punto de entrada exclusivo para la etapa de inicialización.
 *
 * Luego persiste la inicialización creando otro archivo: persisted_initialized_spam_vocabulary_frequenciesZero.json.json
 */

public class BaseDictionaryBuilderMainAntiguo {

    // Ruta del archivo base y del archivo a persistir
    private static final String INITIAL_JSON_PATH = "static/initial_spam_vocabulary_base_only.json";
    private static final String PERSISTED_JSON_PATH = "static/persisted_initialized_spam_vocabulary_frequenciesZero.json";
   // private static final String PERSISTED_JSON_PATH = "spam/src/main/resources/static/persisted_initialized_spam_vocabulary_frequenciesZero.json";

    public static void main(String[] args) {

     /*
        System.out.println("=== Construcción del Diccionario Base ===");

        // Instanciar el diccionario y servicio
        SpamDictionary dictionary = SpamDictionary.getInstance();
        SpamDictionaryService service = new SpamDictionaryService(dictionary);

        // Inicializar el diccionario desde el JSON base
        System.out.println("Inicializando el diccionario desde: " + INITIAL_JSON_PATH);
        service.initializeFromJson(INITIAL_JSON_PATH);

        // Exportar el diccionario inicializado a un archivo persistido
        System.out.println("Exportando el diccionario inicializado a: " + PERSISTED_JSON_PATH);
        service.exportToJson(PERSISTED_JSON_PATH);

        System.out.println("=== Operación completada con éxito ===");



        // Mostrar el diccionario persistido
        service.displayJsonPersistedDictionary(PERSISTED_JSON_PATH);


      */
    }

}
