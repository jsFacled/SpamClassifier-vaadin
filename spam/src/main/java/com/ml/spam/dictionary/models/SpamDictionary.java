package com.ml.spam.dictionary.models;

import java.util.*;

/**
 * SpamDictionary:
 * Clase Singleton que gestiona un diccionario que incluye:
 * - Palabras categorizadas organizadas en `categorizedWords` según WordCategory.
 * - Un mapa de pares acentuados/no acentuados (`accentPairs`) para búsquedas rápidas.
 * - Diccionario de lexemas (`categoryLexemes`) organizado por categorías específicas.
 */
public class SpamDictionary {
    // Instancia única de la clase (Singleton)
    private static final SpamDictionary instance = new SpamDictionary();

    // Palabras categorizadas organizadas por categoría
    private final Map<WordCategory, Map<String, WordData>> categorizedWords = new HashMap<>();
    // Diccionario de lexemas organizados por categoría (ejemplo: numdim, lexcal, etc.)
    private final Map<String, List<String>> categoryLexemes = new HashMap<>();

    // Mapa de pares acentuados/no acentuados para búsquedas rápidas
    private final Map<String, Pair> accentPairs = new HashMap<>();

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

    public boolean containsWord(String word) {
        for (Map<String, WordData> category : categorizedWords.values()) {
            if (category.containsKey(word)) {
                return true;
            }
        }
        return false;
    }

    // ============================
    // Métodos para Pares Acentuados
    // ============================

    public Pair getAccentPair(String accentedWord) {
        return accentPairs.get(accentedWord);
    }

    public void addAccentPair(String accented, String nonAccented, WordCategory category) {
        accentPairs.put(accented, new Pair(accented, nonAccented, category));
    }

    public void initializeAccentPairs(Map<String, Pair> pairs) {
        accentPairs.clear();
        accentPairs.putAll(pairs);
    }

    // ============================
    // Métodos de Inicialización
    // ============================

    /**
     * Inicializa el diccionario completo: palabras categorizadas y pares acentuados.
     * @param categorizedWords Mapa de palabras categorizadas.
     * @param accentPairs Mapa de pares acentuados/no acentuados.
     */
    public void initialize(Map<WordCategory, Map<String, WordData>> categorizedWords, Map<String, Pair> accentPairs) {
        this.categorizedWords.clear();
        this.categorizedWords.putAll(categorizedWords);

        this.accentPairs.clear();
        this.accentPairs.putAll(accentPairs);
    }

    public boolean isFullyInitialized() {
        return !categorizedWords.isEmpty() && !accentPairs.isEmpty();
    }

    // ============================
    // Clase Interna: Pair
    // ============================

    public record Pair(String accented, String nonAccented, WordCategory category) {}
}
