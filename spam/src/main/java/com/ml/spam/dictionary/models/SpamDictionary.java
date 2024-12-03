package com.ml.spam.dictionary.models;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
    // Cada categoría es un Map con palabras como clave y WordData como valor
    private final Map<WordCategory, Map<String, WordData>> dictionary = new HashMap<>();

    /**
     * Constructor privado para inicializar el Singleton.
     * Inicializa un mapa vacío para cada categoría en el diccionario principal.
     */
    private SpamDictionary() {
        for (WordCategory category : WordCategory.values()) {
            dictionary.put(category, new HashMap<>()); // Cambiado de HashSet a HashMap
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
     * @return Un mapa de palabras y sus datos (WordData) correspondiente a la categoría.
     */
    public Map<String, WordData> getCategory(WordCategory category) {
        return dictionary.get(category);
    }

    public Map<WordCategory, Map<String, WordData>> getAllCategories() {
        return dictionary;
    }
    /**
     * Agrega una palabra a la categoría especificada con frecuencias iniciales en cero.
     * Si la palabra ya existe, no se sobrescribe.
     * @param category La categoría a la que pertenece la palabra.
     * @param word El nombre de la palabra a agregar.
     */
    public void addWord(WordCategory category, String word) {
        dictionary.get(category).putIfAbsent(word, new WordData(word));
    }

    /**
     * Agrega una palabra a la categoría especificada con frecuencias personalizadas.
     * Si la palabra ya existe, no se sobrescribe.
     * @param category La categoría a la que pertenece la palabra.
     * @param word El nombre de la palabra.
     * @param spamFrequency Frecuencia como spam.
     * @param hamFrequency Frecuencia como ham.
     */
    public void addWordWithFrequency(WordCategory category, String word, int spamFrequency, int hamFrequency) {
        dictionary.get(category).putIfAbsent(word, new WordData(word, spamFrequency, hamFrequency));
    }

    /**
     * Inicializa una categoría con un conjunto de palabras, asignándoles frecuencias en cero.
     * Si una palabra ya existe, no se sobrescribe.
     * @param category La categoría a inicializar.
     * @param words Iterable de palabras a agregar.
     */
    public void initializeWordsWithZeroFrequency(WordCategory category, Iterable<String> words) {
        words.forEach(word -> addWord(category, word));
    }




    private void loadCategory(JSONObject jsonCategory, Map<String, WordData> targetMap) {
        jsonCategory.keys().forEachRemaining(word -> {
            JSONObject freqData = jsonCategory.getJSONObject(word);
            targetMap.put(word, new WordData(
                    word,
                    freqData.getInt("spamFrequency"),
                    freqData.getInt("hamFrequency")
            ));
        });
    }



    public void clearDictionary() {
        for (WordCategory category : WordCategory.values()) {
            dictionary.get(category).clear();
        }
    }

}
