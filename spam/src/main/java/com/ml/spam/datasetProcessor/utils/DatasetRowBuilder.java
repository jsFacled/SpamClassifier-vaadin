package com.ml.spam.datasetProcessor.utils;

import com.ml.spam.datasetProcessor.metadata.LexemeWordMetadata;
import com.ml.spam.datasetProcessor.models.DatasetRow;
import com.ml.spam.datasetProcessor.schema.DatasetSchema;
import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.models.WordCategory;
import com.ml.spam.dictionary.models.WordData;

import java.util.*;
import java.util.stream.Collectors;

public class DatasetRowBuilder {

    private final SpamDictionary dictionary;
    private final LexemeWordMetadata metadata;
    private final DatasetSchema schema;

    public DatasetRowBuilder(SpamDictionary dictionary,
                             LexemeWordMetadata metadata,
                             DatasetSchema schema) {
        this.dictionary = dictionary;
        this.metadata = metadata;
        this.schema = schema;
    }

    public DatasetRow buildRow(List<WordData> messageTokens, String label) {
        Map<String, Double> features = new LinkedHashMap<>();
        int totalTokens = messageTokens.size();
        Set<String> uniqueWords = messageTokens.stream()
                .map(WordData::getWord)
                .collect(Collectors.toSet());

        // Features dinámicos por cada strongSpamWord
        for (String word : schema.getStrongSpamWords()) {
            long freq = DatasetFeatureCalculator.calculateFrequency(word, messageTokens);
            int lexemeSize = metadata.getWordCountForLexeme(word);
            double relFreqNorm = DatasetFeatureCalculator.calculateRelativeFreqNorm(freq, totalTokens, lexemeSize);
            int weight = DatasetFeatureCalculator.calculateWeight(word, dictionary);
            double polarity = DatasetFeatureCalculator.calculatePolarity(word, dictionary);

            features.put("freq_" + word, (double) freq);
            features.put("relativeFreqNorm_" + word, relFreqNorm);
            features.put("weight_" + word, (double) weight);
            features.put("polarity_" + word, polarity);
        }

        // Features globales por categoría
        Map<WordCategory, Long> countByCategory = messageTokens.stream()
                .collect(Collectors.groupingBy(WordData::getCategory, Collectors.counting()));

        for (WordCategory category : WordCategory.values()) {
            long count = countByCategory.getOrDefault(category, 0L);
            double ratio = totalTokens > 0 ? count * 1.0 / totalTokens : 0.0;

            features.put("count_" + category.getJsonKey(), (double) count);
            features.put("ratio_" + category.getJsonKey(), ratio);
        }

        // Otros globales
        features.put("longitud_mensaje", (double) totalTokens);
        features.put("lexical_diversity", DatasetFeatureCalculator.calculateLexicalDiversity(uniqueWords.size(), totalTokens));
        features.put("net_weighted_score", DatasetFeatureCalculator.calculateNetWeightedScore(messageTokens, dictionary, schema.getStrongSpamWords()));

        // Label
        features.put("label", DatasetFeatureCalculator.encodeLabel(label));

        // Validación opcional
        schema.validateRow(features);

        return new DatasetRow(features);
    }
}
