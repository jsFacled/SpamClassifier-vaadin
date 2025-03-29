package com.ml.spam.dictionary.stageMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.service.SpamDictionaryService;

public class UpdateDictionaryFromTripleQuotesTxtMain {
    // Nombre del archivo TXT a procesar
    private static final String inputTxtFilePath = "static/datasets/correos-spam-fac.txt";
    // Nombre del archivo que se generará al exportar categorizedWords.
    // No sobreescribe. Si existe se incrementará un número.
    private static final String baseOutputPath = FilePathsConfig.BASE_OUTPUT_JSON_PATH;

    //Elementos del Dictionary
    private static final String updatedCatWordsPath = "static/dictionary/categorizedWords/updatedCategorizedWords_1.json";
   private static final String lexemePath = FilePathsConfig.LEXEMES_REPOSITORY_JSON_PATH;

    // Etiqueta para los mensajes (en este caso, spam)
    private static final String messageLabel = "spam";

    public static void main(String[] args) {
        try {
            // Inicia temporizador para calcular el tiempo de procesamiento
            long startTime = System.nanoTime();

            System.out.println("=== INICIANDO PROCESO DE ACTUALIZACIÓN DEL DICCIONARIO DESDE TXT ===");

            // Inicializa el servicio de diccionario
            SpamDictionaryService service = new SpamDictionaryService();

            System.out.println("[ STAGE 1 ] Inicializando el diccionario desde los datos previos...");
            service.initializeDictionaryFromJson(updatedCatWordsPath,lexemePath
            );

            // Mostrar el contenido inicial del diccionario
           //service.displayCategorizedWordsInDictionary();

            System.out.println("[ STAGE 2 ] Procesando mensajes desde archivo TXT...");
            // Actualiza el diccionario utilizando el archivo TXT con triples comillas
            service.updateDictionaryFromTxt(inputTxtFilePath, messageLabel);


            System.out.println("//////////////////////////////////////////////////////////////////////////////////////////////////////////////");
            //service.displayCategorizedWordsInDictionary();
            System.out.println("//////////////////////////////////////////////////////////////////////////////////////////////////////////////");



            System.out.println("[ STAGE 3 ] Exportando diccionario actualizado...");
            // Exporta el diccionario actualizado a un archivo JSON
            service.exportUpdatedCategorizedWords(baseOutputPath);


            System.out.println("//////////////////////////////////////////////////////////////////////////////////////////////////////////////");
            // Mostrar el contenido final del diccionario
           //service.displayCategorizedWordsInDictionary();
            System.out.println("//////////////////////////////////////////////////////////////////////////////////////////////////////////////");


            long endTime = System.nanoTime();
            System.out.printf("Proceso completado en %.2f ms%n", (endTime - startTime) / 1_000_000.0);

        } catch (Exception e) {
            System.err.println("Error durante la actualización del diccionario: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
