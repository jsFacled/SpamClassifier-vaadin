package com.ml.spam.dictionary.stageMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.service.SpamDictionaryService;

public class InitializeFullDictionaryWordsFrequencyZeroMain {

    private static final String catWordsPath = FilePathsConfig.DICTIONARY_CATEGORIZED_WORDS_FREQUENCIES_ZERO_JSON_PATH;
    private static final String lexemePath = FilePathsConfig.DICTIONARY_LEXEMES_REPOSITORY_JSON_PATH;

    public static void main(String[] args) {
        // Crear instancia del servicio
        SpamDictionaryService service = new SpamDictionaryService();

        System.out.println("=== Inicializando Diccionarios Completos ===");

        // Inicializar diccionarios desde JSON
        service.initializeDictionaryFromJsonIfContainOnlyZeroFrequencies(catWordsPath, lexemePath);

        System.out.println("=== Diccionarios Inicializados Correctamente ===");

        // Mostrar el contenido inicializado
        service.displayFullReport();


    }


}
