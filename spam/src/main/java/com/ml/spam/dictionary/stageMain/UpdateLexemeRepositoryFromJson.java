package com.ml.spam.dictionary.stageMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.service.SpamDictionaryService;

public class UpdateLexemeRepositoryFromJson {

    private static final String lexemesRepositoryJsonPath = FilePathsConfig.LEXEMES_REPOSITORY_JSON_PATH;
    private static final String lexemeListToAddPath = "path/to/lexemesListToAdd.json";

    private static final String word ="nuevo1";
    private static final String lexeme ="lexnuevo";

    public static void main(String[] args) {

        SpamDictionaryService service = new SpamDictionaryService();
        service.addWordToLexemeRepository(lexemesRepositoryJsonPath, word, lexeme);

        service.updateLexemeRepositoryFromJsonList(lexemesRepositoryJsonPath,lexemeListToAddPath );
    }


}
