package com.ml.spam.dictionary.models;

import java.util.*;

public class SpamDictionary {
    // Instancia única de la clase (Singleton)
    private static final SpamDictionary instance = new SpamDictionary();

    // Palabras categorizadas organizadas por categoría
    private final Map<WordCategory, Map<String, WordData>> categorizedWords = new HashMap<>();
    // Mapa de pares acentuados/no acentuados para búsquedas rápidas
    private final Map<String, Pair> accentPairs = new HashMap<>();
    // Repositorio de lexemas organizado por CharSize
    private final Map<CharSize, Map<String, Set<String>>> lexemeRepository = new HashMap<>();

    /**
     * Constructor privado para inicializar el Singleton.
     * Inicializa los mapas vacíos pero con sus estructuras.
     */
    private SpamDictionary() {
        // Inicializa categorizedWords con las categorías de WordCategory
        for (WordCategory category : WordCategory.values()) {
            categorizedWords.put(category, new HashMap<>());
        }

        // Inicializa lexemeRepository con CharSize y subcategorías vacías
        for (CharSize charSize : CharSize.values()) {
            lexemeRepository.put(charSize, new HashMap<>());
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

/*
    //Supuestamente agrega palabras pero primero las borra si está presente.
    public void addWordWithFrequencies(WordCategory category, String word, int spamFrequency, int hamFrequency) {
        // Eliminar la palabra de todas las categorías si ya existe
        for (WordCategory existingCategory : categorizedWords.keySet()) {
            if (categorizedWords.get(existingCategory).containsKey(word)) {
                categorizedWords.get(existingCategory).remove(word);
                break; // Salir del bucle tras encontrar la palabra
            }
        }

        // Agregar la palabra a la nueva categoría
        categorizedWords.get(category).put(word, new WordData(word, spamFrequency, hamFrequency));
    }
*/
    public void addWordWithFrequencies(WordCategory category, String word, int spamFrequency, int hamFrequency) {
        categorizedWords.get(category).putIfAbsent(word, new WordData(word, spamFrequency, hamFrequency));
    }

  //////////////////////////////////////////////////////////////////////////////////////



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

    public boolean containsWord(String token) {
        // Buscar solo en las palabras categorizadas
        for (Map<String, WordData> category : categorizedWords.values()) {
            if (category.containsKey(token)) {
                return true;
            }
        }
        // Si no se encuentra, retorna false
        return false;
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

    public void addAccentPair(String accentedWord, String nonAccented, WordCategory category) {
        if (accentedWord == null || nonAccented == null || category == null) {
            throw new IllegalArgumentException("Los valores para accentedWord, nonAccented y category no pueden ser nulos.");
        }
        accentPairs.put(accentedWord, new Pair(nonAccented, category));
    }

    public void initializeAccentPairs(Map<String, Pair> pairs) {
        accentPairs.clear();
        accentPairs.putAll(pairs);
    }

    // ============================
    // Métodos para LexemeRepository
    // ============================

    public Map<CharSize, Map<String, Set<String>>> getLexemeRepository() {
        return lexemeRepository;
    }

    public Map<String, Set<String>> getLexemesByCharSize(CharSize charSize) {
        return lexemeRepository.getOrDefault(charSize, new HashMap<>());
    }

    public Set<String> getLexElements(CharSize charSize, String lexeme) {
        return lexemeRepository.getOrDefault(charSize, new HashMap<>())
                .getOrDefault(lexeme, new HashSet<>());
    }

    public void addLexElement(CharSize charSize, String lexeme, String lexElement) {
        lexemeRepository.computeIfAbsent(charSize, k -> new HashMap<>())
                .computeIfAbsent(lexeme, k -> new HashSet<>())
                .add(lexElement);
    }

    public void initializeLexemeRepository(Map<CharSize, Map<String, Set<String>>> newRepository) {
        lexemeRepository.clear();
        lexemeRepository.putAll(newRepository);
    }

    public void clearLexemeRepository() {
        lexemeRepository.clear();
    }

    public boolean containsLexElement(CharSize charSize, String lexeme, String lexElement) {
        return lexemeRepository.getOrDefault(charSize, new HashMap<>())
                .getOrDefault(lexeme, new HashSet<>())
                .contains(lexElement);
    }

    public boolean containsLexElementInRepository(String lexElement) {
        return lexemeRepository.values().stream()
                .flatMap(groupMap -> groupMap.values().stream())
                .anyMatch(elements -> elements.contains(lexElement));
    }

    public Map<CharSize, Map<String, Set<String>>> getLexemesRepository() {
        return lexemeRepository;
    }

    public Map<WordCategory, Map<String, WordData>> getAllCategorizedWords() {
return categorizedWords;
    }

    // ============================
    // Clase Interna: Pair
    // ============================

    public record Pair(String nonAccented, WordCategory category) {}
}
