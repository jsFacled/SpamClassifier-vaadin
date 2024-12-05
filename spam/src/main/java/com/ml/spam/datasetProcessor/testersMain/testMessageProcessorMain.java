package com.ml.spam.datasetProcessor.testersMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.datasetProcessor.MessageProcessor;
import com.ml.spam.datasetProcessor.models.ProcessedMessage;
import com.ml.spam.handlers.ResourcesHandler;

import java.io.IOException;
import java.util.List;

public class testMessageProcessorMain {

    public static void main(String[] args) {
        String testMessagesFilePath = FilePathsConfig.TEST_CSV_DATA_PATH;
        ResourcesHandler resourcesHandler = new ResourcesHandler();

        try {
            System.out.println("=== Probando MessageProcessor ===");

            // Cargar las filas crudas desde el archivo CSV
            List<String[]> rawRows = resourcesHandler.loadCsvFile(testMessagesFilePath);

            // Procesar las filas crudas a objetos LabeledMessage
            List<ProcessedMessage> processedMessages = MessageProcessor.simpleProcess(rawRows);

            // Mostrar el resultado
            System.out.println("=== Resultado del procesamiento ===");
            processedMessages.forEach(System.out::println);

        } catch (IOException e) {
            System.err.println("Error al cargar el archivo CSV: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error al procesar las filas crudas: " + e.getMessage());
        }
    }
}
