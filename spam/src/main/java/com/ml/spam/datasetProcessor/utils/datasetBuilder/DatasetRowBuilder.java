package com.ml.spam.datasetProcessor.utils.datasetBuilder;

import com.ml.spam.datasetProcessor.metadata.LexemeWordMetadata;
import com.ml.spam.datasetProcessor.models.DatasetColumnName;
import com.ml.spam.datasetProcessor.models.DatasetRow;
import com.ml.spam.datasetProcessor.schema.DatasetSchema;
import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.models.WordCategory;
import com.ml.spam.dictionary.models.WordData;

import java.util.*;
import java.util.stream.Collectors;

public class DatasetRowBuilder {

    private final SpamDictionary dictionary; // Diccionario con palabras categorizadas
    private final LexemeWordMetadata metadata; // Metadata con cantidad de palabras por lexema
    private final DatasetSchema schema; // Define las columnas válidas del dataset final

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

        Map<String, Long> freqMap = messageTokens.stream()
                .collect(Collectors.groupingBy(WordData::getWord, Collectors.counting()));

        Map<String, Long> categoryCounts = new HashMap<>();
        Map<WordCategory, Map<String, WordData>> categorizedWords = dictionary.getCategorizedWords();
        for (WordData wd : messageTokens) {
            String word = wd.getWord();
            WordCategory foundCategory = null;
            for (Map.Entry<WordCategory, Map<String, WordData>> entry : categorizedWords.entrySet()) {
                if (entry.getValue().containsKey(word)) {
                    foundCategory = entry.getKey();
                    break;
                }
            }
            String cat = (foundCategory != null) ? foundCategory.getJsonKey() : WordCategory.UNASSIGNED_WORDS.getJsonKey();
            categoryCounts.put(cat, categoryCounts.getOrDefault(cat, 0L) + 1);
        }

        for (String column : schema.getColumnNames()) {
            double value;

            if (column.startsWith(DatasetColumnName.FREQ.get())) {
                String word = column.substring(DatasetColumnName.FREQ.get().length());
                value = freqMap.getOrDefault(word, 0L).doubleValue();

            } else if (column.startsWith(DatasetColumnName.RELATIVE_FREQ_NORM.get())) {
                String word = column.substring(DatasetColumnName.RELATIVE_FREQ_NORM.get().length());
                long freq = freqMap.getOrDefault(word, 0L);
                int lexemeSize = metadata.getWordCountForLexeme(word);
                if (lexemeSize <= 0) lexemeSize = 1;
                value = DatasetFeatureCalculator.calculateRelativeFreqNormLog(freq, totalTokens, lexemeSize);
                if (label.equalsIgnoreCase("spam") &&
                        dictionary.getCategory(WordCategory.STRONG_SPAM_WORD).containsKey(word)) {
                    value *= WordCategory.STRONG_SPAM_WORD.getWeight();
                }

            } else if (column.startsWith(DatasetColumnName.POLARITY.get())) {
                String word = column.substring(DatasetColumnName.POLARITY.get().length());
                value = DatasetFeatureCalculator.calculatePolarity(word, dictionary);
                if (label.equalsIgnoreCase("spam") &&
                        dictionary.getCategory(WordCategory.STRONG_SPAM_WORD).containsKey(word)) {
                    value *= WordCategory.STRONG_SPAM_WORD.getWeight();
                }

            } else if (column.startsWith("count_")) {
                String key = column.substring("count_".length());
                value = categoryCounts.getOrDefault(key, 0L).doubleValue();

            } else if (column.startsWith("ratio_")) {
                String key = column.substring("ratio_".length());
                long count = categoryCounts.getOrDefault(key, 0L);

                boolean isUnweighted = key.equals("neutralBalancedWords") ||
                        key.equals("stopWords") ||
                        key.equals("unassignedWords");

                double weight = 1.0;
                if (!isUnweighted) {
                    try {
                        WordCategory category = WordCategory.fromJsonKey(key);
                        weight = category.getWeight();
                    } catch (IllegalArgumentException ignored) {}
                }

                value = totalTokens > 0 ? (count * weight) / totalTokens : 0.0;

            } else if (column.equals(DatasetColumnName.MESSAGE_LENGTH.get())) {
                value = (double) totalTokens;

            } else if (column.equals(DatasetColumnName.LEXICAL_DIVERSITY.get())) {
                value = DatasetFeatureCalculator.calculateLexicalDiversity(uniqueWords.size(), totalTokens);

            } else if (column.equals(DatasetColumnName.NET_WEIGHTED_SCORE.get())) {
                value = DatasetFeatureCalculator.calculateNetWeightedScore(messageTokens, dictionary);

            } else if (column.equals(DatasetColumnName.LABEL.get())) {
                value = DatasetFeatureCalculator.encodeLabel(label);

            } else {
                value = 0.0;
            }

            features.put(column, value);
        }

        schema.validateRow(features);
        return new DatasetRow(features);
    }






}
