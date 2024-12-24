package com.ml.spam.dictionary.stageMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.reports.DictionarySummaryReport;
import com.ml.spam.dictionary.service.SpamDictionaryService;

import java.io.IOException;

public class DictionaryZeroInitializationAndUpdateMain {
    private static final String catWordsPath = FilePathsConfig.CATEGORIZED_WORDS_FREQUENCIES_ZERO_JSON_PATH;
    private static final String accentPairsPath = FilePathsConfig.ACCENTED_PAIRS_JSON_PATH;
    private static final String  lexemePath=FilePathsConfig.LEXEMES_REPOSITORY_JSON_PATH;

    private static final String testMessagesFilePath = FilePathsConfig.TEST_CSV_DATA_PATH;
    private static final String testCatWords = "static/dictionary/test_catWord_zero.json";

    public static void main(String[] args) throws IOException {
        SpamDictionaryService service = new SpamDictionaryService();

        System.out.println("===  /  /   /   /   /   /   /   /   /   /   ===  Etapa 2: Actualización del Diccionario  === /  /   /   /   /   /   /   /   /   /   === \n");

/**
 ***  Solamente se utilizarían estos 2 mètodos en casos de algun error o duda ***

 //Mostrar el Map de Dictionary para chequear que estén las categorías vacías
 service.displayCategorizedWordsInDictionary();

 // Mostrar el diccionario persistido para testear el formato del json en consola
 service.displayJsonFileDictionary(filePath);
 */

        System.out.println("[ STAGE 1 ]* * * * * Inicializando dictionary   .    .   .  .  .  . . . . . . . . . \n");

        // Inicializar el diccionario desde el JSON
        service.initializeDictionaryFromJsonIfContainOnlyZeroFrequencies(testCatWords, accentPairsPath, lexemePath);

        // Mostrar los Map de SpamDictionary para chequear que se haya inicializado correctamente
        service.displayCategorizedWordsInDictionary();


        System.out.println("\n[ STAGE 2 ]* * * * * Solicitando Actualización    .    .   .  .  .  . . . . . . . . . \n");




        // Solicitar la actualización del diccionario al service
        service.updateDictionary(testMessagesFilePath);
        System.out.println(" *  *  *  *  *  //  //  //  //  //  //  // //  //  //  Actualización finalizada ! ! ! ! !\n");


        System.out.println("\n [ REPORT  ] * * * * * Reporte   .    .   .  .  .  . . . . . . . . . \n");
        // Mostrar SpamDictionary actualizado
        service.displayCategorizedWordsInDictionary();

        System.out.println("\n [ REPORT  ] * * * * * Reporte   .    .   .  .  .  . . . . . . . . . \n");
        // Mostrar el informe del diccionario actualizado
        DictionarySummaryReport.displaySummaryReport(service);


    }

}
