package com.ml.spam.dictionary.stageMain;

public class FinalDictionaryDecisionMain {
    public static void main(String[] args) {

        /*
        String updatedPath = "path/to/updated_spam_vocabulary.json";
        String finalPath = "path/to/final_spam_vocabulary.json";
        String newWordsExportPath = "path/to/new_words_to_review.json";

        try {
            // Cargar el diccionario actualizado
            SpamDictionary dictionary = SpamDictionary.getInstance();
            SpamDictionaryService service = new SpamDictionaryService(dictionary);
            service.loadFromJson(updatedPath);

            // Clasificar automáticamente palabras nuevas
            RuleEngine ruleEngine = new RuleEngine();
            ruleEngine.applyRules(dictionary);

            // Exportar palabras nuevas para revisión manual
            DatasetExporter exporter = new DatasetExporter();
            exporter.exportNewWords(dictionary, newWordsExportPath);

            // Consolidar y guardar el diccionario final
            service.exportToJson(finalPath);
            System.out.println("Diccionario final consolidado y guardado en: " + finalPath);
        } catch (Exception e) {
            System.err.println("Error en la decisión final del diccionario: " + e.getMessage());
            e.printStackTrace();
        }

        */
    }
}
