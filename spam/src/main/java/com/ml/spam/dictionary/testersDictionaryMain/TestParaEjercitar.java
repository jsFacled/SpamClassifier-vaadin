package com.ml.spam.dictionary.testersDictionaryMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.models.CharSize;
import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.models.WordCategory;
import com.ml.spam.dictionary.models.WordData;
import com.ml.spam.dictionary.service.SpamDictionaryService;

import java.io.IOException;
import java.util.*;

public class TestParaEjercitar {
    private static final String lexemesPath = FilePathsConfig.LEXEMES_REPOSITORY_JSON_PATH;
    private static final String accentPairsPath = FilePathsConfig.ACCENTED_PAIRS_JSON_PATH;
    private static final String prueba_catWords = "static/dictionary/test_catWord_zero.json";

    private static final Map<WordCategory, Map<String, WordData>> categorizedWords = new HashMap<>();
    private static final Map<String, SpamDictionary.Pair> accentPairs = new HashMap<>();
    private static Map<CharSize, Map<String, Set<String>>> lexRepo = new HashMap<>();

    public static void main(String[] args) throws IOException {

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////







    }//end
}




