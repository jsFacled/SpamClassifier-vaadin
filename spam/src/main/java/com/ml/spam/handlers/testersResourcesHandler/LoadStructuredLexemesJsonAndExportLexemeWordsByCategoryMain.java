package com.ml.spam.handlers.testersResourcesHandler;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.handlers.ResourcesHandler;
import com.ml.spam.utils.ValidationResult;

public class LoadStructuredLexemesJsonAndExportLexemeWordsByCategoryMain {

    public static void main(String[] args) {
        ResourcesHandler handler = new ResourcesHandler();
        String inputFilePath = FilePathsConfig.LEXEMES_REPOSITORY_JSON_PATH;
        String outputFilePath = FilePathsConfig.LEXEME_WORDS_BY_CATEGORY_JSON_PATH;

        // Leer y validar el archivo JSON
        ValidationResult validationResult = handler.validateAndProcessStructuredLexemes(inputFilePath);

        if (validationResult.isValidStructure()) {
            System.out.println("[INFO] Estructura válida.");
            if (!validationResult.getDuplicateWords().isEmpty()) {
                System.out.println("[WARNING] Palabras duplicadas detectadas: " + validationResult.getDuplicateWords());
            }

            // Eliminar duplicados inválidos por CharSize
            handler.removeInvalidDuplicatesByCharSize(inputFilePath);

            // Exportar lexemes con palabras por categoría
           handler.loadStructuredLexemesJsonAndExportLexemesWithWords(inputFilePath, outputFilePath);
                        System.out.println("[INFO] Exportación completada.");
        } else {
            System.err.println("[ERROR] Estructura inválida en el archivo JSON.");
        }
    }
}
