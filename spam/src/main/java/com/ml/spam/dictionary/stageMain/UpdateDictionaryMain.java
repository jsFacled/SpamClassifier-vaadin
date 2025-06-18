package com.ml.spam.dictionary.stageMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.models.DatasetMetadata;
import com.ml.spam.dictionary.reports.DictionarySummaryReport;
import com.ml.spam.dictionary.service.SpamDictionaryService;
import com.ml.spam.dictionary.models.SpamDictionary;

import java.io.IOException;

public class UpdateDictionaryMain {

    private static final String baseOutputPath = FilePathsConfig.DICTIONARY_OUTPUT_BASE_JSON_PATH;
    private static final String dictionaryMetadataJsonPath = FilePathsConfig.DICTIONARY_METADATA_JSON_PATH;

    private static final String updatedCatWordsPath = "static/dictionary/categorizedWords/updatedCategorizedWords.json";;
    private static final String lexemePath = FilePathsConfig.DICTIONARY_LEXEMES_REPOSITORY_JSON_PATH;
    private static final String cleanedTrainMessagesPath = FilePathsConfig.MODEL_ORIGINAL_CLEANED_TRAIN_MESSAGES_CSV_PATH;

    private static final String inputMessagesCsvPath = "static/datasets/joined/joined_messages_label.csv";

    public static void main(String[] args) throws IOException {
        long startUpdate = System.nanoTime();
        SpamDictionaryService service = new SpamDictionaryService();

        System.out.println("=== Etapa 2: Actualización del Diccionario desde archivo actualizado ===\n");

        // Inicialización
        System.out.println("[ STAGE 1 ] Inicializando diccionario desde archivo actualizado...\n");
        service.initializeDictionaryFromJson(updatedCatWordsPath, lexemePath, dictionaryMetadataJsonPath);
        service.displayCategorizedWordsInDictionary();

        // Guardar valores antes de actualizar
        int previousSpam = SpamDictionary.getInstance().getMetadata().getTotalSpam();
        int previousHam = SpamDictionary.getInstance().getMetadata().getTotalHam();

        // Actualizar con los nuevos mensajes
        System.out.println("\n[ STAGE 2 ] Procesando mensajes del dataset...\n");
        service.updateDictionaryFromCsvMessages(inputMessagesCsvPath);
        System.out.println("Actualización finalizada.\n");

        // Calcular diferencias
        int updatedSpam = SpamDictionary.getInstance().getMetadata().getTotalSpam();
        int updatedHam = SpamDictionary.getInstance().getMetadata().getTotalHam();

        int newSpam = updatedSpam - previousSpam;
        int newHam = updatedHam - previousHam;
        int newTotal = newSpam + newHam;

        // Registrar el nuevo dataset
        String datasetFileName = inputMessagesCsvPath.substring(inputMessagesCsvPath.lastIndexOf("/") + 1);
        String timestamp = java.time.LocalDateTime.now().toString();

        DatasetMetadata dataset = new DatasetMetadata(
                datasetFileName,
                newTotal,
                newHam,
                newSpam,
                timestamp
        );
        SpamDictionary.getInstance().getMetadata().addDataset(dataset);

        // Mostrar y exportar
        System.out.println("\n[ REPORT ] Diccionario actualizado:\n");
        service.displayCategorizedWordsInDictionary();

        System.out.println("\n[ REPORT ] Resumen del diccionario:\n");
        DictionarySummaryReport.displaySummaryReport(service);

        service.exportUpdatedCategorizedWords(baseOutputPath);
        service.exportMetadataJson(dictionaryMetadataJsonPath, baseOutputPath);

        long endUpdate = System.nanoTime();
        System.out.printf("Tiempo de actualización del diccionario: %.2f ms%n", (endUpdate - startUpdate) / 1_000_000.0);
    }
}
