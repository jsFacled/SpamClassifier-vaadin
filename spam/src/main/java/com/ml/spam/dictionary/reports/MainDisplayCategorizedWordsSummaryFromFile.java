package com.ml.spam.dictionary.reports;

import com.ml.spam.handlers.ResourcesHandler;

public class MainDisplayCategorizedWordsSummaryFromFile {

    public static void main(String[] args) {
        ResourcesHandler handler = new ResourcesHandler();
        String resourcePath = "static/dictionary/categorizedWords/updatedCategorizedWords_2.json";

        DictionarySummaryReport.displayCategorizedWordsSummaryFromFile(resourcePath, handler);
    }

}
