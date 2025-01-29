package com.ml.spam.dictionary.reports;

import com.ml.spam.handlers.ResourcesHandler;

public class MainDisplayLexemesRepositorySummaryFromFile {


    public static void main(String[] args) {
        ResourcesHandler handler = new ResourcesHandler();
        String resourcePath = "static/dictionary/lexemesRepository/structured_lexemes_repository.json";

        DictionarySummaryReport.displayLexemesRepositorySummaryFromFile(resourcePath, handler);
    }

}
