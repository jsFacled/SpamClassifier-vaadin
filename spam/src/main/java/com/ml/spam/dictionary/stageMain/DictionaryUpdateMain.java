package com.ml.spam.dictionary.stageMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.service.SpamDictionaryService;

import java.io.IOException;

public class DictionaryUpdateMain {

    private static final String filePath = FilePathsConfig.CATEGORIZED_WORDS_BASE_JSON_PATH;
    private static final String testMessagesFilePath = FilePathsConfig.TEST_CSV_DATA_PATH;


    public static void main(String[] args) throws IOException {
        SpamDictionaryService service = new SpamDictionaryService();



    }
}