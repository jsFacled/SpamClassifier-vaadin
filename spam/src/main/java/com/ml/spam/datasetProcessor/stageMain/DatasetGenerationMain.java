package com.ml.spam.datasetProcessor.stageMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.datasetProcessor.services.DatasetGeneratorService;

public class DatasetGenerationMain {

    private static final String updatedCatWordsPath = "static/dictionary/categorizedWords/finalCategorizedWords/finalCategorizedWords.json";
    private static final String lexemePath = FilePathsConfig.LEXEMES_REPOSITORY_JSON_PATH;
    private static final String metadataPath = FilePathsConfig.DICTIONARY_METADATA_JSON_PATH;
    private static final String lexemeMetadataPath = "static/dictionary/lexemesRepository/lexeme_words_detailed.json";

    public static void main(String[] args) {
        try {
            System.out.println("=== Etapa 4: Generación de los 6 datasets ===");
            DatasetGeneratorService generator = new DatasetGeneratorService();

            generator.generateDatasetFromCorpus(updatedCatWordsPath, lexemePath, metadataPath, FilePathsConfig.CORREOS_SPAM_FAC_TXT_PATH, "txt", "spam", "generated_dataset_comillas_spam.csv", lexemeMetadataPath);
            generator.generateDatasetFromCorpus(updatedCatWordsPath, lexemePath, metadataPath, FilePathsConfig.IA_HAM_TXT_PATH, "txt", "ham", "generated_dataset_ia_ham.csv", lexemeMetadataPath);
            generator.generateDatasetFromCorpus(updatedCatWordsPath, lexemePath, metadataPath, FilePathsConfig.IA_SPAM_TXT_PATH, "txt", "spam", "generated_dataset_ia_spam.csv", lexemeMetadataPath);
            generator.generateDatasetFromCorpus(updatedCatWordsPath, lexemePath, metadataPath, FilePathsConfig.IA_OTROS_CSV_PATH, "csv", "null", "generated_dataset_ia_otros_spam_ham.csv", lexemeMetadataPath);
            generator.generateDatasetFromCorpus(updatedCatWordsPath, lexemePath, metadataPath, FilePathsConfig.TEST_MESSAGES_CSV_ESPAÑOL_DATA_PATH, "csv", "null", "generated_dataset_test.csv", lexemeMetadataPath);
            generator.generateDatasetFromCorpus(updatedCatWordsPath, lexemePath, metadataPath, FilePathsConfig.CLEANED_TRAIN_MESSAGES_CSV_PATH, "csv", "null", "generated_dataset_train.csv", lexemeMetadataPath);

            JoinDatasetsMain.main(null);

        } catch (Exception e) {
            System.err.println("[ERROR] Falló alguna parte del proceso: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
