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
    private static final String lexemePath = FilePathsConfig.LEXEMES_REPOSITORY_JSON_PATH;

    private static final String testMessagesFilePath = FilePathsConfig.PRUEBA_CSV_DATA_PATH;

    public static void main(String[] args) {
        try {
            SpamDictionaryService service = new SpamDictionaryService();
            ResourcesHandler resourcesHandler = new ResourcesHandler();

            // Etapa 1: Inicializar el diccionario
            System.out.println("[ STAGE 1 ] Inicializando diccionario desde JSON...");
            service.initializeDictionaryFromJsonIfContainOnlyZeroFrequencies(catWordsPath, accentPairsPath, lexemePath);

            // Mostrar el estado inicial del diccionario
            System.out.println("[INFO] Diccionario inicializado:");
            service.displayCategorizedWordsInDictionary();

            // Etapa 2: Cargar las filas crudas desde el CSV
            System.out.println("\n[ STAGE 2 ] Cargando mensajes desde archivo CSV...");
            List<String[]> rawRows = resourcesHandler.loadCsvFile(testMessagesFilePath);

            // Validar y procesar las filas crudas
            if (rawRows == null || rawRows.isEmpty()) {
                throw new IllegalArgumentException("[ERROR] El archivo CSV no contiene datos válidos.");
            }
            System.out.println("[INFO] Filas crudas cargadas: " + rawRows.size());

            // Filtrar filas válidas
            List<String[]> validRows = rawRows.stream()
                    .filter(row -> row.length == 2 && !row[0].isBlank() && !row[1].isBlank())
                    .toList();

            System.out.println("[INFO] Filas válidas después del filtrado: " + validRows.size());
            if (validRows.isEmpty()) {
                throw new IllegalArgumentException("[ERROR] No hay filas válidas después del filtrado.");
            }

            // Etapa 3: Tokenizar y procesar mensajes
            System.out.println("\n[ STAGE 3 ] Tokenizando y procesando mensajes...");
            List<List<WordData>> processedWordData = MessageProcessor.processToWordData(validRows,
                    service.getAccentPairs(), service.getLexemesRepository());
            System.out.println("[INFO] Número de listas de WordData generadas: " + processedWordData.size());

            // Etapa 4: Actualizar el diccionario
            System.out.println("\n[ STAGE 4 ] Actualizando el diccionario con los tokens procesados...");
            service.updateDictionaryFromProcessedWordData(processedWordData);

            // Mostrar el reporte del diccionario
            System.out.println("\n[INFO] Generando reporte del diccionario actualizado...");
            DictionarySummaryReport.displayFullReport(service);

        } catch (IOException e) {
            System.err.println("[ERROR] Error al manejar archivos: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("[ERROR] Error en datos de entrada: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[ERROR] Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
