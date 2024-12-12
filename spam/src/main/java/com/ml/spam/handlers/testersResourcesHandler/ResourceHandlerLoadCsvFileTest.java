package com.ml.spam.handlers.testersResourcesHandler;

import com.ml.spam.handlers.ResourcesHandler;

import java.io.IOException;

/**
 * Clase: ResourcesHandlerTestRescue
 *
 * Descripción:
 * Este test verifica el comportamiento del método `loadCsvFile` de la clase `ResourcesHandler`.
 * - Carga un archivo CSV de prueba que contiene datos válidos e inválidos.
 * - Procesa las filas del archivo, identificando, validando y, si es posible, rescatando filas inválidas.
 * - Imprime en consola un resumen del procesamiento, que incluye:
 *   - Número total de filas válidas (incluyendo rescatadas).
 *   - Contenido de las filas procesadas.
 * - Maneja y registra cualquier excepción generada durante el procesamiento.
 *
 * Propósito:
 * - Garantizar que el método `loadCsvFile` maneje correctamente archivos CSV con filas válidas e inválidas,
 *   implementando las estrategias de rescate y mostrando el resumen final en consola.
 */

public class ResourceHandlerLoadCsvFileTest {

    public static void main(String[] args) {
        // Instancia de ResourcesHandler
        ResourcesHandler resourcesHandler = new ResourcesHandler();

        // Ruta relativa al archivo CSV de prueba
        String testCsvPath = "static/datasets/mensajes_pruebas.txt";
        String testMmensajesEspañol = "static/datasets/test-mensajesEspañol.csv";

        try {
            // Cargar y procesar el archivo CSV
            resourcesHandler.loadCsvFile(testMmensajesEspañol);
            // Nota: La salida se maneja completamente dentro de loadCsvFile y showSummary
        } catch (IOException e) {
            // Manejo de errores
            System.err.println("Error al procesar el archivo CSV: " + e.getMessage());
        }
    }
}
