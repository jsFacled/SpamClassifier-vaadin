package com.ml.spam.datasetProcessor;
import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.service.SpamDictionaryService;

public class TestSpamProcessor {

    /*

    public static void main(String[] args) {
        // Inicializar el diccionario y el servicio
        SpamDictionary dictionary = SpamDictionary.getInstance();
        SpamDictionaryService dictionaryService = new SpamDictionaryService(dictionary);

        // Inicializar el procesador
        CsvMessageProcessor processor = new CsvMessageProcessor(dictionaryService);

        try {
            System.out.println("--->>>>   Iniciando TestSpamProcessor    <<<<--------");

            // Ruta del archivo de prueba
            String filePath = "spam/src/main/resources/static/mensajes_pruebas.txt";

            // Ejecutar el procesamiento
            processor.processCsvAndUpdateDictionary(filePath);

            // Mostrar el resultado
            System.out.println("--->>>> Diccionario actualizado: <<<<--------");
            dictionaryService.displayDictionary();

            System.out.println("\nPalabras nuevas detectadas:");
            dictionary.getNewWords().forEach((word, freq) ->
                    System.out.println(word + " -> " + freq)
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     */
}
