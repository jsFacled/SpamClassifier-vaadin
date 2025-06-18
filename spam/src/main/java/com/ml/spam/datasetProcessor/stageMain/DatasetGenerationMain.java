package com.ml.spam.datasetProcessor.stageMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.datasetProcessor.services.DatasetGeneratorService;
public class DatasetGenerationMain {

    private static final String updatedCatWordsPath = "static/dictionary/categorizedWords/finalCategorizedWords/finalCategorizedWords.json";
    private static final String lexemePath = FilePathsConfig.DICTIONARY_LEXEMES_REPOSITORY_JSON_PATH;
    private static final String metadataPath = FilePathsConfig.DICTIONARY_METADATA_JSON_PATH;
    private static final String lexemeMetadataPath = "static/dictionary/lexemesRepository/lexeme_words_detailed.json";

    public static void main(String[] args) {
        try {
            System.out.println("=== Etapa 4: Generación del dataset combinado ===");
            DatasetGeneratorService generator = new DatasetGeneratorService();

            // El archivo CSV ya reúne todos los mensajes normalizados y etiquetados
            String inputCsv = "static/datasets/joined/joined_messages_label.csv";
            String outputCsv = "static/mlDatasets/mix_combined_full_dataset.csv";

            generator.generateDatasetFromCorpus(
                    updatedCatWordsPath,
                    lexemePath,
                    metadataPath,
                    inputCsv,
                    "csv",
                    "null",
                    outputCsv,
                    lexemeMetadataPath
            );

            System.out.println("✅ Dataset generado en " + outputCsv);

        } catch (Exception e) {
            System.err.println("[ERROR] Falló alguna parte del proceso: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
