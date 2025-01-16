package com.ml.spam.dictionary.stageMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.service.SpamDictionaryService;

public class ReassignWordsMain {
    private static final String updatedCatWordsPath = "static/dictionary/updatedCategorizedWords_2.json";

    public static void main(String[] args) {
        try {
            // Inicia temporizador para calcular el tiempo de procesamiento
            long startTime = System.nanoTime();

            System.out.println("=== INICIANDO PROCESO DE REASIGNACIÓN DE PALABRAS ===");

            // Inicializa el servicio de diccionario
            SpamDictionaryService service = new SpamDictionaryService();

            System.out.println("[ STAGE 1 ] Inicializando el diccionario desde el archivo JSON...");
            service.initializeCategorizedWordsFromJsonPath(updatedCatWordsPath);

            System.out.println("[ STAGE 2 ] Reasignando palabras según sus frecuencias...");
            // Reasignar palabras basándose en el archivo JSON actualizado
            service.reassignWordsFromUpdatedJson();

            System.out.println("[ STAGE 3 ] Exportando diccionario reasignado...");
            // Exporta el diccionario reasignado
            service.exportUpdatedCategorizedWords(FilePathsConfig.BASE_OUTPUT_JSON_PATH);

            long endTime = System.nanoTime();
            System.out.printf("\nProceso completado en %.2f ms%n", (endTime - startTime) / 1_000_000.0);

        } catch (Exception e) {
            System.err.println("Error durante la reasignación de palabras: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
