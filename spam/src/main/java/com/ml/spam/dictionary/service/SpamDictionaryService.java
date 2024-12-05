package com.ml.spam.dictionary.service;

import com.ml.spam.datasetProcessor.MessageProcessor;
import com.ml.spam.datasetProcessor.models.ProcessedMessage;
import com.ml.spam.undefined.LabeledMessage;
import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.models.WordCategory;
import com.ml.spam.dictionary.models.WordData;
import com.ml.spam.handlers.ResourcesHandler;
import com.ml.spam.utils.JsonUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SpamDictionaryService {

    private final SpamDictionary dictionary;
    private final ResourcesHandler resourcesHandler;

    public SpamDictionaryService() {
        this.resourcesHandler = new ResourcesHandler();
        this.dictionary = SpamDictionary.getInstance();
    }

    /**
     * Crea un diccionario desde un archivo JSON con palabras organizadas por categoría.
     * Cada palabra se inicializa con frecuencia en cero.
     *
     * @param resourcePath Ruta relativa del archivo JSON en los recursos.
     */
    public void createDictionaryFromWordsInJson(String resourcePath) {
        try {
            // Leer JSON desde el handler
            JSONObject jsonObject = resourcesHandler.loadJson(resourcePath);

            // Validar la estructura del JSON
            JsonUtils.validateJsonStructure(jsonObject);

            // Transformar el JSON en un mapa
            Map<WordCategory, List<String>> categoryMap = JsonUtils.jsonToCategoryMap(jsonObject);

            // Inicializar el diccionario
            dictionary.clearDictionary();
            categoryMap.forEach(dictionary::initializeWordsWithZeroFrequency);

            System.out.println("Diccionario creado desde palabras en el archivo: " + resourcePath);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear el diccionario desde palabras: " + e.getMessage(), e);
        }
    }

    //Inicializa solamente si las frecuencias están en cero
    public void initializeDictionaryFromJsonIfContainOnlyZeroFrequencies(String filePath) {
        try {
            // Validar que las frecuencias en el JSON sean cero antes de inicializar
            JSONObject jsonObject = resourcesHandler.loadJson(filePath);
            JsonUtils.validateJsonFrequenciesZero(jsonObject);

            // Inicializar el diccionario desde el JSON
            initializeDictionaryFromJson(filePath);

            // Confirmar que las frecuencias en el diccionario son cero
            if (!dictionary.areFrequenciesZero()) {
                throw new IllegalStateException("El diccionario contiene frecuencias no inicializadas a cero.");
            }

            System.out.println("Diccionario inicializado correctamente con frecuencias en cero.");
        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar y validar el diccionario: " + e.getMessage(), e);
        }
    }


    public void initializeDictionaryFromJson(String filePath) {
        try {
            // Cargar el JSON desde el archivo
            JSONObject jsonObject = resourcesHandler.loadJson(filePath);

            // Validar la estructura del JSON
            JsonUtils.validateJsonStructure(jsonObject);

            // Iterar sobre las categorías y actualizar el diccionario
            for (WordCategory category : WordCategory.values()) {
                JSONObject categoryJson = jsonObject.optJSONObject(category.name().toLowerCase());
                if (categoryJson != null) {
                    for (String word : categoryJson.keySet()) {
                        JSONObject frequencies = categoryJson.getJSONObject(word);
                        int spamFrequency = frequencies.getInt("spamFrequency");
                        int hamFrequency = frequencies.getInt("hamFrequency");

                        // Agregar la palabra al diccionario
                        dictionary.addWordWithFrequencies(category, word, spamFrequency, hamFrequency);
                    }
                }
            }

            System.out.println("Diccionario inicializado correctamente desde el archivo JSON.");
        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar el diccionario: " + e.getMessage(), e);
        }
    }

    public void updateDictionary(String csvFilePath) throws IOException {
        // Obtener rawRows (filas crudas) del archivo CSV
        List<String[]> rawRows = resourcesHandler.loadCsvFile(csvFilePath);

        // Validar que rawRows no sea vacío
        if (rawRows == null || rawRows.isEmpty()) {
            throw new IllegalArgumentException("El archivo CSV no contiene datos válidos.");
        }

        // Procesar filas crudas a ProcessedMessage
        List<ProcessedMessage> processedMessages = MessageProcessor.simpleProcess(rawRows);

        // Aquí se agregará la lógica para actualizar el diccionario con los mensajes procesados


    }


    /**
     * Exporta el contenido del diccionario a un archivo JSON en el sistema.
     * @param filePath Ruta absoluta o relativa donde se guardará el JSON.
     */
    public void exportDictionaryToJson(String filePath) {
        try {
            // Obtener el diccionario categorizado
            Map<WordCategory, Map<String, WordData>> categorizedDictionary = dictionary.getAllCategories();

            // Convertir el diccionario a JSON usando JsonUtils
            JSONObject jsonObject = JsonUtils.toJson(categorizedDictionary);

            // Guardar el JSON utilizando el handler de recursos
            resourcesHandler.saveJson(jsonObject, filePath);

            System.out.println("Diccionario exportado a: " + filePath);
        } catch (Exception e) {
            throw new RuntimeException("Error al exportar el diccionario: " + e.getMessage(), e);
        }
    }

    /**
     * Muestra el contenido actual del diccionario en la consola.
     */
    public void displayDictionary() {
        System.out.println("========= Contenido del Diccionario =========\n");
        for (WordCategory category : WordCategory.values()) {
            System.out.println("Categoría: " + category);
            dictionary.getCategory(category).forEach((word, wordData) ->
                    System.out.println("  " + word + " -> " + wordData)
            );
        }
    }

    /**
     * Muestra el contenido de un archivo JSON desde resources en la consola.
     * @param resourcePath Ruta relativa del archivo JSON en los recursos.
     */
    public void displayJsonFileDictionary(String resourcePath) {
        try {
            // Leer el contenido del archivo desde recursos
            String jsonContent = resourcesHandler.loadResourceAsString(resourcePath);

            // Mostrar el contenido en la consola
            System.out.println("=== Diccionario Persistido en JSON ===");
            System.out.println(jsonContent);
        } catch (Exception e) {
            System.err.println("Error al leer el archivo JSON desde recursos: " + e.getMessage());
        }
    }
}
