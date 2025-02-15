package com.ml.spam.dictionary.stageMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.reports.DictionarySummaryReport;
import com.ml.spam.dictionary.service.SpamDictionaryService;

import java.io.IOException;

public class DictionaryZeroInitializationAndUpdateMain {
    private static final String newCatWordsFreqZeroPath = FilePathsConfig.CATEGORIZED_WORDS_FREQUENCIES_ZERO_JSON_PATH;

    private static final String accentPairsPath = FilePathsConfig.ACCENTED_PAIRS_JSON_PATH;
    private static final String  lexemePath=FilePathsConfig.LEXEMES_REPOSITORY_JSON_PATH;

    private static final String mensajesPruebaFilePath = FilePathsConfig.PRUEBA_CSV_DATA_PATH;
    private static final String TestMessagesFilePath = FilePathsConfig.TEST_MESSAGES_CSV_ESPAÑOL_DATA_PATH;
    private static final String TrainMessagesFilePath = FilePathsConfig.TRAIN_MESSAGES_CSV_ESPAÑOL_DATA_PATH;

//Nombre del archivo que se generará al exportar categorizedWords.
// No sobreescribe. Si existe se incrementará un número.
private static final String baseOutputPath = "static/dictionary/categorizedWords/updatedCategorizedWords.json";

    public static void main(String[] args) throws IOException {
        //Inicia temporizador para calcular el tiempo de procesamiento total
        long startUpdate = System.nanoTime();

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
        service.initializeDictionaryFromJsonIfContainOnlyZeroFrequencies(newCatWordsFreqZeroPath, accentPairsPath, lexemePath);

        // Mostrar los Map de SpamDictionary para chequear que se haya inicializado correctamente
        service.displayCategorizedWordsInDictionary();


        System.out.println("\n[ STAGE 2 ]* * * * * Solicitando Actualización    .    .   .  .  .  . . . . . . . . . \n");




        // Solicitar la actualización del diccionario al service Desde un set de Mensajes
        service.updateDictionaryFromCsvMessages(TestMessagesFilePath);
        System.out.println(" *  *  *  *  *  //  //  //  //  //  //  // //  //  //  Actualización finalizada ! ! ! ! !\n");


        System.out.println("\n [ REPORT  ] * * * * * Reporte   .    .   .  .  .  . . . . . . . . . \n");
        // Mostrar SpamDictionary actualizado
        service.displayCategorizedWordsInDictionary();

        System.out.println("\n [ REPORT  ] * * * * * Reporte   .    .   .  .  .  . . . . . . . . . \n");
        // Mostrar el informe del diccionario actualizado
        DictionarySummaryReport.displaySummaryReport(service);

 service.exportUpdatedCategorizedWords(baseOutputPath);

        long endUpdate = System.nanoTime();
        System.out.printf("Tiempo de actualización del diccionario: %.2f ms%n", (endUpdate - startUpdate) / 1_000_000.0);

    }

}
