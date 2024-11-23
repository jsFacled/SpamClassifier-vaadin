package com.ml.spam.dictionary;

import com.ml.spam.datasetProcessor.CsvMessageProcessor;

public class DictionaryUpdateMain {
    public static void main(String[] args) {
        String persistedPath = "path/to/persisted_initialized_spam_vocabulary.json";
        String csvPath = "path/to/dataset.csv";
        String updatedPath = "path/to/updated_spam_vocabulary.json";

        try {
            // Cargar el diccionario persistido
            SpamDictionary dictionary = SpamDictionary.getInstance();
            SpamDictionaryService service = new SpamDictionaryService(dictionary);
            service.loadFromJson(persistedPath);

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
    }
}
