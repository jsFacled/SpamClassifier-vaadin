package com.ml.spam.datasetProcessor.testersMain;

import com.ml.spam.datasetProcessor.services.DatasetGeneratorService;

public class DatasetGenerationMainTest {

    public static void main(String[] args) {
        try {
            System.out.println("=== TEST: Generación de Dataset con un mensaje ===");

            // Rutas
            String catWordsPath = "static/dictionary/categorizedWords/finalCategorizedWords/finalCategorizedWords.json";
            String lexemePath = "static/dictionary/lexemesRepository/structured_lexemes_repository.json";
            String metadataPath = "static/dictionary/categorizedWords/dictionary_metadata_2.json";
         //   String corpusPath = "static/datasets/un_mensaje_prueba_triplecomillas.txt";
            String corpusPath = "static/datasets/forDictionaryTest/un_mensaje_prueba_.txt";
            String outputPath = "dataset_test_1msg.csv";
            String lexemeMetadataPath = "static/dictionary/lexemesRepository/lexeme_words_detailed.json";
            // Formato del corpus y etiqueta si es TXT
            //String format = "txt";       // o "csv"
            String format = "csv";
            String labelIfTxt =null;
           // String labelIfTxt = "spam";// usado sólo si es TXT

            // Generación
            DatasetGeneratorService generator = new DatasetGeneratorService();
            generator.generateDatasetFromCorpus(
                    catWordsPath,
                    lexemePath,
                    metadataPath,
                    corpusPath,
                    format,
                    labelIfTxt,
                    outputPath,
                    lexemeMetadataPath);

            System.out.println("=== Dataset generado en: " + outputPath);

        } catch (Exception e) {
            System.err.println("[ERROR] Falló la generación de prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
