package com.ml.spam.datasetProcessor.utils;

import com.ml.spam.datasetProcessor.metadata.LexemeWordMetadata;
import com.ml.spam.datasetProcessor.models.DatasetRow;
import com.ml.spam.datasetProcessor.schema.DatasetSchema;
import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.models.WordCategory;
import com.ml.spam.dictionary.models.WordData;

import java.util.*;
import java.util.stream.Collectors;

public class DatasetRowBuilder2 {

    private final SpamDictionary dictionary;
    private final LexemeWordMetadata metadata;
    private final DatasetSchema schema;

    public DatasetRowBuilder2(SpamDictionary dictionary,
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

        // Features por palabra strongSpamWord
        for (String word : schema.getStrongSpamWordsOrdered()) {
            long freq = DatasetFeatureCalculator.calculateFrequency(word, messageTokens);
            int lexemeSize = metadata.getWordCountForLexeme(word);
            double relFreqNorm = DatasetFeatureCalculator.calculateRelativeFreqNorm(freq, totalTokens, lexemeSize);
            double polarity = DatasetFeatureCalculator.calculatePolarity(word, dictionary);

            features.put("freq_" + word, (double) freq);
            features.put("relativeFreqNorm_" + word, relFreqNorm);
            features.put("polarity_" + word, polarity);
        }

        // Features globales por categoría
        Map<WordCategory, Long> countByCategory = new EnumMap<>(WordCategory.class);
        for (WordData wd : messageTokens) {
            WordCategory category = resolveCategory(wd.getWord());
            countByCategory.put(category, countByCategory.getOrDefault(category, 0L) + 1);
        }

        for (WordCategory category : WordCategory.values()) {
            long count = countByCategory.getOrDefault(category, 0L);
            double ratio = totalTokens > 0 ? count * 1.0 / totalTokens : 0.0;

            features.put("count_" + category.getJsonKey(), (double) count);
            features.put("ratio_" + category.getJsonKey(), ratio);
        }

        // Globales adicionales
        features.put("longitud_mensaje", (double) totalTokens);
        features.put("lexical_diversity", DatasetFeatureCalculator.calculateLexicalDiversity(uniqueWords.size(), totalTokens));
        features.put("net_weighted_score", DatasetFeatureCalculator.calculateNetWeightedScore(messageTokens, dictionary));
        features.put("label", DatasetFeatureCalculator.encodeLabel(label));


        //DEBUG//
        System.out.println(">>> DEBUG COLUMNAS GENERADAS vs ESPERADAS <<<");
        System.out.println("  Total generadas (features): " + features.size());
        System.out.println("  Total esperadas (schema): " + schema.getColumnCount());
        System.out.println("  Diferencia: " + (features.size() - schema.getColumnCount()));
        System.out.println("  --- Lista de columnas generadas ---");
        for (String key : features.keySet()) {
            System.out.println("  -> " + key);
        }
        System.out.println("  ------------------------------------");

        //Fin DEBUG//


        schema.validateRow(features);
        return new DatasetRow(features);
    }

    private WordCategory resolveCategory(String word) {
        for (WordCategory category : WordCategory.values()) {
            Map<String, WordData> wordsMap = dictionary.getCategory(category);
            if (wordsMap.containsKey(word)) {
                return category;
            }
        }
        return WordCategory.UNASSIGNED_WORDS;
    }
}
