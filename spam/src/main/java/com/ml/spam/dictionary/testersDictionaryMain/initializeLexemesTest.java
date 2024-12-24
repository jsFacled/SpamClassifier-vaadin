package com.ml.spam.dictionary.testersDictionaryMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.reports.DictionarySummaryReport;
import com.ml.spam.dictionary.service.SpamDictionaryService;

public class initializeLexemesTest {


      private static final String lexemesRepositoryJsonPath = FilePathsConfig.LEXEMES_REPOSITORY_JSON_PATH;
        public static void main(String[] args) {
            SpamDictionaryService service = new SpamDictionaryService();

            System.out.println("===  /  /   /   /   /   /   /   /   /   /   ===  Etapa 2: Actualización del Diccionario  === /  /   /   /   /   /   /   /   /   /   === \n");



            service.initializeLexemes(lexemesRepositoryJsonPath);

            // Mostrar los Map de SpamDictionary para chequear que se haya inicializado correctamente
            DictionarySummaryReport.displayLexemesReport();



        }

}
