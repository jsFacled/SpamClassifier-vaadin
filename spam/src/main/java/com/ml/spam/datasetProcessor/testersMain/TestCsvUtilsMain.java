package com.ml.spam.datasetProcessor.testersMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.handlers.ResourcesHandler;
import com.ml.spam.utils.CsvUtils;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TestCsvUtilsMain {

    public static void main(String[] args) {
        System.out.println("=== Prueba directa con CsvUtils ===");
        testCsvUtilsDirectly();

        System.out.println("\n=== Prueba a través de ResourcesHandler ===");
        testResourcesHandler();
    }

    /**
     * Prueba directa de CsvUtils.
     */
    private static void testCsvUtilsDirectly() {
        String resourcePath = "static/datasets/mensajes_pruebas.txt";

        try {
            URL resourceUrl = TestCsvUtilsMain.class.getClassLoader().getResource(resourcePath);
            if (resourceUrl == null) {
                throw new RuntimeException("Archivo no encontrado en el classpath: " + resourcePath);
            }

            Path filePath = Paths.get(resourceUrl.toURI());

            // Llama al método para detectar y mostrar el delimitador
            CsvUtils.displayDelimiterInConsole(filePath.toString());
        } catch (URISyntaxException e) {
            System.err.println("Error al convertir el recurso a una ruta válida: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error al procesar el archivo: " + e.getMessage());
        }
    }

    /**
     * Prueba a través del ResourcesHandler para cargar el archivo y procesarlo.
     */
    private static void testResourcesHandler() {
        String relativePath = FilePathsConfig.TEST_CSV_DATA_PATH;

        try {
            ResourcesHandler handler = new ResourcesHandler();

            // Llama al método para cargar el CSV y obtener las filas
            List<String[]> rows = handler.loadCsvFile(relativePath);

            // Muestra el contenido del archivo procesado
            System.out.println("Contenido del archivo procesado:");
            for (String[] row : rows) {
                System.out.println(String.join(", ", row));
            }
        } catch (Exception e) {
            System.err.println("Error al procesar el archivo desde ResourcesHandler: " + e.getMessage());
        }
    }
}
