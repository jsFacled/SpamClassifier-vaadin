package com.ml.spam.dictionary.stageMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.utils.TextUtils;
import com.ml.spam.handlers.ResourcesHandler;
import com.ml.spam.dictionary.models.CharSize;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * BuildStructuredLexemeRepositoryMain
 *
 * <p>Este programa transforma un archivo JSON plano de lexemas en una versión estructurada
 * organizada por tamaño de palabra (CharSize). Se utiliza normalmente después de una
 * revisión humana del repositorio plano.</p>
 *
 * <p>Convierte un archivo como:</p>
 *
 * <pre>
 * {
 *   "lexemaEjemplo": ["palabra1", "palabra2"]
 * }
 * </pre>
 *
 * <p>En un archivo estructurado como:</p>
 *
 * <pre>
 * {
 *   "threeChars": {
 *     "lexemaEjemplo": ["uno", "dos"]
 *   },
 *   "eightChars": {
 *     "lexemaEjemplo": ["palabra"]
 *   }
 * }
 * </pre>
 *
 * <p>Este proceso es necesario para que el sistema de tokenización y clasificación
 * pueda usar correctamente el repositorio.</p>
 *
 * <p><b>Entradas:</b> JSON plano en {@code lexeme_words_flat.json}</p>
 * <p><b>Salidas:</b> Repositorio estructurado guardado en {@code structured_lexemes_repository.json}</p>
 *
 * <p>Las rutas de entrada/salida están definidas como constantes dentro de la clase.</p>
 */

public class BuildStructuredLexemeRepositoryMain {

    private static final String flatLexemesPath = "static/dictionary/flat_lexemes.json"; // entrada
    private static final String outputStructuredPath = "static/dictionary/structured_lexemes_repository.json"; // salida

    public static void main(String[] args) {
        ResourcesHandler handler = new ResourcesHandler();
        JSONObject flatLexemes = handler.loadJson(flatLexemesPath);

        JSONObject structuredRepo = new JSONObject();

        for (String lexeme : flatLexemes.keySet()) {
            JSONArray wordsArray = flatLexemes.getJSONArray(lexeme);
            for (int i = 0; i < wordsArray.length(); i++) {
                String word = wordsArray.getString(i);

                CharSize charSize = TextUtils.determineCharSize(word);
                String charSizeKey = charSize.getJsonKey();

                // Crear bloque de CharSize si no existe
                if (!structuredRepo.has(charSizeKey)) {
                    structuredRepo.put(charSizeKey, new JSONObject());
                }

                JSONObject block = structuredRepo.getJSONObject(charSizeKey);

                // Crear lista del lexema si no existe
                if (!block.has(lexeme)) {
                    block.put(lexeme, new JSONArray());
                }

                JSONArray existingWords = block.getJSONArray(lexeme);

                // Agregar palabra si no está
                if (!existingWords.toList().contains(word)) {
                    existingWords.put(word);
                }
            }
        }

        handler.saveJson(structuredRepo, outputStructuredPath);
        System.out.println("[INFO] Repositorio estructurado generado correctamente.");
    }
}
