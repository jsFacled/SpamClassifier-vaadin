package com.ml.spam.dictionary;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Rol:
 *      Implementa la lógica de negocio para manipular y enriquecer el diccionario.
 * Responsabilidades:
 *      Inicializar el diccionario desde listas o archivos.
 *      Enriquecerlo con nuevas palabras.
 *      Consultar y visualizar el contenido o detalles de palabras.
 *
 */


public class SpamDictionaryService {
    private final SpamDictionary dictionary;

    public SpamDictionaryService(SpamDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public void initializeFromFile(String filePath) throws IOException {
        List<String> words = Files.readAllLines(new File(filePath).toPath());
        initializeFromList(Set.copyOf(words));
    }

    public void initializeFromList(Set<String> words) {
        words.forEach(word -> dictionary.addOrUpdateWord(word, false)); // Inicializa como "ham" por defecto
    }

    public void enrichFromSet(Set<String> words, boolean isSpam) {
        words.forEach(word -> dictionary.addOrUpdateWord(word, isSpam));
    }

    public void enrichFromList(List<String> words, boolean isSpam) {
        words.forEach(word -> dictionary.addOrUpdateWord(word, isSpam));
    }

    public void displayDictionary() {
        dictionary.getWordSpam().forEach((word, data) ->
                System.out.println(word + " -> " + data)
        );
        dictionary.getStopWords().forEach((word, data) ->
                System.out.println(word + " -> " + data)
        );
        dictionary.getRareSymbols().forEach((word, data) ->
                System.out.println(word + " -> " + data)
        );

    }

    public void displayWordDetails(String word) {
        WordData data = dictionary.getWordSpam().get(word);
        if (data != null) {
            System.out.println("Word: " + word + ", Spam Frequency: " + data.getSpamFrequency() + ", Ham Frequency: " + data.getHamFrequency());
        } else {
            System.out.println("Word '" + word + "' not found in dictionary.");
        }
    }

    public double calculateRareSymbolSpamWeight(Map<String, Integer> messageSymbols) {
        int totalSpamFrequency = dictionary.getRareSymbols().values().stream()
                .mapToInt(WordData::getSpamFrequency).sum();

        return messageSymbols.entrySet().stream()
                .mapToDouble(entry -> {
                    WordData data = dictionary.getRareSymbols().get(entry.getKey());
                    return data != null ? (data.getSpamFrequency() / (double) totalSpamFrequency) * entry.getValue() : 0;
                })
                .sum();
    }

    public double calculateStopWordSpamWeight(Map<String, Integer> messageStopWords) {
        int totalSpamFrequency = dictionary.getStopWords().values().stream()
                .mapToInt(WordData::getSpamFrequency).sum();

        return messageStopWords.entrySet().stream()
                .mapToDouble(entry -> {
                    WordData data = dictionary.getStopWords().get(entry.getKey());
                    return data != null ? (data.getSpamFrequency() / (double) totalSpamFrequency) * entry.getValue() : 0;
                })
                .sum();
    }


}
