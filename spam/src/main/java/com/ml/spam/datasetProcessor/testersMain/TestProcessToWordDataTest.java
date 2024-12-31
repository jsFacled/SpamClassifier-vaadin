package com.ml.spam.datasetProcessor.testersMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.models.WordData;
import com.ml.spam.dictionary.service.SpamDictionaryService;
import com.ml.spam.datasetProcessor.MessageProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TestProcessToWordDataTest {

    private static final String catWordsPath = FilePathsConfig.CATEGORIZED_WORDS_FREQUENCIES_ZERO_JSON_PATH;
    private static final String accentPairsPath = FilePathsConfig.ACCENTED_PAIRS_JSON_PATH;
    private static final String lexemePath = FilePathsConfig.LEXEMES_REPOSITORY_JSON_PATH;


    public static void main(String[] args) {
String[] mensaje = new String[]{
        "Comprá comprate 2999 35kg oferta! $ $100 24hs http://promo123.com \uD83D\uDE0A ya yá",
         "spam"
};
        String[] mensajeMixto = new String[]{
                "http://promo123.com abc123$ 50kg! promo@2024 23@abc# $10off!",
                "spam"
        };
        String[] mensajeTextSymbol = new String[]{
                "\"@ño niño$ piñ@ta sueño! casa123 año2024 día1\"\n",
                "spam"
        };
        String[] mensajeAccent = new String[]{
                "cuándo cuando cacatúa había servidor",
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
                    mensajeMixto
            );


            //Mostrar lexemerepository en dictionary
           // dictionaryService.displayLexemeRepository();

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
                System.out.println(Arrays.toString(rawRows.get(0)));
                for (WordData wordData : processedData.get(i)) {
                    System.out.println(" - " + wordData);
                }
                System.out.println("--------------------");
            }

            // Mostrar una vista general de todos los datos procesados
            System.out.println("\n[INFO] Resumen de datos procesados:");
            System.out.println("Total de mensajes procesados: " + processedData.size());


           // displayAccentPairs(dictionaryService.getAccentPairs());
        } catch (IllegalArgumentException e) {
            System.err.println("[ERROR] Datos de entrada inválidos: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[ERROR] Error inesperado durante el procesamiento: " + e.getMessage());
            e.printStackTrace();
        }


        System.out.println("\n[INFO] Fin del procesamiento de datos.");

    }
    private static void displayAccentPairs(Map<String, SpamDictionary.Pair> accentPairs) {
        System.out.println("\n[DEBUG] Contenido de accentPairs:");
        if (accentPairs == null || accentPairs.isEmpty()) {
            System.out.println("El mapa de accentPairs está vacío o no ha sido inicializado.");
        } else {
            for (Map.Entry<String, SpamDictionary.Pair> entry : accentPairs.entrySet()) {
                System.out.println(" - Palabra con acento: " + entry.getKey() + ", Sin acento: "
                        + entry.getValue().nonAccented() + ", Categoría: " + entry.getValue().category());
            }
        }
    }
}
