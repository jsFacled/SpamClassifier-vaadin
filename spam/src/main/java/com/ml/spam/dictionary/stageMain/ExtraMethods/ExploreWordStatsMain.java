package com.ml.spam.dictionary.stageMain.ExtraMethods;

import com.ml.spam.dictionary.models.WordData;
import com.ml.spam.handlers.ResourcesHandler;
import com.ml.spam.utils.WordStatsAnalyzer;

import java.util.*;

public class ExploreWordStatsMain {

    public static void main(String[] args) {
        String inputPath = "static/output/word_stats.json";
        String outputPath = "static/output/filtered_words.json";

        ResourcesHandler handler = new ResourcesHandler();

        try {
            // Leer JSON generado anteriormente
            Map<String, WordData> wordMap = handler.loadWordDataMapFromJson(inputPath);

            // Crear analizador
            WordStatsAnalyzer analyzer = new WordStatsAnalyzer(wordMap);

            // Elegí el análisis que quieras ejecutar
            List<WordData> topSpam = analyzer.getTopNBySpam(50);
            List<WordData> spamDominantes = analyzer.filterBySpamGreaterThanHam();

            // Mostrar por consola
            System.out.println("\nTOP 50 por spam:");
            analyzer.printAsTable(topSpam);

            System.out.println("\nPalabras con spam > ham:");
            analyzer.printAsTable(spamDominantes);

            // Exportar resultado
            handler.exportFilteredWordDataList(spamDominantes, outputPath);
            System.out.println("\n✅ Exportado JSON con palabras filtradas a: " + outputPath);

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
