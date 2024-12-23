package com.ml.spam.dictionary.models;

import java.util.*;

public class SpamDictionary {
    // Instancia única de la clase (Singleton)
    private static final SpamDictionary instance = new SpamDictionary();

    // Palabras categorizadas organizadas por categoría
    private final Map<WordCategory, Map<String, WordData>> categorizedWords = new HashMap<>();
    // Mapa de pares acentuados/no acentuados para búsquedas rápidas
    private final Map<String, Pair> accentPairs = new HashMap<>();
    // Lista de lexemas organizados por categoría
    private final Map<LexemeRepositoryCategories, Set<String>> lexemesRepository = new HashMap<>();

    /**
     * Constructor privado para inicializar el Singleton.
     * Inicializa los mapas vacíos pero con sus estructuras.
     */
    private SpamDictionary() {
        // Inicializa categorizedWords con las categorías de WordCategory
        for (WordCategory category : WordCategory.values()) {
            categorizedWords.put(category, new HashMap<>());
        }

        // Inicializa lexemesRepository con las categorías de LexemeRepositoryCategories
        for (LexemeRepositoryCategories category : LexemeRepositoryCategories.values()) {
            lexemesRepository.put(category, new HashSet<>());
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

    public Map<WordCategory, Map<String, WordData>> getCategorizedWords() {
        return categorizedWords;
    }

    public Map<WordCategory, Integer> getCategoryCounts() {
        Map<WordCategory, Integer> categoryCounts = new HashMap<>();
        for (Map.Entry<WordCategory, Map<String, WordData>> entry : categorizedWords.entrySet()) {
            categoryCounts.put(entry.getKey(), entry.getValue().size());
        }
        return categoryCounts;
    }

    public Map<String, WordData> getCategory(WordCategory category) {
        return categorizedWords.get(category);
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

    public Map<String, Pair> getAccentPairs() {
        return accentPairs;
    }

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
    // Métodos para LexemesRepository
    // ============================

    public Map<LexemeRepositoryCategories, Set<String>> getLexemesRepository() {
        return lexemesRepository;
    }

    public Set<String> getLexemesByCategory(LexemeRepositoryCategories category) {
        return lexemesRepository.get(category);
    }

    public void addLexeme(LexemeRepositoryCategories category, String lexeme) {
        lexemesRepository.get(category).add(lexeme);
    }

    public void initializeLexemes(Map<LexemeRepositoryCategories, Set<String>> lexemes) {
        lexemesRepository.clear();
        lexemesRepository.putAll(lexemes);
    }

    public boolean containsLexeme(String lexeme) {
        return lexemesRepository.values().stream().anyMatch(set -> set.contains(lexeme));
    }

    public void clearLexemesRepository() {
        for (LexemeRepositoryCategories category : LexemeRepositoryCategories.values()) {
            lexemesRepository.get(category).clear();
        }
    }

    // ============================
    // Clase Interna: Pair
    // ============================

    public record Pair(String accented, String nonAccented, WordCategory category) {}
}
