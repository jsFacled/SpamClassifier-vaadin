package com.ml.spam.datasetProcessor.testersMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.models.TokenType;
import com.ml.spam.dictionary.service.SpamDictionaryService;
import com.ml.spam.utils.TextUtils;

public class TestClassifyToken {


    private static final String catWordsPath = FilePathsConfig.CATEGORIZED_WORDS_FREQUENCIES_ZERO_JSON_PATH;
    private static final String accentPairsPath = FilePathsConfig.ACCENTED_PAIRS_JSON_PATH;
    private static final String lexemePath = FilePathsConfig.LEXEMES_REPOSITORY_JSON_PATH;


    public static void main(String[] args) {
        String mensaje = "a Z 2 fac@gmail.com Ahora! 35hs $100 !token--58//n @    8cm2 7cm3";

        try {
            var mensajeSplited =TextUtils.splitMessageAndLowercase(mensaje);

            // Inicializar el Service
            SpamDictionaryService dictionaryService = new SpamDictionaryService();
            System.out.println("=== Inicializando Diccionarios Completos ===");

            // Inicializar diccionarios desde JSON
            dictionaryService.initializeDictionaryFromJsonIfContainOnlyZeroFrequencies(catWordsPath, accentPairsPath, lexemePath);
            System.out.println("=== Diccionarios Inicializados Correctamente ===\n");

            System.out.println(" -  -  -  -  -  -  -  -  -  -  -  -  -  - Iniciando Test de Clasificación-  -  -  -  -  -  -  -  -  -  -  -  -  - \n");

            for (String token : mensajeSplited) {
                // Inicializar tokenType
                TokenType tokenType = TokenType.UNASSIGNED;
                // Subpaso 4.1: Procesar Clasificar el token
                tokenType = TextUtils.classifyToken(token);

                System.out.println("El token: "+token+" es de tipo: "+tokenType);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}//END
