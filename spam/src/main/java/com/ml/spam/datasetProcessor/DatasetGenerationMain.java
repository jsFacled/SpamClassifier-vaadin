package com.ml.spam.datasetProcessor;


import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.datasetProcessor.services.DatasetGeneratorService;

public class DatasetGenerationMain {

    private static final String updatedCatWordsPath = "static/dictionary/categorizedWords/updatedCategorizedWords.json";
    private static final String lexemePath = FilePathsConfig.LEXEMES_REPOSITORY_JSON_PATH;
    private static final String metadataPath = FilePathsConfig.DICTIONARY_METADATA_JSON_PATH;
    private static final String inputCsvPath = FilePathsConfig.CLEANED_TRAIN_MESSAGES_CSV_PATH;
    private static final String outputDatasetPath = "static/dataset/generated_dataset.csv";

    public static void main(String[] args) {
        try {
            long start = System.nanoTime();
            System.out.println("=== Etapa 4: Generación del Dataset de Entrenamiento ===");

            DatasetGeneratorService generator = new DatasetGeneratorService();
            generator.generateDatasetFromCorpus(
                    updatedCatWordsPath,
                    lexemePath,
                    metadataPath,
                    inputCsvPath,
                    "csv",     // nuevo parámetro
                    null,      // label no necesario para csv
                    outputDatasetPath
            );

            long end = System.nanoTime();
            System.out.printf("Dataset generado en %.2f ms%n", (end - start) / 1_000_000.0);

        } catch (Exception e) {
            System.err.println("[ERROR] Falló la generación del dataset: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
