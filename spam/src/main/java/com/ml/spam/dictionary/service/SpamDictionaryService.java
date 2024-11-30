package com.ml.spam.dictionary.service;

import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.models.WordCategory;
import com.ml.spam.handlers.ResourcesHandler;

import com.ml.spam.utils.JsonUtils;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * SpamDictionaryService:
 * Servicio que interactúa con SpamDictionary para inicialización, manipulación, y exportación de datos.
 */
/**
 * SpamDictionaryService:
 * Servicio que interactúa con SpamDictionary para inicialización, manipulación y exportación de datos.
 */
public class SpamDictionaryService {

    private final SpamDictionary dictionary;
    private final ResourcesHandler resourcesHandler;

    public SpamDictionaryService(ResourcesHandler resourcesHandler, SpamDictionary dictionary) {
        this.resourcesHandler = resourcesHandler;
        this.dictionary = dictionary;
    }

    /**
     * Crea un diccionario desde un archivo JSON con palabras organizadas por categoría.
     * Cada palabra se inicializa con frecuencia en cero.
     *
     * @param resourcePath Ruta relativa del archivo JSON en los recursos.
     */
    public void createDictionaryFromWords(String resourcePath) {
        try {
            // 1. Limpiar el diccionario antes de inicializar
            dictionary.clearDictionary();

            // 2. Leer y procesar el JSON desde recursos
            JSONObject jsonObject = resourcesHandler.loadJson(resourcePath);

            // 3. Inicializar las palabras en el diccionario
            initializeCategoriesFromJson(jsonObject);

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
            // 1. Leer y procesar el JSON desde recursos
            JSONObject jsonObject = resourcesHandler.loadJson(resourcePath);

            // 2. Inicializar las palabras en el diccionario
            initializeCategoriesFromJson(jsonObject);

            System.out.println("Diccionario inicializado desde JSON en: " + resourcePath);
        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar desde JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Exporta el contenido del diccionario a un archivo JSON en el sistema.
     * @param filePath Ruta absoluta o relativa donde se guardará el JSON.
     */

    public void exportToJson(String filePath) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            // 1. Convertir el diccionario a JSON
            JSONObject jsonObject = dictionary.toJson();
            // 2. Escribir el JSON formateado en el archivo
            writer.write(jsonObject.toString(4));  // Formato de indentación
            System.out.println("Diccionario exportado a JSON en: " + filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error al exportar a JSON: " + e.getMessage(), e);
        }
    }

    /*
    public void exportToJson(String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            // 1. Convertir el diccionario a JSON
            JSONObject jsonObject = dictionary.toJson();

            // 2. Escribir el JSON formateado en el archivo
            fileWriter.write(jsonObject.toString(4));

            System.out.println("Diccionario exportado a JSON en: " + filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error al exportar a JSON: " + e.getMessage(), e);
        }
    }
    */



    /**
     * Inicializa las categorías del diccionario utilizando datos de un JSONObject.
     * @param jsonObject JSON que contiene las palabras por categoría.
     * 3 pasos:
     *         - Iterar sobre las categorías (WordCategory).
     *         - Procesar un JSONArray para convertirlo en una List<String>.
     *         - Inicializar palabras en el diccionario con frecuencia cero.
     *
     */
    private void initializeCategoriesFromJson(JSONObject jsonObject) {
        for (WordCategory category : WordCategory.values()) {
            initializeCategoryFromJson(jsonObject, category);
        }
    }

    // Métod auxiliar para inicializar una categoría específica
    private void initializeCategoryFromJson(JSONObject jsonObject, WordCategory category) {
        if (jsonObject.has(category.name().toLowerCase())) {
            List<String> words = JsonUtils.jsonArrayToStringList(
                    jsonObject.optJSONArray(category.name().toLowerCase())
            );
            dictionary.initializeWordsWithZeroFrequency(category, words);
        }
    }

    /**
     * Muestra en la consola el contenido actual del diccionario.
     */
    public void displayDictionary() {
        System.out.println("=== Contenido del Diccionario ===");
        for (WordCategory category : WordCategory.values()) {
            System.out.println("Categoría: " + category);
            dictionary.getCategory(category).forEach((word, wordData) ->
                    System.out.println(word + " -> " + wordData)
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
