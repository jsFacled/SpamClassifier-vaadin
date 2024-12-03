package com.ml.spam.dictionary.service;

import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.models.WordCategory;
import com.ml.spam.dictionary.models.WordData;
import com.ml.spam.handlers.ResourcesHandler;
import com.ml.spam.utils.JsonUtils;
import org.json.JSONObject;

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

    /**
     * Inicializa el diccionario cargando palabras desde un archivo JSON ubicado en resources.
     * @param resourcePath Ruta relativa del archivo JSON en los recursos.
     */
    public void initializeFromJson(String resourcePath) {
        try {
            // Leer JSON desde el handler
            JSONObject jsonObject = resourcesHandler.loadJson(resourcePath);

            // Validar la estructura y cargar palabras
            JsonUtils.validateJsonStructure(jsonObject);
            Map<WordCategory, List<String>> categoryMap = JsonUtils.jsonToCategoryMap(jsonObject);

            // Inicializar palabras en el diccionario
            dictionary.clearDictionary();
            categoryMap.forEach(dictionary::initializeWordsWithZeroFrequency);

            System.out.println("Diccionario inicializado desde JSON en: " + resourcePath);
        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar desde JSON: " + e.getMessage(), e);
        }
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
