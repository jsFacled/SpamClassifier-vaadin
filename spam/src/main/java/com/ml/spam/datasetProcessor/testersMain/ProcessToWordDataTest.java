package com.ml.spam.datasetProcessor.testersMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.models.WordData;
import com.ml.spam.dictionary.service.SpamDictionaryService;
import com.ml.spam.datasetProcessor.MessageProcessor;

import java.util.ArrayList;
import java.util.List;

public class ProcessToWordDataTest {

    private static final String catWordsPath = FilePathsConfig.CATEGORIZED_WORDS_FREQUENCIES_ZERO_JSON_PATH;
    private static final String accentPairsPath = FilePathsConfig.ACCENTED_PAIRS_JSON_PATH;
    private static final String lexemePath = FilePathsConfig.LEXEMES_REPOSITORY_JSON_PATH;


    public static void main(String[] args) {
String[] mensaje = new String[]{
        "Compra 2999 35kg arroz oferta! $ $100 24hs es buen precio ahorrar y es urgente! http://promo123.com \uD83D\uDE0A cómpralo ya yá",
         "spam"
};
        String[] mensaje2 = new String[]{
                "9 $ a z hola",
                "spam"
        };

        try {
            // Inicializar el Service
            SpamDictionaryService dictionaryService = new SpamDictionaryService();
            System.out.println("=== Inicializando Diccionarios Completos ===");

            // Inicializar diccionarios desde JSON
            dictionaryService.initializeDictionaryFromJsonIfContainOnlyZeroFrequencies(catWordsPath, accentPairsPath, lexemePath);
            System.out.println("=== Diccionarios Inicializados Correctamente ===");

            // Simulación de datos crudos (rawRows) en formato [mensaje, etiqueta]
            List<String[]> rawRows = new ArrayList<>();
            rawRows.add(
                    mensaje2
                    //new String[]{
                   // "Compra 35kg de arroz en oferta! $100 24hs es un buen precio para ahorrar y es urgente! http://promo123.com \uD83D\uDE0A cómpralo ya yá",
                   // "spam"
                    //    }
        );



            //Mostrar lexemerepository en dictionary
            dictionaryService.displayLexemeRepository();

            // Mostrar datos iniciales para referencia
            System.out.println("\n[INFO Inicio de lextura de Mensajes ] * * * Datos crudos iniciales:");
            for (String[] row : rawRows) {
                System.out.println("[Mensaje: " + row[0] + ", Etiqueta: " + row[1] + "]");
            }

            System.out.println("\n[INFO] Iniciando procesamiento de datos...");

            // Procesar las filas crudas con acceso a los diccionarios
            List<List<WordData>> processedData = MessageProcessor.processToWordData(
                    rawRows,
                    dictionaryService.getAccentPairs(),
                    dictionaryService.getLexemesRepository()
            );

            // Mostrar los resultados procesados
            System.out.println("\n[INFO] Resultados del procesamiento:");
            for (int i = 0; i < processedData.size(); i++) {
                System.out.println("Nuevo mensaje procesado (" + (i + 1) + "):");
                for (WordData wordData : processedData.get(i)) {
                    System.out.println(" - " + wordData);
                }
                System.out.println("--------------------");
            }

            // Mostrar una vista general de todos los datos procesados
            System.out.println("\n[INFO] Resumen de datos procesados:");
            System.out.println("Total de mensajes procesados: " + processedData.size());

        } catch (IllegalArgumentException e) {
            System.err.println("[ERROR] Datos de entrada inválidos: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[ERROR] Error inesperado durante el procesamiento: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n[INFO] Fin del procesamiento de datos.");
    }
}
