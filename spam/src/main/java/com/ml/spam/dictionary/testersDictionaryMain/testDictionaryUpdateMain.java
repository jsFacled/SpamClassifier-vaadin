package com.ml.spam.dictionary.testersDictionaryMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.service.SpamDictionaryService;
import com.ml.spam.handlers.ResourcesHandler;

import java.io.IOException;
import java.util.List;

public class testDictionaryUpdateMain {

    private static final String testMessagesFilePath = FilePathsConfig.PRUEBA_CSV_DATA_PATH;

    public static void main(String[] args) throws IOException {
        SpamDictionaryService service = new SpamDictionaryService();

        System.out.println("===  /  /   /   /   /   /   /   /   /   /   ===  Etapa 2: Actualización del Diccionario  === /  /   /   /   /   /   /   /   /   /   === \n");

        // Validación explícita para verificar las filas crudas que recibe del resourcesHandler
        validateRawRows(testMessagesFilePath);

        // Solicitar la actualización del diccionario al service
        service.updateDictionary(testMessagesFilePath);
    }

    private static void validateRawRows(String csvFilePath) throws IOException {
        ResourcesHandler resourcesHandler = new ResourcesHandler();
        List<String[]> rawRows = resourcesHandler.loadCsvFile(csvFilePath);

        System.out.println("=== Verificando las filas crudas cargadas ===");
        if (rawRows.isEmpty()) {
            System.out.println("No se encontraron filas en el archivo CSV.");
        } else {
            System.out.println("Se encontraron " + rawRows.size() + " filas:");
            for (String[] row : rawRows) {
                System.out.println(String.join(", ", row));
            }
        }
    }

}