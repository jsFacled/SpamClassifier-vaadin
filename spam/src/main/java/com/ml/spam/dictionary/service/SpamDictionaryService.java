package com.ml.spam.dictionary.service;

import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.models.WordData;
import com.ml.spam.dictionary.models.WordCategory;
import com.ml.spam.utils.FileLoader;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * SpamDictionaryService:
 * Servicio que interactúa con SpamDictionary para inicialización, manipulación, y exportación de datos.
 */
public class SpamDictionaryService {

    private final SpamDictionary dictionary;

    /**
     * Constructor para inicializar con un diccionario existente.
     * @param dictionary Instancia de SpamDictionary.
     */
    public SpamDictionaryService(SpamDictionary dictionary) {
        this.dictionary = dictionary;
    }

    /**
     * Inicializa el diccionario desde un archivo JSON ubicado en resources.
     * @param resourcePath Ruta del archivo JSON en los recursos.
     */
    public void initializeFromJson(String resourcePath) {
        try (InputStream inputStream = FileLoader.loadResourceAsStream(resourcePath)) {
            String jsonContent = FileLoader.readFile(inputStream);
            JSONObject jsonObject = new JSONObject(jsonContent);

            // Inicializar las categorías
            for (WordCategory category : WordCategory.values()) {
                dictionary.initializeWordsWithZeroFrequency(
                        category,
                        jsonObject.optJSONArray(category.name().toLowerCase())
                                .toList().stream().map(Object::toString).toList()
                );
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al inicializar desde JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Exporta el contenido del diccionario a un archivo JSON.
     * @param filePath Ruta del archivo donde se exportará el JSON.
     */
    public void exportToJson(String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            JSONObject jsonObject = dictionary.toJson();
            fileWriter.write(jsonObject.toString(4)); // Formateado con sangría
        } catch (IOException e) {
            throw new RuntimeException("Error al exportar a JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Muestra las palabras y frecuencias en la consola.
     */
    public void displayDictionary() {
        System.out.println("=== Display Diccionario ===");
        for (WordCategory category : WordCategory.values()) {
            System.out.println("Categoría: " + category);
            dictionary.getCategory(category).forEach((word, wordData) ->
                    System.out.println(word + " -> " + wordData)
            );
            System.out.println();
        }
    }

    /**
     * Verifica si una palabra existe en el diccionario.
     * @param word Palabra a verificar.
     * @return true si la palabra existe en alguna categoría, false en caso contrario.
     */
    public boolean wordExists(String word) {
        for (WordCategory category : WordCategory.values()) {
            if (dictionary.getCategory(category).containsKey(word)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Actualiza la frecuencia de una palabra existente en la categoría correspondiente.
     * @param word Palabra a actualizar.
     * @param isSpam True para incrementar como spam, false para incrementar como ham.
     */
    public void updateWordFrequency(String word, boolean isSpam) {
        for (WordCategory category : WordCategory.values()) {
            Map<String, WordData> categoryMap = dictionary.getCategory(category);
            if (categoryMap.containsKey(word)) {
                WordData wordData = categoryMap.get(word);
                if (isSpam) {
                    wordData.incrementSpamFrequency();
                } else {
                    wordData.incrementHamFrequency();
                }
                return;
            }
        }
        throw new RuntimeException("La palabra '" + word + "' no existe en el diccionario.");
    }

    /**
     * Agrega una nueva palabra a una categoría específica con frecuencias iniciales en cero.
     * @param category Categoría a la que pertenece la palabra.
     * @param word Palabra a agregar.
     */
    public void addNewWord(WordCategory category, String word) {
        dictionary.addWord(category, word);
    }
}
