package com.ml.spam.dictionary.stageMain;
import com.ml.spam.config.FilePathsConfig;
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
    private static final String INITIAL_JSON_PATH = FilePathsConfig.INITIAL_JSON_PATH;
    private static final String EXPORT_DICTIONARY_CREATED_JSON_PATH = FilePathsConfig.EXPORT_DICTIONARY_CREATED_JSON_PATH;

    public static void main(String[] args) {
        SpamDictionaryService service = new SpamDictionaryService();

        System.out.println("=== Construcción del Diccionario Base ===");

        // Paso 1: Crear el diccionario desde el JSON base
        service.createDictionaryFromWordsInJson(INITIAL_JSON_PATH);

        // Paso 2: Mostrar el diccionario en memoria
        service.displayDictionary();

        // Paso 3: Exportar el diccionario a un archivo persistido
        service.exportDictionaryToJson(EXPORT_DICTIONARY_CREATED_JSON_PATH);

        // Paso 4: Mostrar el contenido del archivo JSON persistido
        service.displayJsonFileDictionary(EXPORT_DICTIONARY_CREATED_JSON_PATH);
        /*
        // Crear el servicio de diccionario
        SpamDictionaryService service = new SpamDictionaryService();

        // Paso 1: Crear el diccionario desde el JSON base
        DictionaryUtils.createDictionary(service, INITIAL_JSON_PATH);

        // Paso 2: Mostrar el diccionario en memoria
        DictionaryUtils.displayDictionary(service);

        // Paso 3: Exportar el diccionario a un archivo persistido
        DictionaryUtils.exportDictionary(service, EXPORT_DICTIONARY_CREATED_JSON_PATH);

        // Paso 4: Mostrar el contenido del archivo JSON persistido (opcional)
        DictionaryUtils.displayPersistedDictionary(EXPORT_DICTIONARY_CREATED_JSON_PATH);
*/
    }
}
