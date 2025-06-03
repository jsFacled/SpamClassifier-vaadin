package com.ml.spam.dictionary.stageMain.lexemesMain;


import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.handlers.ResourcesHandler;

public class updateDeletingWordsFromLexemesJsonListMain {
    public static void main(String[] args) {
        String lexemeRepositoryPath = FilePathsConfig.LEXEMES_REPOSITORY_JSON_PATH;
        String wordsToDeleteJsonPath = "static/dictionary/wordsToDeleteInLexemeFromLexemesRespository.json";

        ResourcesHandler handler = new ResourcesHandler();
        handler.updateDeletingWordsFromLexemesJsonList(lexemeRepositoryPath, wordsToDeleteJsonPath);
    }
}
