package com.ml.spam.dictionary.stageMain.lexemesMain;


import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.handlers.ResourcesHandler;


public class updateDeletingLexemesFromJsonListMain {
    public static void main(String[] args) {
        String lexemeRepositoryPath = FilePathsConfig.DICTIONARY_LEXEMES_REPOSITORY_JSON_PATH;
        String lexemeRepositoryPathTest = "static/dictionary/temporary/structured_lexemes_repository_test.json";

        String lexemesToDeleteJsonPath = "static/dictionary/lexemesToDelete.json";

        ResourcesHandler handler = new ResourcesHandler();
        handler.updateDeletingLexemesFromJsonList(lexemeRepositoryPath, lexemesToDeleteJsonPath);
       // handler.updateDeletingLexemesFromJsonList(lexemeRepositoryPathTest, lexemesToDeleteJsonPath);


    }
}
