package com.ml.spam.dictionary.stageMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.service.SpamDictionaryService;

/**
 * Clase principal para inicializar el diccionario desde un archivo JSON.
 *
 * Responsabilidades:
 * - Leer el archivo `categorized_words_vocabulary_base_only.json` desde el sistema de recursos.
 * - Utilizar `SpamDictionaryService` para transformar palabras categorizadas en WordData con frecuencias en cero.
 * - Exportar el diccionario a un archivo JSON persistido.
 * - Mostrar el contenido del diccionario en la consola.
 */
public class BaseDictionaryBuilderMain {

    // Rutas para el JSON base y el archivo exportado
    private static final String INITIAL_JSON_PATH = FilePathsConfig.CATEGORIZED_WORDS_BASE_JSON_PATH;
    private static final String EXPORT_JSON_PATH = FilePathsConfig.CATEGORIZED_WORDS_FREQUENCIES_ZERO_JSON_PATH;

    public static void main(String[] args) {
        // Inicia temporizador para calcular el tiempo de procesamiento total
        long startUpdate = System.nanoTime();

        SpamDictionaryService service = new SpamDictionaryService();

        System.out.println("=== Etapa 1 - Iniciando la Construcción de la Estructura del Diccionario Base ===\n");

        // Paso 1: Transformar palabras categorizadas en WordData con frecuencias en cero
        service.transformBaseWordsToFrequenciesZero(INITIAL_JSON_PATH, EXPORT_JSON_PATH);

        // Paso 2: Mostrar el diccionario en memoria
        service.displayCategorizedWordsInDictionary();

        // Paso 3: Mostrar el contenido del archivo JSON exportado
        service.displayJsonFileDictionary(EXPORT_JSON_PATH);

        System.out.println("=== Proceso de Construcción del Diccionario Base Finalizado ===");
        System.out.println(" * Se ha persistido categorizedWords y se a rellenado el Spamdictionary ===");
        long endUpdate = System.nanoTime();
        System.out.printf("Tiempo de creación del diccionario: %.2f ms%n", (endUpdate - startUpdate) / 1_000_000.0);

    }
}
