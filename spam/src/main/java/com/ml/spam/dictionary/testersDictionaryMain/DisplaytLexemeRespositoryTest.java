package com.ml.spam.dictionary.testersDictionaryMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.service.SpamDictionaryService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DisplaytLexemeRespositoryTest {

static String lexemePath = FilePathsConfig.DICTIONARY_LEXEMES_REPOSITORY_JSON_PATH;
static String lexemePathSinResolve = "spam/src/main/resources/static/dictionary/lexemes_repository.json";
static String catwords = FilePathsConfig.DICTIONARY_CATEGORIZED_WORDS_FREQUENCIES_ZERO_JSON_PATH;

        public static void main(String[] args) {
            System.out.println("=== Inicializando Diccionario Completo ===");

            // Crear instancia del servicio de diccionario
            SpamDictionaryService dictionaryService = new SpamDictionaryService();

            //Mostrar el lexemerepository.json en static/dictionary
            debugJsonContent(lexemePathSinResolve);


            // Inicializar el diccionario completo
            dictionaryService.initializeDictionaryFromJsonIfContainOnlyZeroFrequencies(
                    catwords, // Ruta al archivo de palabras categorizadas

                    lexemePath// Ruta al archivo del repositorio de lexemas
            );

            System.out.println("\n=== Diccionario Inicializado Correctamente ===");

            // Mostrar el contenido del lexemeRepository
            System.out.println("\n========= Contenido del Lexeme Repository =========");
            dictionaryService.displayLexemeRepository();

            System.out.println("\n=== Fin de la Prueba ===");
        }

    public static void debugJsonContent(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            System.out.println("Contenido del JSON:");
            System.out.println(content);
        } catch (IOException e) {
            System.err.println("Error leyendo el archivo JSON: " + e.getMessage());
        }
    }


}
