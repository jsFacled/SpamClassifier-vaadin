package com.ml.spam.dictionary;

import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.service.SpamDictionaryService;

public class DictionaryUpdateMain {
    public static void main(String[] args) {
        SpamDictionary dictionary = SpamDictionary.getInstance();
        SpamDictionaryService service = new SpamDictionaryService(dictionary);
        String filePath = "spam/src/main/resources/static/persisted_initialized_spam_vocabulary_frequenciesZero.json";

        /*
        * * * * * * Lectura del diccionario json inicializado a Zero para ver su contenido
        * */

        // Mostrar el diccionario persistido
        service.displayJsonPersistedDictionary(filePath);

        // Mostrar los Map de SpamDictionary


      /*
       String persistedPath = "path/to/persisted_initialized_spam_vocabulary_frequenciesZero.json";
        String csvPath = "path/to/dataset.csv";
        String updatedPath = "path/to/updated_spam_vocabulary.json";

        try {
            // Cargar el diccionario persistido
            SpamDictionary dictionary = SpamDictionary.getInstance();
            SpamDictionaryService service = new SpamDictionaryService(dictionary);

            try (InputStream inputStream = DictionaryUpdateMain.class.getClassLoader()
                    .getResourceAsStream(persistedPath)) {

                if (inputStream == null) {
                    throw new RuntimeException("Archivo JSON no encontrado.");
                }

                // Escoge si sobrescribir o fusionar
                service.loadFromJsonAndReplace(inputStream); // O usar mergeFromJson(inputStream)
            }

            // Procesar el archivo CSV y actualizar el diccionario
            CsvMessageProcessor processor = new CsvMessageProcessor(dictionary);
            processor.processCsv(csvPath);

            // Guardar el diccionario actualizado
            service.exportToJson(updatedPath);
            System.out.println("Diccionario actualizado y guardado en: " + updatedPath);
        } catch (Exception e) {
            System.err.println("Error en la actualización del diccionario: " + e.getMessage());
            e.printStackTrace();
        }

*/
    }

}
