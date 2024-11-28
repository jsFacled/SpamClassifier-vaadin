package com.ml.spam.dictionary.models;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * SpamDictionary:
 * Clase Singleton que gestiona un diccionario dividido en categorías de palabras.
 * Utiliza WordCategory para categorizar palabras y WordData para almacenar información
 * sobre cada palabra (nombre, frecuencias de spam y ham).
 */
public class SpamDictionary {
    // Instancia única de la clase (Singleton)
    private static final SpamDictionary instance = new SpamDictionary();

    // Mapa principal que organiza las palabras por categorías
    private final Map<WordCategory, Set<WordData>> dictionary = new HashMap<>();

    /**
     * Constructor privado para inicializar el Singleton.
     * Inicializa un conjunto vacío para cada categoría en el mapa principal.
     */
    private SpamDictionary() {
        for (WordCategory category : WordCategory.values()) {
            dictionary.put(category, new HashSet<>());
        }
    }

    /**
     * Devuelve la instancia única del diccionario.
     * @return Instancia Singleton de SpamDictionary.
     */
    public static SpamDictionary getInstance() {
        return instance;
    }

    /**
     * Obtiene todas las palabras asociadas a una categoría específica.
     * @param category La categoría de palabras (WordCategory).
     * @return Un conjunto de WordData correspondiente a la categoría.
     */
    public Set<WordData> getCategory(WordCategory category) {
        return dictionary.get(category);
    }

    /**
     * Agrega una palabra a la categoría especificada con frecuencias iniciales en cero.
     * Si la palabra ya existe, no se agrega nuevamente.
     * @param category La categoría a la que pertenece la palabra.
     * @param word El nombre de la palabra a agregar.
     */
    public void addWord(WordCategory category, String word) {
        dictionary.get(category).add(new WordData(word));
    }

    /**
     * Agrega una palabra a la categoría especificada con frecuencias personalizadas.
     * Si la palabra ya existe, no se agrega nuevamente.
     * @param category La categoría a la que pertenece la palabra.
     * @param word El nombre de la palabra.
     * @param spamFrequency Frecuencia como spam.
     * @param hamFrequency Frecuencia como ham.
     */
    public void addWordWithFrequency(WordCategory category, String word, int spamFrequency, int hamFrequency) {
        dictionary.get(category).add(new WordData(word, spamFrequency, hamFrequency));
    }

    /**
     * Inicializa una categoría con un conjunto de palabras, asignándoles frecuencias en cero.
     * Si una palabra ya existe, no se agrega nuevamente.
     * @param category La categoría a inicializar.
     * @param words Iterable de palabras a agregar.
     */
    public void initializeWordsWithZeroFrequency(WordCategory category, Iterable<String> words) {
        words.forEach(word -> addWord(category, word));
    }

    /**
     * Carga palabras y sus frecuencias desde un archivo JSON para todas las categorías.
     * Sobrescribe las categorías existentes en el diccionario.
     * @param jsonObject Objeto JSON que contiene las categorías y palabras.
     */
    public void initializeFromJson(JSONObject jsonObject) {
        for (WordCategory category : WordCategory.values()) {
            JSONObject jsonCategory = jsonObject.optJSONObject(category.name().toLowerCase());
            if (jsonCategory != null) {
                loadCategory(jsonCategory, dictionary.get(category));
            }
        }
    }

    /**
     * Convierte el contenido del diccionario a un objeto JSON para exportarlo o persistirlo.
     * @return Un JSONObject que representa el diccionario completo.
     */
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        for (WordCategory category : WordCategory.values()) {
            jsonObject.put(category.name().toLowerCase(), categoryToJson(dictionary.get(category)));
        }
        return jsonObject;
    }

    /**
     * Carga una categoría desde un JSON en el conjunto correspondiente.
     * @param jsonCategory JSON que contiene las palabras y sus frecuencias.
     * @param targetSet Conjunto donde se almacenarán las palabras cargadas.
     */
    private void loadCategory(JSONObject jsonCategory, Set<WordData> targetSet) {
        jsonCategory.keys().forEachRemaining(word -> {
            JSONObject freqData = jsonCategory.getJSONObject(word);
            targetSet.add(new WordData(
                    word,
                    freqData.getInt("spamFrequency"),
                    freqData.getInt("hamFrequency")
            ));
        });
    }

    /**
     * Convierte un conjunto de WordData a un objeto JSON.
     * @param category Conjunto de WordData a convertir.
     * @return Un JSONObject que representa la categoría.
     */
    private JSONObject categoryToJson(Set<WordData> category) {
        JSONObject jsonCategory = new JSONObject();
        category.forEach(wordData -> {
            JSONObject freqData = new JSONObject();
            freqData.put("spamFrequency", wordData.getSpamFrequency());
            freqData.put("hamFrequency", wordData.getHamFrequency());
            jsonCategory.put(wordData.getWord(), freqData);
        });
        return jsonCategory;
    }
}
