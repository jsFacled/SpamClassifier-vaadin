package com.ml.spam.handlers;


import com.ml.spam.config.FilePathsConfig;

public class LoadStructuredLexemesJsonAndExportUniqueLexemesMain {
    public static void main(String[] args) {
        ResourcesHandler handler = new ResourcesHandler();
        String inputFilePath = FilePathsConfig.DICTIONARY_LEXEMES_REPOSITORY_JSON_PATH; // Ruta del archivo JSON de entrada
        String outputFilePath = FilePathsConfig.DICTIONARY_LEXEMES_CATEGORIES_JSON_PATH; // Ruta del archivo JSON de salida

        handler.loadStructuredLexemesJsonAndExportUniqueLexemes(inputFilePath, outputFilePath);
    }
}
