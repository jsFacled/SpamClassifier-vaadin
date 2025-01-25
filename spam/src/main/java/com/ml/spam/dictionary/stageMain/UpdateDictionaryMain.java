package com.ml.spam.dictionary.stageMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.reports.DictionarySummaryReport;
import com.ml.spam.dictionary.service.SpamDictionaryService;

import java.io.IOException;

public class UpdateDictionaryMain {
    // Nombre del archivo que se generará al exportar categorizedWords.
    // No sobreescribe. Si existe se incrementará un número.
    private static final String baseOutputPath = FilePathsConfig.BASE_OUTPUT_JSON_PATH;

    //Elementos del Dictionary
    private static final String updatedCatWordsPath = "static/dictionary/updatedCategorizedWords.json";
    private static final String accentPairsPath = FilePathsConfig.ACCENTED_PAIRS_JSON_PATH;
    private static final String lexemePath = FilePathsConfig.LEXEMES_REPOSITORY_JSON_PATH;

    private static final String TestMessagesFilePath = FilePathsConfig.TEST_MESSAGES_CSV_ESPAÑOL_DATA_PATH;
    private static final String TrainMessagesFilePath = FilePathsConfig.TRAIN_MESSAGES_CSV_ESPAÑOL_DATA_PATH;
    private static final String CleanedTrainMessagesFilePath = "static/datasets/train-mensajesEspañol_cleaned.csv";





    public static void main(String[] args) throws IOException {
        // Inicia temporizador para calcular el tiempo de procesamiento total
        long startUpdate = System.nanoTime();

        SpamDictionaryService service = new SpamDictionaryService();

        System.out.println("===  /  /   /   /   /   /   /   /   /   /   ===  Etapa 2: Actualización del Diccionario desde archivo actualizado === /  /   /   /   /   /   /   /   /   /   === \n");

        System.out.println("[ STAGE 1 ]* * * * * Inicializando dictionary desde archivo actualizado   .    .   .  .  .  . . . . . . . . . \n");

        // Inicializar el diccionario desde el JSON actualizado
        service.initializeDictionaryFromJson(updatedCatWordsPath, accentPairsPath, lexemePath);

        // Mostrar los Map de SpamDictionary para chequear que se haya inicializado correctamente
        service.displayCategorizedWordsInDictionary();

        System.out.println("\n[ STAGE 2 ]* * * * * Solicitando Actualización    .    .   .  .  .  . . . . . . . . . \n");

        // Solicitar la actualización del diccionario al service Desde un set de Mensajes
        service.updateDictionaryFromCsvMessages(CleanedTrainMessagesFilePath);
        System.out.println(" *  *  *  *  *  //  //  //  //  //  //  // //  //  //  Actualización finalizada ! ! ! ! !\n");

        System.out.println("\n [ REPORT  ] * * * * * Reporte   .    .   .  .  .  . . . . . . . . . \n");
        // Mostrar SpamDictionary actualizado
        service.displayCategorizedWordsInDictionary();

        System.out.println("\n [ REPORT  ] * * * * * Reporte   .    .   .  .  .  . . . . . . . . . \n");
        // Mostrar el informe del diccionario actualizado
        DictionarySummaryReport.displaySummaryReport(service);

        // Exportación del diccionario actualizado
        service.exportUpdatedCategorizedWords(baseOutputPath);

        long endUpdate = System.nanoTime();
        System.out.printf("Tiempo de actualización del diccionario: %.2f ms%n", (endUpdate - startUpdate) / 1_000_000.0);
    }
}
