package com.ml.spam.datasetProcessor.testersMain;

import com.ml.spam.datasetProcessor.MessageProcessor;
import com.ml.spam.handlers.ResourcesHandler;
import com.ml.spam.dictionary.models.WordData;

import java.io.IOException;
import java.util.List;

public class TestProcessToWordDataMain {
    public static void main(String[] args) {
        try {
            // Paso 1: Cargar el archivo CSV
            ResourcesHandler resourcesHandler = new ResourcesHandler();
            String relativePath = "static/datasets/mensajes_pruebas.txt"; // Ruta relativa al archivo CSV
            List<String[]> rawRows = resourcesHandler.loadCsvFile(relativePath);

            // Paso 2: Invocar el método processToWordData
            List<List<WordData>> processedData = MessageProcessor.processToWordData(rawRows);

            // Paso 3: Imprimir los resultados para verificar
            System.out.println("Resultados del procesamiento:");
            for (List<WordData> wordDataList : processedData) {
                System.out.println("Mensaje procesado:");
                for (WordData wordData : wordDataList) {
                    System.out.println(wordData);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar el archivo CSV: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error en el procesamiento: " + e.getMessage());
        }
    }
}
