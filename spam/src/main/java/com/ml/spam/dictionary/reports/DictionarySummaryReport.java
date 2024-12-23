package com.ml.spam.dictionary.reports;

import com.ml.spam.dictionary.models.LexemeRepositoryCategories;
import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.models.WordCategory;
import com.ml.spam.dictionary.models.WordData;
import com.ml.spam.dictionary.service.SpamDictionaryService;

import java.util.Map;
import java.util.Set;

/**
 * The DictionarySummaryReport class provides a structured and concise way
 * to display a summary of the current state of the Spam Dictionary.
 *
 * <p>This class focuses exclusively on the contents of the SpamDictionary
 * and generates a report that includes:
 * <ul>
 *   <li>The number of words in each category (e.g., SPAM_WORDS, STOP_WORDS).</li>
 *   <li>The total number of categories in the dictionary.</li>
 *   <li>The total number of words across all categories.</li>
 * </ul>
 *
 * <p>The report is intended for debugging, monitoring, or analyzing the
 * structure and content of the dictionary after it has been initialized,
 * updated, or manipulated by the SpamDictionaryService.
 *
 * <p>Usage:
 * <pre>
 * {@code
 * DictionarySummaryReport.displaySummaryReport(service);
 * }
 * </pre>
 *
 * <p>This class is designed to work in tandem with the SpamDictionaryService,
 * which acts as the orchestrator for initializing, updating, and exporting the
 * dictionary. The DictionarySummaryReport retrieves the dictionary instance
 * through the service and processes its contents.
 *
 * <p>Example Output:
 * <pre>
 * === Dictionary Summary Report ===
 * Category: SPAM_WORDS
 *  - Number of words: 120
 * Category: STOP_WORDS
 *  - Number of words: 50
 * Category: RARE_SYMBOLS
 *  - Number of words: 30
 *
 * Total Categories: 3
 * Total Words in Dictionary: 200
 * === End of Summary ===
 * </pre>
 *
 * <p>Note: This class does not modify the dictionary or interact with external
 * resources. Its sole purpose is to display information about the dictionary's
 * current state.
 *
 * @author [Your Name]
 * @version 1.0
 */
public class DictionarySummaryReport {

    public static void displaySummaryReport(SpamDictionaryService service) {
        System.out.println("\n=== Dictionary Summary Report ===");

        // Obtener el diccionario y sus categorías
        SpamDictionary dictionary = service.getDictionary();
        Map<WordCategory, Map<String, WordData>> allCategories = dictionary.getAllCategorizedWords();


        int totalWords = 0; // Acumulador para el total de palabras

        // Mostrar un resumen por categoría
        for (WordCategory category : allCategories.keySet()) {
            Map<String, WordData> categoryData = allCategories.get(category);
            int categoryWordCount = categoryData.size();
            totalWords += categoryWordCount; // Sumar al total general
            System.out.println("Category: " + category.name());
            System.out.println(" - Number of words: " + categoryWordCount);
        }

        // Resumen adicional
        System.out.println("\nTotal Categories: " + allCategories.size());
        System.out.println("Total Words in Dictionary: " + totalWords);
        System.out.println("=== End of Summary ===\n");
    }

    public static void displayLexemesReport() {
        //implementar metod
    }

    public static void displayFullReport(SpamDictionaryService spamDictionaryService) {
        System.out.println("\n=== Full Dictionary Report ===");

        // Obtener el diccionario y sus categorías
        SpamDictionary dictionary = spamDictionaryService.getDictionary();
        Map<WordCategory, Map<String, WordData>> allCategories = dictionary.getAllCategorizedWords();

        // Iterar sobre cada categoría y mostrar detalles
        for (WordCategory category : allCategories.keySet()) {
            Map<String, WordData> wordsInCategory = allCategories.get(category);
            System.out.printf("\nCategory: %s -> Total Words: %d%n", category.name(), wordsInCategory.size());

            if (wordsInCategory.isEmpty()) {
                System.out.println(" - No words in this category.");
            } else {
                System.out.println(" - Words and frequencies:");
                wordsInCategory.forEach((word, data) ->
                        System.out.printf("   * %s -> Spam Frequency: %d, Ham Frequency: %d%n",
                                word, data.getSpamFrequency(), data.getHamFrequency()));
            }
        }

        // Mostrar la cantidad de Accent Pairs
        Map<String, SpamDictionary.Pair> accentPairs = dictionary.getAccentPairs();
        System.out.println("\nAccent Pairs Count: " + accentPairs.size());

        // Mostrar la cantidad de lexemas por categoría
        Map<LexemeRepositoryCategories, Set<String>> lexemesRepository = dictionary.getLexemesRepository();
        System.out.println("\nLexeme Categories:");
        lexemesRepository.forEach((category, lexemes) ->
                System.out.printf(" - %s: %d lexemes%n", category.name(), lexemes.size()));

        // Resumen adicional
        int totalWords = allCategories.values().stream().mapToInt(Map::size).sum();
        System.out.println("\nSummary:");
        System.out.println(" - Total Categories: " + allCategories.size());
        System.out.println(" - Total Words in Dictionary: " + totalWords);
        System.out.println("=== End of Full Report ===\n");
    }

}
