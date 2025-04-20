package com.ml.spam.dictionary.stageMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.models.DatasetMetadata;
import com.ml.spam.dictionary.reports.DictionarySummaryReport;
import com.ml.spam.dictionary.service.SpamDictionaryService;
import com.ml.spam.dictionary.models.SpamDictionary;

import java.io.IOException;

public class DictionaryZeroInitializationAndUpdateMain {

    private static final String newCatWordsFreqZeroPath = FilePathsConfig.CATEGORIZED_WORDS_FREQUENCIES_ZERO_JSON_PATH;
    private static final String lexemePath = FilePathsConfig.LEXEMES_REPOSITORY_JSON_PATH;
    private static final String testMessagesFilePath = FilePathsConfig.TEST_MESSAGES_CSV_ESPAÑOL_DATA_PATH;
    private static final String baseOutputPath = FilePathsConfig.BASE_OUTPUT_JSON_PATH;

    public static void main(String[] args) throws IOException {
        long startUpdate = System.nanoTime();
        SpamDictionaryService service = new SpamDictionaryService();

        System.out.println("=== Etapa 2: Actualización del Diccionario ===\n");

        // Inicialización
        System.out.println("[ STAGE 1 ] Inicializando diccionario desde base con frecuencias en cero...\n");
        service.initializeDictionaryFromJsonIfContainOnlyZeroFrequencies(newCatWordsFreqZeroPath, lexemePath);
        service.displayCategorizedWordsInDictionary();

        // Actualización con dataset
        System.out.println("\n[ STAGE 2 ] Procesando mensajes del dataset...\n");
        service.updateDictionaryFromCsvMessages(testMessagesFilePath);
        System.out.println("Actualización finalizada.\n");

        // REGISTRAR el dataset procesado en metadata
        String datasetFileName = testMessagesFilePath.substring(testMessagesFilePath.lastIndexOf("/") + 1);
        int spamCount = SpamDictionary.getInstance().getMetadata().getTotalSpam();
        int hamCount = SpamDictionary.getInstance().getMetadata().getTotalHam();
        int total = spamCount + hamCount;
        String timestamp = java.time.LocalDateTime.now().toString();

        DatasetMetadata dataset = new DatasetMetadata(
                datasetFileName,
                total,
                hamCount,
                spamCount,
                timestamp
        );
        SpamDictionary.getInstance().getMetadata().addDataset(dataset);

        // Reportes
        System.out.println("\n[ REPORT ] Diccionario actualizado:\n");
        service.displayCategorizedWordsInDictionary();

        System.out.println("\n[ REPORT ] Resumen del diccionario:\n");
        DictionarySummaryReport.displaySummaryReport(service);

        // Exportación
        service.exportUpdatedCategorizedWords(baseOutputPath);
        service.exportMetadataJson(
                FilePathsConfig.DICTIONARY_METADATA_JSON_PATH,
                baseOutputPath
        );

        long endUpdate = System.nanoTime();
        System.out.printf("Tiempo total de ejecución: %.2f ms%n", (endUpdate - startUpdate) / 1_000_000.0);
    }
}
