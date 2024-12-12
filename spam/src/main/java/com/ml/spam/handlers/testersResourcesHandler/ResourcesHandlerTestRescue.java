package com.ml.spam.handlers.testersResourcesHandler;

import com.ml.spam.handlers.ResourcesHandler;

import java.io.IOException;

public class ResourcesHandlerTestRescue {

    public static void main(String[] args) {
        // Instancia de ResourcesHandler
        ResourcesHandler resourcesHandler = new ResourcesHandler();

        // Ruta relativa al archivo CSV de prueba
        String testCsvPath = "static/datasets/mensajes_pruebas.txt";

        try {
            // Cargar y procesar el archivo CSV
            resourcesHandler.loadCsvFile(testCsvPath);
            // Nota: La salida se maneja completamente dentro de loadCsvFile y showSummary
        } catch (IOException e) {
            // Manejo de errores
            System.err.println("Error al procesar el archivo CSV: " + e.getMessage());
        }
    }
}
