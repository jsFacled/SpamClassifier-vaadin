package com.ml.spam.datasetProcessor.utils;

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
        Map<String, Double> features = new LinkedHashMap<>(); // Mapa para almacenar los features de esta fila
        int totalTokens = messageTokens.size();

        // Conjunto de palabras únicas del mensaje
        Set<String> uniqueWords = messageTokens.stream()
                .map(WordData::getWord)
                .collect(Collectors.toSet());

        // Frecuencia de cada palabra del mensaje
        Map<String, Long> freqMap = messageTokens.stream()
                .collect(Collectors.groupingBy(WordData::getWord, Collectors.counting()));

        // Conteo de cuántas veces aparece cada categoría (como moderateSpamWord, etc.)
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

            // ** Para test **

            if (foundCategory == null) {
                System.out.println(" < < < < [Testeando DatesRowBuilder] > > > No encontrado esta  *word* en categorías: " + word + "<<<>>>");
            }

            // ** Fin test


            String cat = (foundCategory != null) ? foundCategory.getJsonKey() : "unassignedWords";
            categoryCounts.put(cat, categoryCounts.getOrDefault(cat, 0L) + 1);
        }

        // Recorrer todas las columnas que el schema indica como válidas
        for (String column : schema.getColumnNames()) {
            double value; // Valor calculado para la columna actual

            // Si la columna es del tipo freq_<palabra>
            if (column.startsWith(DatasetColumnName.FREQ.get())) {
                String word = column.substring(DatasetColumnName.FREQ.get().length());
                value = freqMap.getOrDefault(word, 0L).doubleValue();

                // Si la columna es del tipo relativeFreqNorm_<palabra>
            } else if (column.startsWith(DatasetColumnName.RELATIVE_FREQ_NORM.get())) {
                String word = column.substring(DatasetColumnName.RELATIVE_FREQ_NORM.get().length());
                long freq = freqMap.getOrDefault(word, 0L);
                int lexemeSize = metadata.getWordCountForLexeme(word);
                if (lexemeSize <= 0) lexemeSize = 1;
                //<<<<<<<<<<<<<< Para realizar pruebas<<<<<<<<<<<<<<<<<<<<
                if (word.equals("$")) {
                    System.out.println(">>> freq_$ = " + freq +" <<<<<<<<<<<<<<");
                    System.out.println(">>> totalTokens = " + totalTokens +" <<<<<<<<<<<<<<");
                    System.out.println(">>> lexemeSize_$ = " + lexemeSize +" <<<<<<<<<<<<<<");
                }
                //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

                value = DatasetFeatureCalculator.calculateRelativeFreqNorm(freq, totalTokens, lexemeSize);

                // Si la columna es del tipo polarity_<palabra>
            } else if (column.startsWith(DatasetColumnName.POLARITY.get())) {
                String word = column.substring(DatasetColumnName.POLARITY.get().length());
                value = DatasetFeatureCalculator.calculatePolarity(word, dictionary);

                // Si la columna es del tipo count_<categoria>
            } else if (column.startsWith("count_")) {
                String key = column.substring("count_".length());
                value = categoryCounts.getOrDefault(key, 0L).doubleValue();

                // Si la columna es del tipo ratio_<categoria>
            } else if (column.startsWith("ratio_")) {
                String key = column.substring("ratio_".length());
                long count = categoryCounts.getOrDefault(key, 0L);
                value = totalTokens > 0 ? count * 1.0 / totalTokens : 0.0;

                // longitud total del mensaje
            } else if (column.equals(DatasetColumnName.MESSAGE_LENGTH.get())) {
                value = (double) totalTokens;

                // diversidad léxica: palabras únicas / total
            } else if (column.equals(DatasetColumnName.LEXICAL_DIVERSITY.get())) {
                value = DatasetFeatureCalculator.calculateLexicalDiversity(uniqueWords.size(), totalTokens);

                // suma de pesos (sin strongSpamWords)
            } else if (column.equals(DatasetColumnName.NET_WEIGHTED_SCORE.get())) {
                value = DatasetFeatureCalculator.calculateNetWeightedScore(messageTokens, dictionary);

                // etiqueta: 1 para spam, 0 para ham
            } else if (column.equals(DatasetColumnName.LABEL.get())) {
                value = DatasetFeatureCalculator.encodeLabel(label);

                // Si no se reconoce la columna, se deja en cero
            } else {
                value = 0.0;
            }

            features.put(column, value); // Agrega el feature a la fila
        }

        schema.validateRow(features); // Verifica que la fila final cumple con el schema
        return new DatasetRow(features); // Devuelve la fila completa
    }
}
