package com.ml.spam.dictionary.testersDictionaryMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.reports.DictionarySummaryReport;
import com.ml.spam.dictionary.service.SpamDictionaryService;
import com.ml.spam.datasetProcessor.MessageProcessor;
import com.ml.spam.handlers.ResourcesHandler;
import com.ml.spam.dictionary.models.WordData;

import java.io.IOException;
import java.util.List;

public class InitializationAndTokenProcessingTestMain {

        private static final String catWordsPath = FilePathsConfig.CATEGORIZED_WORDS_FREQUENCIES_ZERO_JSON_PATH;
        private static final String accentPairsPath = FilePathsConfig.ACCENTED_PAIRS_JSON_PATH;
        private static final String  lexemePath=FilePathsConfig.LEXEMES_REPOSITORY_JSON_PATH;

        private static final String testMessagesFilePath = FilePathsConfig.TEST_CSV_DATA_PATH;
        private static final String testCatWords = "static/dictionary/test_catWord_zero.json";

        public static void main(String[] args) throws IOException {
            SpamDictionaryService service = new SpamDictionaryService();

            System.out.println("===  /  /   /   /   /   /   /   /   /   /   ===  Etapa 2: Actualización del Diccionario  === /  /   /   /   /   /   /   /   /   /   === \n");

/**
 ***  Solamente se utilizarían estos 2 mètodos en casos de algun error o duda ***

 //Mostrar el Map de Dictionary para chequear que estén las categorías vacías
 service.displayCategorizedWordsInDictionary();

 // Mostrar el diccionario persistido para testear el formato del json en consola
 service.displayJsonFileDictionary(filePath);
 */

            System.out.println("[ STAGE 1 ]* * * * * Inicializando dictionary   .    .   .  .  .  . . . . . . . . . \n");

            // Inicializar el diccionario desde el JSON
            service.initializeDictionaryFromJsonIfContainOnlyZeroFrequencies(testCatWords, accentPairsPath, lexemePath);

            // Mostrar los Map de SpamDictionary para chequear que se haya inicializado correctamente
            service.displayCategorizedWordsInDictionary();


            System.out.println("\n[ STAGE 2 ]* * * * * Solicitando Actualización    .    .   .  .  .  . . . . . . . . . \n");




            // Solicitar la actualización del diccionario al service
            service.updateDictionary(testMessagesFilePath);
            System.out.println(" *  *  *  *  *  //  //  //  //  //  //  // //  //  //  Actualización finalizada ! ! ! ! !\n");


            System.out.println("\n [ REPORT  ] * * * * * Reporte   .    .   .  .  .  . . . . . . . . . \n");
            // Mostrar SpamDictionary actualizado
            service.displayCategorizedWordsInDictionary();

            System.out.println("\n [ REPORT  ] * * * * * Reporte   .    .   .  .  .  . . . . . . . . . \n");
            // Mostrar el informe del diccionario actualizado
            DictionarySummaryReport.displaySummaryReport(service);
//--------------------------------------------------------------------------
        //------------------------------------------------------------------
            //-------------------------------------------------------
            System.out.println("\n **************** INICIANDO PRECESS MESSAGES ********************************************************************************");

        ResourcesHandler resourcesHandler = new ResourcesHandler();


        // Paso 2: Cargar las filas crudas desde el archivo CSV
        List<String[]> rawRows = resourcesHandler.loadCsvFile(testCatWords);
        System.out.println("[INFO] Filas crudas cargadas: " + rawRows.size());

        // Paso 3: Validar que las filas no estén vacías
        if (rawRows == null || rawRows.isEmpty()) {
            throw new IllegalArgumentException("El archivo CSV no contiene datos válidos.");
        }

        // Paso 4: Filtrar y validar filas
        List<String[]> validRows = rawRows.stream()
                .filter(row -> row.length == 2) // Validación simple: deben tener 2 columnas
                .toList();
        System.out.println("[INFO] Filas válidas después de filtrar: " + validRows.size());

        // Paso 5: Procesar filas válidas para obtener listas de WordData
        System.out.println("[INFO] Procesando filas válidas...");
        List<List<WordData>> processedWordData = MessageProcessor.processToWordData(validRows);
        System.out.println("[INFO] Número de listas de WordData generadas: " + processedWordData.size());

        // Imprimir los datos procesados
        for (int i = 0; i < processedWordData.size(); i++) {
            System.out.println("\n[Mensaje " + (i + 1) + "]");
            for (WordData wordData : processedWordData.get(i)) {
                System.out.println(wordData);
            }
        }
    }
}
