package com.ml.spam.dictionary.testersDictionaryMain;

import com.helger.commons.collection.map.MapEntry;
import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.models.LexemeRepositoryCategories;
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
    private static Map<LexemeRepositoryCategories, Map<String, Set<String>>> lexRepo = new HashMap<>();

    public static void main(String[] args) throws IOException {
        SpamDictionaryService service = new SpamDictionaryService();

        System.out.println("===  /  /   /   /   /   /   /   /   /   /   ===  Etapa 2: Actualización del Diccionario  === /  /   /   /   /   /   /   /   /   /   === \n");

        // Inicializar el diccionario desde el JSON
        service.initializeDictionaryFromJsonIfContainOnlyZeroFrequencies(prueba_catWords, accentPairsPath, lexemesPath);

lexRepo=service.getLexemesRepository();

        System.out.println("""
                * * * * * * *    * * * * * * * * * * * * *    * * * * * *  Aquí comienza el código para ejercitar  * * * * * * *    * * * * * *""");

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////







    }//end
}




