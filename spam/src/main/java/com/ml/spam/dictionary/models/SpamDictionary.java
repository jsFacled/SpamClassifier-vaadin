package com.ml.spam.dictionary.models;

import java.util.*;

public class SpamDictionary {
    // Instancia única de la clase (Singleton)
    private static final SpamDictionary instance = new SpamDictionary();

    // Palabras categorizadas organizadas por categoría
    private final Map<WordCategory, Map<String, WordData>> categorizedWords = new HashMap<>();

    // Repositorio de lexemas organizado por CharSize
    private final Map<CharSize, Map<String, Set<String>>> lexemeRepository = new HashMap<>();

    private final SpamDictionaryMetadata metadata = new SpamDictionaryMetadata();

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
    /**
     * Devuelve el WordData asociado a una palabra, sin importar su categoría.
     * Si la palabra no está en ninguna categoría, retorna null.
     */
    public WordData getWordData(String word) {
        for (WordCategory category : categorizedWords.keySet()) {
            Map<String, WordData> words = categorizedWords.get(category);
            if (words.containsKey(word)) {
                return words.get(word);
            }
        }
        return null;
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
    // Metadata
    // ============================
    public SpamDictionaryMetadata getMetadata() {
        return metadata;
    }
    public void setMetadata(SpamDictionaryMetadata metadata) {
        if (metadata != null) {
            this.metadata.setDatasetDetails(metadata.getDatasetDetails());
            this.metadata.setTotalSpam(metadata.getTotalSpam());
            this.metadata.setTotalHam(metadata.getTotalHam());
            this.metadata.setTotalInstances(metadata.getTotalInstances());
            this.metadata.setTotalDatasetsProcessed(metadata.getTotalDatasetsProcessed());
            this.metadata.setExportedDictionaryFileName(metadata.getExportedDictionaryFileName());
        }
    }


   }
