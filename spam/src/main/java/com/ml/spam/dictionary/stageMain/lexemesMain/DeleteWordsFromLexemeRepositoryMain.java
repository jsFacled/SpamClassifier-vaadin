package com.ml.spam.dictionary.stageMain.lexemesMain;

import com.ml.spam.handlers.ResourcesHandler;
import com.ml.spam.dictionary.models.CharSize;
import com.ml.spam.utils.TextUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.*;

/**
 * Recibe json con lexema y palabras a borrar, modifica el repositorio y reporta.
 * {"lexDemoA":["b1"]}
 */
public class DeleteWordsFromLexemeRepositoryMain {

  //private static final String LEXEMES_REPOSITORY_PATH = FilePathsConfig.LEXEMES_REPOSITORY_JSON_PATH;
    private static final String LEXEMES_REPOSITORY_PATH = "static/dictionary/temporary/structured_lexemes_repository_test.json";

    private static final String WORDS_TO_DELETE_PATH = "static/dictionary/wordsToDeleteInLexemeFromLexemesRepository.json";
    private static final String OUTPUT_REPORT_PATH = "static/dictionary/temporary/deleted_words_from_lexemes_report.json";

    public static void main(String[] args) {
        ResourcesHandler handler = new ResourcesHandler();

        JSONObject repository = handler.loadJson(LEXEMES_REPOSITORY_PATH);
        JSONObject toDelete = handler.loadJson(WORDS_TO_DELETE_PATH);

        JSONObject report = new JSONObject();
        report.put("day", LocalDate.now().toString());
        JSONObject deleted = new JSONObject();

        for (String lexeme : toDelete.keySet()) {
            JSONArray words = toDelete.getJSONArray(lexeme);
            JSONArray deletedWords = new JSONArray();

            for (int i = 0; i < words.length(); i++) {
                String word = words.getString(i);
                CharSize charSize = TextUtils.determineCharSize(word);
                String charSizeKey = charSize.getJsonKey();

                if (repository.has(charSizeKey)) {
                    JSONObject block = repository.getJSONObject(charSizeKey);
                    if (block.has(lexeme)) {
                        JSONArray lexemeWords = block.getJSONArray(lexeme);
                        List<Object> currentWords = lexemeWords.toList();
                        if (currentWords.remove(word)) {
                            block.put(lexeme, new JSONArray(currentWords));
                            deletedWords.put(word);
                            System.out.println("[INFO] Eliminada palabra '" + word + "' de lexema '" + lexeme + "'");
                        }
                    }
                }
            }

            if (deletedWords.length() > 0) {
                deleted.put(lexeme, deletedWords);
            }
        }

        report.put("deleted_words", deleted);
        handler.saveJson(repository, LEXEMES_REPOSITORY_PATH);
        handler.saveJson(report, OUTPUT_REPORT_PATH);

        System.out.println("[INFO] Repositorio actualizado.");
        System.out.println("[INFO] Reporte generado en: " + OUTPUT_REPORT_PATH);
    }
}
