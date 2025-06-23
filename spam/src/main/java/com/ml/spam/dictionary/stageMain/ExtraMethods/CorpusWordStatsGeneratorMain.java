package com.ml.spam.dictionary.stageMain.ExtraMethods;

import com.ml.spam.dictionary.models.WordData;
import com.ml.spam.handlers.ResourcesHandler;
import com.ml.spam.utils.TextUtils;

import java.util.*;
public class CorpusWordStatsGeneratorMain {

    public static void main(String[] args) {
        String inputPath = "static/datasets/joined/full_joined_normalized_noduplicates.csv";
        String outputPath = "static/output/word_stats.json";

        ResourcesHandler resourcesHandler = new ResourcesHandler();
        Map<String, WordData> wordMap = new TreeMap<>();

        int spamTotal = 0;
        int hamTotal = 0;

        List<String[]> rows = resourcesHandler.loadQuotedOrPlainLabeledTxtFileAsMessages(inputPath);

        for (String[] row : rows) {
            if (row.length < 2) continue;

            String message = row[0].trim();
            String label = row[1].trim().toLowerCase();

            List<String> tokens = TextUtils.splitMessageAndLowercase(message);

            for (String token : tokens) {
                if (token.isEmpty()) continue;

                WordData wd = wordMap.getOrDefault(token, new WordData(token));
                if (label.equals("spam")) {
                    wd.incrementSpamFrequency(1);
                    spamTotal++;
                } else if (label.equals("ham")) {
                    wd.incrementHamFrequency(1);
                    hamTotal++;
                }
                wordMap.put(token, wd);
            }
        }

        resourcesHandler.exportWordDataMapToJson(wordMap, outputPath);
        System.out.println("✅ Archivo generado correctamente en: " + outputPath);

        // 🟡 Resumen final
        System.out.println("\n[ RESUMEN FINAL ]");
        System.out.println("Palabras distintas: " + wordMap.size());
        System.out.println("Total spamFrequency acumulado: " + spamTotal);
        System.out.println("Total hamFrequency acumulado: " + hamTotal);

        // 🔝 Tabla TOP 50 palabras con más spamFrequency
        System.out.println("\n[ TOP 50 PALABRAS CON MÁS spamFrequency ]");
        System.out.printf("%-20s | %-10s | %-10s%n", "Palabra", "Spam", "Ham");
        System.out.println("---------------------|------------|-----------");

        wordMap.values().stream()
                .filter(wd -> wd.getSpamFrequency() > 0)
                .sorted(Comparator.comparingInt(WordData::getSpamFrequency).reversed())
                .limit(50)
                .forEach(wd -> System.out.printf("%-20s | %-10d | %-10d%n",
                        wd.getWord(), wd.getSpamFrequency(), wd.getHamFrequency()));

    }
}
