package com.ml.spam.dictionary.reports;

import com.ml.spam.dictionary.models.CharSize;
import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.models.WordCategory;
import com.ml.spam.dictionary.models.WordData;
import com.ml.spam.dictionary.service.SpamDictionaryService;
import com.ml.spam.handlers.ResourcesHandler;
import com.ml.spam.utils.JsonUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
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
        SpamDictionary dictionary = SpamDictionary.getInstance();

        System.out.println("\n=== Reporte de Lexemas ===");

        // Iterar sobre las categorías
        for (CharSize category : CharSize.values()) {
            Map<String, Set<String>> subcategories = dictionary.getLexemesRepository().get(category);

            if (subcategories == null || subcategories.isEmpty()) {
                System.out.println("Categoría: " + category.getJsonKey() + " (sin lexemas)");
                continue;
            }

            System.out.println("Categoría: " + category.getJsonKey());
            System.out.println("Número de subcategorías: " + subcategories.size());

            // Iterar sobre las subcategorías
            for (Map.Entry<String, Set<String>> entry : subcategories.entrySet()) {
                String subcategory = entry.getKey();
                Set<String> lexemes = entry.getValue();

                System.out.println("  Subcategoría: " + subcategory);
                System.out.println("  Número de lexemas: " + lexemes.size());

                // Mostrar los primeros 5 lexemas como muestra
                lexemes.stream()
                        .limit(5)
                        .forEach(lexeme -> System.out.println("    " + lexeme));

                System.out.println("  ...");
            }
        }

        System.out.println("\n[INFO] Reporte de lexemas generado correctamente.");
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


        // Mostrar la cantidad de lexemas por categoría y subcategoría
        Map<CharSize, Map<String, Set<String>>> lexemesRepository = dictionary.getLexemesRepository();
        System.out.println("\nLexeme Categories:");
        for (Map.Entry<CharSize, Map<String, Set<String>>> entry : lexemesRepository.entrySet()) {
            CharSize category = entry.getKey();
            Map<String, Set<String>> subcategories = entry.getValue();

            System.out.printf(" - %s -> Total Subcategories: %d%n", category.name(), subcategories.size());

            for (Map.Entry<String, Set<String>> subcategoryEntry : subcategories.entrySet()) {
                String subcategory = subcategoryEntry.getKey();
                Set<String> lexemes = subcategoryEntry.getValue();

                System.out.printf("   * Subcategory: %s -> Total Lexemes: %d%n", subcategory, lexemes.size());
                lexemes.stream()
                        .limit(5) // Muestra solo los primeros 5 lexemas como ejemplo
                        .forEach(lexeme -> System.out.println("     - " + lexeme));
                System.out.println("     ...");
            }
        }

        // Resumen adicional
        int totalWords = allCategories.values().stream().mapToInt(Map::size).sum();
        System.out.println("\nCategorizedWords Summary:");
        System.out.println(" - Total Categories in CategorizedWords: " + allCategories.size());
        System.out.println(" - Total Words: " + totalWords);

        int totalLexemes = lexemesRepository.values().stream()
                .flatMap(subcategories -> subcategories.values().stream())
                .mapToInt(Set::size)
                .sum();
        System.out.println("\nLexemes Summary:");
        System.out.println(" - Total Categories in LexemesRepository: " + lexemesRepository.size());
        System.out.println(" - Total Lexemes: " + totalLexemes);

        System.out.println("=== End of Full Report ===\n");
    }

    public static void displayCategorizedWordsSummaryFromFile(String resourcePath, ResourcesHandler handler) {
        System.out.println("\n=== Categorized Words Summary Report (From File) ===");

        try {
            // Usar ResourcesHandler para cargar el archivo JSON
            JSONObject jsonObject = handler.loadJson(resourcePath);

            int totalWords = 0; // Contador para el total de palabras

            // Iterar sobre las categorías en el JSON
            for (WordCategory category : WordCategory.values()) {
                String categoryKey = category.getJsonKey();
                JSONObject categoryJson = jsonObject.optJSONObject(categoryKey);

                if (categoryJson == null) {
                    System.out.printf("Category: %s -> No words found.%n", category.name());
                    continue;
                }

                int wordCount = categoryJson.length(); // Número de palabras en la categoría
                totalWords += wordCount;

                System.out.printf("Category: %s -> Number of words: %d%n", category.name(), wordCount);
            }

            // Resumen adicional
            System.out.printf("\nTotal Categories: %d%n", WordCategory.values().length);
            System.out.printf("Total Words in Categorized Words: %d%n", totalWords);
            System.out.println("=== End of Summary ===\n");

        } catch (Exception e) {
            System.err.println("[ERROR] Error reading or processing the JSON file: " + e.getMessage());
        }
    }


    public static void displayLexemesRepositorySummaryFromFile(String jsonFilePath, ResourcesHandler handler) {
        System.out.println("\n=== Lexemes Repository Summary Report ===");

        try {
            // Cargar el archivo JSON estructurado
            JSONObject structuredLexemesJson = handler.loadJson(jsonFilePath);

            // Obtener el mapa de lexemes únicos con sus conteos desde JsonUtils
            Map<String, Integer> lexemeWordCount = JsonUtils.getLexemeWordCountFromStructuredLexemes(structuredLexemesJson);

            // Mostrar el resumen de lexemes únicos
            System.out.printf("Total Unique Lexemes: %d%n", lexemeWordCount.size());
            lexemeWordCount.forEach((lexeme, count) ->
                    System.out.printf("Lexeme: %s -> Count: %d%n", lexeme, count)
            );

            System.out.println("=== End of Summary ===\n");
        } catch (Exception e) {
            System.err.println("[ERROR] Error reading or processing the JSON file: " + e.getMessage());
        }
    }

}//end
