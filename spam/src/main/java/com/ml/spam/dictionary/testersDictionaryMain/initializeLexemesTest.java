package com.ml.spam.dictionary.testersDictionaryMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.models.LexemeRepositoryCategories;
import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.reports.DictionarySummaryReport;
import com.ml.spam.dictionary.service.SpamDictionaryService;

import java.util.Set;

public class initializeLexemesTest {


      private static final String lexemesRepositoryJsonPath = FilePathsConfig.LEXEMES_REPOSITORY_JSON_PATH;
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

        // Iterar sobre las categorías
        for (LexemeRepositoryCategories category : LexemeRepositoryCategories.values()) {
            Set<String> lexemes = dictionary.getLexemesRepository().get(category);

            if (lexemes == null || lexemes.isEmpty()) {
                System.out.println("Categoría: " + category.getJsonKey() + " (sin lexemas)");
                continue;
            }

            System.out.println("Categoría: " + category.getJsonKey());
            System.out.println("Número de lexemas: " + lexemes.size());

            // Mostrar los primeros 5 lexemas como muestra
            lexemes.stream()
                    .limit(5)
                    .forEach(lexeme -> System.out.println("  " + lexeme));

            System.out.println("...");
        }

        System.out.println("\n[INFO] Reporte de lexemas generado correctamente.");
    }
}
