package com.ml.spam.datasetProcessor.utils;

import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.models.WordCategory;
import com.ml.spam.dictionary.models.WordData;

import java.util.List;
import java.util.Set;

public class DatasetFeatureCalculator {

    public static long calculateFrequency(String word, List<WordData> tokens) {
        return tokens.stream().filter(w -> w.getWord().equals(word)).count();
    }

    public static double calculateRelativeFreqNorm(long freq, int totalTokens, int lexemeSize) {
//Para debug
       // System.out.println("<<<<<<---------------<<<<<<<<<<<<<<<<<<<<<<------------------------<<<<<<<<<<<<<<<<<<<<<<<");
      //  System.out.println("freq es: "+freq+"<< totaltokens es: "+totalTokens+ " <<<<<<<<<<< lexemeSize es: "+lexemeSize+"<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
      //  System.out.println("<<<<<<---------------<<<<<<<<<<<<<<<<<<<<<<------------------------<<<<<<<<<<<<<<<<<<<<<<<");
//fin debug

        if (lexemeSize <= 0 || totalTokens <= 0) return 0.0;
        return (freq / (double) totalTokens) / lexemeSize;
    }

    public static double calculateWeight(String word, SpamDictionary dictionary) {
        for (WordCategory category : WordCategory.values()) {
            if (dictionary.getCategory(category).containsKey(word)) {
                return category.getWeight();
            }
        }
        return 0.0;
    }

    public static double calculatePolarity(String word, SpamDictionary dictionary) {
        var data = dictionary.getWordData(word);
        if (data == null) return 0.0;

        int spam = data.getSpamFrequency();
        int ham = data.getHamFrequency();
        return (spam - ham) * 1.0 / (spam + ham + 1);
    }

    public static double calculateLexicalDiversity(int unique, int total) {
        return total > 0 ? unique * 1.0 / total : 0.0;
    }

    public static double calculateNetWeightedScore(List<WordData> tokens, SpamDictionary dictionary) {
        Set<String> strongWords = dictionary.getCategory(WordCategory.STRONG_SPAM_WORD).keySet();

        return tokens.stream()
                .filter(w -> !strongWords.contains(w.getWord()))
                .mapToDouble(w -> {
                    for (WordCategory category : WordCategory.values()) {
                        if (dictionary.getCategory(category).containsKey(w.getWord())) {
                            return category.getWeight();
                        }
                    }
                    return 0.0;
                })
                .sum();
    }

    public static double encodeLabel(String label) {
        if (label.equalsIgnoreCase("spam")) return 1.0;
        if (label.equalsIgnoreCase("ham")) return 0.0;
        throw new IllegalArgumentException("Etiqueta no reconocida: " + label);
    }
}
