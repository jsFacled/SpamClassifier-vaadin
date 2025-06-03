package com.ml.spam.dictionary.stageMain.categorizedWordsMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.service.SpamDictionaryService;

public class saveCategorizedWordsOnlyMain {
    private static final String categorizedWordsPath = "static/dictionary/categorizedWords/updatedCategorizedWords_2.json";
    private static final String updatedCatWordsOnlyBasePathForExport = FilePathsConfig.CATEGORIZED_WORDS_ONLY_BASE_JSON_PATH;



    public static void main(String[] args) {

        SpamDictionaryService service = new SpamDictionaryService();

        service.initializeCategorizedWordsFromJsonPath(categorizedWordsPath);
 // service.displayCategorizedWordsInDictionary();

       service.exportCategorizedWordsWithoutFrequencies(
                updatedCatWordsOnlyBasePathForExport,
                "static/dictionary/omitted_words_report.txt"
        );

    }
}
