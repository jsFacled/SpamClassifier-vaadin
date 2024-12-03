package com.ml.spam.dictionary.service;

import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.models.WordCategory;
import com.ml.spam.dictionary.models.WordData;
import com.ml.spam.handlers.ResourcesHandler;

import com.ml.spam.utils.JsonUtils;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

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
            // 1. Limpiar el diccionario antes de inicializar
            dictionary.clearDictionary();

            // 2. Leer y procesar el JSON desde recursos
            JSONObject jsonObject = resourcesHandler.loadJson(resourcePath);

            // 3. Validar las claves del JSON
            validateJsonKeys(jsonObject);

            // 4. Inicializar las palabras en el diccionario
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
     * Valida que el JSON contenga las claves necesarias para las categorías esenciales
     * (spam_words, rare_symbols, stop_words). Si alguna de estas claves falta, lanza una excepción
     * para interrumpir el proceso de creación del diccionario. La categoría 'unassigned_words'
     * no se valida, ya que se llenará en una etapa posterior del proceso.
     */
    private void validateJsonKeys(JSONObject jsonObject) {
        // Lista de las categorías esenciales que debemos verificar
        String[] requiredCategories = {
                "spam_words",       // Clave para SPAM_WORDS
                "rare_symbols",     // Clave para RARE_SYMBOLS
                "stop_words"        // Clave para STOP_WORDS
        };

        boolean missingKey = false;

        // Iterar sobre las categorías requeridas
        for (String categoryKey : requiredCategories) {
            if (!jsonObject.has(categoryKey)) {
                System.err.println("Advertencia: La clave '" + categoryKey + "' no se encuentra en el JSON.");
                missingKey = true;
            }
        }

        // Si alguna clave faltante es detectada, lanzamos una excepción
        if (missingKey) {
            throw new RuntimeException("Error: Faltan claves necesarias en el JSON (spam_words, rare_symbols, stop_words).");
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

        } catch (IOException e) {
            throw new RuntimeException("Error al exportar a JSON: " + e.getMessage(), e);
        }
    }

    public void exportDictionaryToJson(String exportPath) {
        // Crear el JSON desde el diccionario
        JSONObject jsonObject = dictionary.toJson();

        // Solicitar a la fachada que guarde el JSON
        resourcesHandler.saveJson(jsonObject, exportPath);

        // Confirmar la exportación
        System.out.println("Diccionario exportado a: " + exportPath);
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
        System.out.println("========= Contenido del Diccionario =========\n");
        for (WordCategory category : WordCategory.values()) {
            System.out.println("Categoría: " + category);
            Map<String, WordData> wordsInCategory = dictionary.getCategory(category);

            if (wordsInCategory.isEmpty()) {
                System.out.println("  [Vacío]"); // Indicar que la categoría está vacía
            } else {
                wordsInCategory.forEach((word, wordData) ->
                        System.out.println("  " + word + " -> " + wordData)
                );
            }
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
