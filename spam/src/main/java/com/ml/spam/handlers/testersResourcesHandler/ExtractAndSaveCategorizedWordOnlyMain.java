package com.ml.spam.handlers.testersResourcesHandler;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.handlers.ResourcesHandler;

public class ExtractAndSaveCategorizedWordOnlyMain {

    public static void main(String[] args) {
        ResourcesHandler handler = new ResourcesHandler();

        String inputFilePath = "static/dictionary/updatedCategorizedWords_2.json";
        String outputFilePath = "static/dictionary/updated_categorized_words_base_only.json";


        handler.extractAndSaveCategorizedWordOnly(
                inputFilePath,outputFilePath
        );
    }

}
