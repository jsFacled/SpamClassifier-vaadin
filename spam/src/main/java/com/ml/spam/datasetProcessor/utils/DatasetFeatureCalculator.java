package com.ml.spam.datasetProcessor.utils;

import com.ml.spam.datasetProcessor.models.DatasetRow;
import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.models.WordCategory;
import com.ml.spam.dictionary.models.WordData;

import java.util.*;

public class DatasetFeatureCalculator {

    private static final List<String> STRONG_SPAM_WORDS = List.of(
            // completar con tus 43 palabras elegidas
            //"gratis", "promocion", "oferta", /* etc */
    );

    public static List<DatasetRow> createDatasetFromProcessedWordData(
            List<List<WordData>> allMessages,
            List<String[]> rawRows,
            SpamDictionary dictionary) {

        List<DatasetRow> rows = new ArrayList<>();

        for (int i = 0; i < allMessages.size(); i++) {
            List<WordData> message = allMessages.get(i);
            String label = rawRows.get(i)[1];

            DatasetRow row = calculateFeaturesForMessage(message, label, dictionary);
            rows.add(row);
        }

        return rows;
    }

    public static DatasetRow calculateFeaturesForMessage(
            List<WordData> messageWords,
            String label,
            SpamDictionary dictionary) {

        Map<String, Double> featureMap = new LinkedHashMap<>();
        int totalTokens = messageWords.size();
        Set<String> uniqueWords = new HashSet<>();

        // --- Features por palabra (strongSpamWord) ---
        for (String word : STRONG_SPAM_WORDS) {
            long freq = messageWords.stream().filter(w -> w.getWord().equals(word)).count();
            double relFreq = totalTokens > 0 ? (double) freq / totalTokens : 0;

            int weight = dictionary.getWeightForWord(word); // debe implementarse si no está
            WordData globalData = dictionary.getWordData(word);
            double polarity = globalData != null
                    ? (globalData.getSpamFrequency() - globalData.getHamFrequency()) * 1.0
                    / (globalData.getSpamFrequency() + globalData.getHamFrequency() + 1)
                    : 0;

            featureMap.put("freq_" + word, (double) freq);
            featureMap.put("relative_freq_" + word, relFreq);
            featureMap.put("weight_" + word, (double) weight);
            featureMap.put("polarity_" + word, polarity);
        }

        // --- Features globales ---
        Map<WordCategory, Long> categoryCounts = messageWords.stream()
                .collect(Collectors.groupingBy(WordData::getCategory, Collectors.counting()));

        for (WordCategory category : WordCategory.values()) {
            if (categoryCounts.containsKey(category)) {
                long count = categoryCounts.get(category);
                double ratio = totalTokens > 0 ? count * 1.0 / totalTokens : 0;

                featureMap.put("count_" + category.getJsonKey(), (double) count);
                featureMap.put("ratio_" + category.getJsonKey(), ratio);
            } else {
                featureMap.put("count_" + category.getJsonKey(), 0.0);
                featureMap.put("ratio_" + category.getJsonKey(), 0.0);
            }
        }

        // --- Otros globales ---
        for (WordData wd : messageWords) uniqueWords.add(wd.getWord());

        featureMap.put("longitud_mensaje", (double) totalTokens);
        featureMap.put("lexical_diversity", totalTokens > 0 ? uniqueWords.size() * 1.0 / totalTokens : 0);

        double netWeightedScore = messageWords.stream()
                .filter(w -> w.getCategory() != WordCategory.STRONG_SPAM_WORD)
                .mapToDouble(w -> dictionary.getWeightForCategory(w.getCategory()))
                .sum();

        featureMap.put("net_weighted_score", netWeightedScore);

        // --- Label ---
        featureMap.put("label", label.equalsIgnoreCase("spam") ? 1.0 : 0.0);

        return new DatasetRow(featureMap);
    }
}
