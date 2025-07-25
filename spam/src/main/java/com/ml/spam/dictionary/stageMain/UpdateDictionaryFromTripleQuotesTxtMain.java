package com.ml.spam.dictionary.stageMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.models.DatasetMetadata;
import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.service.SpamDictionaryService;

public class UpdateDictionaryFromTripleQuotesTxtMain {

   // private static final String inputTxtFilePath = "static/datasets/correos-spam-fac-79m.txt";
    //private static final String inputTxtFilePath = "static/datasets/joined/joined_messages_triplecomillas_ham.txtxt";
    private static final String inputTxtFilePath = "static/datasets/joined/joined_messages_triplecomillas_spam.txt";

    private static final String baseOutputPath = FilePathsConfig.DICTIONARY_OUTPUT_BASE_JSON_PATH;

    private static final String updatedCatWordsPath = "static/dictionary/categorizedWords/updatedCategorizedWords_1.json";
    private static final String lexemePath = FilePathsConfig.DICTIONARY_LEXEMES_REPOSITORY_JSON_PATH;
    private static final String dictionaryMetadataJsonPath = FilePathsConfig.DICTIONARY_METADATA_JSON_PATH;
    private static final String dictionaryMetadataJsonPath2 = "F:\\JAVA GENERAL\\MACHINE LEARNING JAVA\\Código-ejemplos-intellij\\Clasificador Spam\\SpamClassifier-vaadin\\spam\\src\\main\\resources\\static\\dictionary\\categorizedWords\\dictionary_metadata_1.json";

    private static final String messageLabel = "spam";
    //private static final String messageLabel = "ham";


    public static void main(String[] args) {
        try {
            long startTime = System.nanoTime();
            System.out.println("=== INICIANDO PROCESO DE ACTUALIZACIÓN DEL DICCIONARIO DESDE TXT ===");

            SpamDictionaryService service = new SpamDictionaryService();

            System.out.println("[ STAGE 1 ] Inicializando el diccionario...");
            service.initializeDictionaryFromJson(updatedCatWordsPath, lexemePath, dictionaryMetadataJsonPath2);

            int previousSpam = SpamDictionary.getInstance().getMetadata().getTotalSpam();
            int previousHam = SpamDictionary.getInstance().getMetadata().getTotalHam();
            int previousTotal = previousSpam + previousHam;

            System.out.println("[ STAGE 2 ] Procesando archivo TXT...");
            service.updateDictionaryFromTxt(inputTxtFilePath, messageLabel);

            int newSpam = SpamDictionary.getInstance().getMetadata().getTotalSpam() - previousSpam;
            int newHam = SpamDictionary.getInstance().getMetadata().getTotalHam() - previousHam;
            int newTotal = newSpam + newHam;

            String datasetFileName = inputTxtFilePath.substring(inputTxtFilePath.lastIndexOf("/") + 1);
            String timestamp = java.time.LocalDateTime.now().toString();

            SpamDictionary.getInstance().getMetadata().addDataset(new DatasetMetadata(
                    datasetFileName,
                    newTotal,
                    newHam,
                    newSpam,
                    timestamp
            ));


            System.out.println("[ STAGE 3 ] Exportando...");
            service.exportUpdatedCategorizedWords(baseOutputPath);
            service.exportMetadataJson(dictionaryMetadataJsonPath, baseOutputPath);

            long endTime = System.nanoTime();
            System.out.printf("Proceso completado en %.2f ms%n", (endTime - startTime) / 1_000_000.0);

        } catch (Exception e) {
            System.err.println("Error durante la actualización del diccionario: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
