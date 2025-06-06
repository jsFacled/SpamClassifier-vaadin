package com.ml.spam.datasetProcessor;


import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.datasetProcessor.services.DatasetGeneratorService;

public class DatasetGenerationMain {

    private static final String updatedCatWordsPath = "static/dictionary/categorizedWords/finalCategorizedWords/finalCategorizedWords.json";
    private static final String lexemePath = FilePathsConfig.LEXEMES_REPOSITORY_JSON_PATH;
    private static final String metadataPath = FilePathsConfig.DICTIONARY_METADATA_JSON_PATH;
    private static final String lexemeMetadataPath = "static/dictionary/lexemesRepository/lexeme_words_detailed.json";


    //private static final String inputCsvPathIA ="static/datasets/mensajesInventadosIA/ham_triplecomillas_ia_varios.txt";
    private static final String inputCsvPathIA ="static/datasets/mensajesInventadosIA/Mensajes_varios_label_spam__ham.csv";
  // private static final String inputCsvPathIA ="static/datasets/mensajesInventadosIA/spam_triplecomillas_ia_varios.txt";

   //  private static final String outputDatasetPathIA = "generated_dataset_ia_ham.csv";
    //private static final String outputDatasetPathIA = "generated_dataset_ia_spam.csv";
    private static final String outputDatasetPathIA = "generated_dataset_ia_otros_spam_ham.csv";

    // private static final String inputCsvPath = FilePathsConfig.CLEANED_TRAIN_MESSAGES_CSV_PATH;
    //private static final String outputDatasetPath = "generated_dataset_train.csv";

     private static final String inputCsvPath = FilePathsConfig.TEST_MESSAGES_CSV_ESPAÑOL_DATA_PATH;
    private static final String outputDatasetPath = "generated_dataset_test.csv";

                    // ** paths para realizar pruebas **  //
            //private static final String inputCsvPath = FilePathsConfig.PRUEBA_CSV_DATA_PATH;
            //private static final String inputCsvPath = "static/datasets/forDictionaryTest/mensajes_test_lexeme_para_generateDataset.csv";
            //private static final String outputDatasetPath = "generated_dataset_pruebas.csv";

    private static final String datasetFormatType = "csv";
    private static final String labelType = "null";

/*
    private static final String outputDatasetPath = "generated_dataset_comillas_spam.csv";
    private static final String inputCsvPath = FilePathsConfig.CORREOS_SPAM_FAC_TXT_PATH;
*/
   // private static final String datasetFormatType = "txt";
    //private static final String labelType = "spam";
    //private static final String labelType = "ham";


    public static void main(String[] args) {
        try {
            long start = System.nanoTime();
            System.out.println("=== Etapa 4: Generación del Dataset de Entrenamiento ===");

            DatasetGeneratorService generator = new DatasetGeneratorService();
            generator.generateDatasetFromCorpus(
                    updatedCatWordsPath,
                    lexemePath,
                    metadataPath,
                    inputCsvPathIA,
                    datasetFormatType,     // indicar si es "txt" para triple comillas o "csv"
                    labelType,      // label no necesario para csv
                    outputDatasetPathIA,
                    lexemeMetadataPath
            );

            long end = System.nanoTime();
            System.out.printf("Dataset generado en %.2f ms%n", (end - start) / 1_000_000.0);

        } catch (Exception e) {
            System.err.println("[ERROR] Falló la generación del dataset: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
