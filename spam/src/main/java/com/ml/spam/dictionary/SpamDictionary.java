package com.ml.spam.dictionary;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *Rol:
 *      Clase Singleton que gestiona las palabras y sus frecuencias en el diccionario.
 *
 * Responsabilidades:
 *      Almacenar palabras relevantes en un mapa.
 *      Identificar "stop words" y símbolos raros.
 *      Agregar o actualizar palabras en el diccionario.
 */


public class SpamDictionary {
    private static final SpamDictionary instance = new SpamDictionary();

    private final Map<String, WordData> wordSpam = new HashMap<>();
    private final Map<String, WordData> rareSymbols = new HashMap<>();
    private final Map<String, WordData> stopWords = new HashMap<>();

    private SpamDictionary() {}

    public static SpamDictionary getInstance() {
        return instance;
    }

    public Map<String, WordData> getWordSpam() {
        return wordSpam;
    }

    public Map<String, WordData> getRareSymbols() {
        return rareSymbols;
    }

    public Map<String, WordData> getStopWords() {
        return stopWords;
    }

    public void initializeDictionary(Set<String> words) {
        // Agrega cada palabra al mapa con frecuencias en 0
        words.forEach(this::initializeWord);
    }


    public void addOrUpdateWord(String word, boolean isSpam) {
        WordData wordData = wordSpam.getOrDefault(word, new WordData(word, 0, 0));
        if (isSpam) {
            wordData.incrementSpamFrequency();
        } else {
            wordData.incrementHamFrequency();
        }
        wordSpam.put(word, wordData);
    }

    public void initializeWord(String word) {
        // Inicializa la palabra con ambas frecuencias en 0
        wordSpam.put(word, new WordData(word, 0, 0));
    }

    public void initializeRareSymbols(Set<String> symbols) {
        symbols.forEach(symbol -> rareSymbols.put(symbol, new WordData(symbol, 0, 0)));
    }

    public void initializeStopWords(Set<String> words) {
        words.forEach(word -> stopWords.put(word, new WordData(word, 0, 0)));
    }

}
