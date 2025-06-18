package com.ml.spam.dictionary.testersDictionaryMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.models.CharSize;
import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.service.SpamDictionaryService;

import java.util.Map;
import java.util.Set;

public class initializeLexemesTest {


      private static final String lexemesRepositoryJsonPath = FilePathsConfig.DICTIONARY_LEXEMES_REPOSITORY_JSON_PATH;
        public static void main(String[] args) {
            SpamDictionaryService service = new SpamDictionaryService();

            System.out.println("===  /  /   /   /   /   /   /   /   /   /   ===  Etapa 2: Actualización del Diccionario  === /  /   /   /   /   /   /   /   /   /   === \n");



            service.initializeLexemes(lexemesRepositoryJsonPath);

            // Mostrar los Map de SpamDictionary para chequear que se haya inicializado correctamente
           // DictionarySummaryReport.displayLexemesReport();
displayLexemesReportInThisTest();


        }
    public static void displayLexemesReportInThisTest() {
        SpamDictionary dictionary = SpamDictionary.getInstance();

        System.out.println("\n=== Reporte de Lexemas ===");

        // Iterar sobre las categorías principales
        for (CharSize category : CharSize.values()) {
            Map<String, Set<String>> subCategoryMap = dictionary.getLexemesRepository().get(category);

            if (subCategoryMap == null || subCategoryMap.isEmpty()) {
                System.out.println("Categoría: " + category.getJsonKey() + " (sin subcategorías ni lexemas)");
                continue;
            }

            System.out.println("Categoría: " + category.getJsonKey());
            System.out.println("Número de subcategorías: " + subCategoryMap.size());

            // Iterar sobre las subcategorías dentro de cada categoría
            for (Map.Entry<String, Set<String>> subCategoryEntry : subCategoryMap.entrySet()) {
                String subCategory = subCategoryEntry.getKey();
                Set<String> lexemes = subCategoryEntry.getValue();

                System.out.println("  Subcategoría: " + subCategory);
                System.out.println("  Número de lexemas: " + (lexemes != null ? lexemes.size() : 0));

                if (lexemes != null && !lexemes.isEmpty()) {
                    lexemes.stream()
                            .limit(5) // Mostrar un máximo de 5 lexemas
                            .forEach(lexeme -> System.out.println("    - " + lexeme));
                } else {
                    System.out.println("    (sin lexemas)");
                }
            }
            System.out.println("...");
        }

        System.out.println("\n[INFO] Reporte de lexemas generado correctamente.");
    }
}
