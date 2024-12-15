package com.ml.spam.dictionary.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * SpamDictionary:
 * Clase Singleton que gestiona un diccionario compuesto por:
 * - Palabras categorizadas (`categorizedWords`) organizadas por WordCategory.
 * - Pares acentuados/no acentuados (`accentPairs`).
 */
public class SpamDictionary {
    // Instancia única de la clase (Singleton)
    private static final SpamDictionary instance = new SpamDictionary();

    // Palabras categorizadas organizadas por categoría
    private final Map<WordCategory, Map<String, WordData>> categorizedWords = new HashMap<>();

    // Lista de pares acentuados/no acentuados
    private List<Pair> accentPairs = new ArrayList<>();

    /**
     * Constructor privado para inicializar el Singleton.
     * Inicializa un mapa vacío para cada categoría en las palabras categorizadas.
     */
    private SpamDictionary() {
        for (WordCategory category : WordCategory.values()) {
            categorizedWords.put(category, new HashMap<>());
        }
    }

    /**
     * Devuelve la instancia única del diccionario.
     * @return Instancia Singleton de SpamDictionary.
     */
    public static SpamDictionary getInstance() {
        return instance;
    }

    // ============================
    // Métodos para Palabras Categorizadas
    // ============================

    public Map<String, WordData> getCategory(WordCategory category) {
        return categorizedWords.get(category);
    }

    public Map<WordCategory, Map<String, WordData>> getAllCategories() {
        return categorizedWords;
    }

    public void addWord(WordCategory category, String word) {
        categorizedWords.get(category).putIfAbsent(word, new WordData(word));
    }

    public void addWordWithFrequencies(WordCategory category, String word, int spamFrequency, int hamFrequency) {
        categorizedWords.get(category).putIfAbsent(word, new WordData(word, spamFrequency, hamFrequency));
    }

    public void initializeWordsWithZeroFrequency(WordCategory category, Iterable<String> words) {
        words.forEach(word -> addWord(category, word));
    }

    public boolean areFrequenciesZero() {
        for (WordCategory category : categorizedWords.keySet()) {
            for (WordData wordData : categorizedWords.get(category).values()) {
                if (wordData.getSpamFrequency() != 0 || wordData.getHamFrequency() != 0) {
                    return false; // Frecuencia no válida
                }
            }
        }
        return true; // Todas las frecuencias están en cero
    }

    public void clearDictionary() {
        for (WordCategory category : WordCategory.values()) {
            categorizedWords.get(category).clear();
        }
    }

    // ============================
    // Métodos para Pares Acentuados
    // ============================

    public List<Pair> getAccentPairs() {
        return accentPairs;
    }

    public void setAccentPairs(List<Pair> accentPairs) {
        this.accentPairs = accentPairs;
    }

    // ============================
    // Métodos de Inicialización
    // ============================

    /**
     * Inicializa el diccionario completo: palabras categorizadas y pares acentuados.
     * @param categorizedWords Mapa de palabras categorizadas.
     * @param accentPairs Lista de pares acentuados/no acentuados.
     */
    public void initialize(Map<WordCategory, Map<String, WordData>> categorizedWords, List<Pair> accentPairs) {
        this.categorizedWords.clear();
        this.categorizedWords.putAll(categorizedWords);

        this.accentPairs.clear();
        this.accentPairs.addAll(accentPairs);
    }

    // ============================
    // Validaciones
    // ============================

    /**
     * Verifica si el diccionario está completamente cargado.
     * @return True si contiene palabras categorizadas y pares acentuados.
     */
    public boolean isFullyInitialized() {
        return !categorizedWords.isEmpty() && !accentPairs.isEmpty();
    }

    // ============================
    // Clase Interna: Pair
    // ============================

    public record Pair(String accented, String nonAccented, WordCategory category) {}
}
