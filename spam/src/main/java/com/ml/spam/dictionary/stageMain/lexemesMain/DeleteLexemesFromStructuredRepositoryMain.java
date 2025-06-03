package com.ml.spam.dictionary.stageMain.lexemesMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.handlers.ResourcesHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

public class DeleteLexemesFromStructuredRepositoryMain {

    private static final String STRUCTURED_REPO_PATH = FilePathsConfig.LEXEMES_REPOSITORY_JSON_PATH;
    private static final String LEXEMES_TO_DELETE_PATH = "static/dictionary/temporary/lexemes_to_eliminate.json";
    private static final String OUTPUT_REPORT_PATH = "static/dictionary/temporary/deleted_lexemes_report.json";

    public static void main(String[] args) {

        ResourcesHandler handler = new ResourcesHandler();

        // Cargar repositorio estructurado
        JSONObject structuredRepo = handler.loadJson(STRUCTURED_REPO_PATH);

        // Cargar lista de lexemas a eliminar
        Set<String> lexemesToDelete = loadLexemeList(LEXEMES_TO_DELETE_PATH);

        // Reporte de lexemas eliminados
        JSONObject deletedLexemesReport = new JSONObject();
        deletedLexemesReport.put("day", LocalDate.now().toString());

        JSONObject deletedDetails = new JSONObject();

        for (String charSizeKey : structuredRepo.keySet()) {
            JSONObject block = structuredRepo.getJSONObject(charSizeKey);

            for (String lexeme : new HashSet<>(block.keySet())) {
                if (lexemesToDelete.contains(lexeme)) {
                    JSONArray deletedWords = block.getJSONArray(lexeme);
                    deletedDetails.put(lexeme, deletedWords);
                    block.remove(lexeme);
                    System.out.println("[INFO] Lexema eliminado: " + lexeme + " del bloque " + charSizeKey);
                }
            }
        }

        deletedLexemesReport.put("deleted_lexemes", deletedDetails);

        // Guardar el nuevo repositorio actualizado
        handler.saveJson(structuredRepo, STRUCTURED_REPO_PATH);

        // Guardar reporte de eliminación
        handler.saveJson(deletedLexemesReport, OUTPUT_REPORT_PATH);

        System.out.println("[INFO] Eliminación de lexemas completada. Reporte generado en: " + OUTPUT_REPORT_PATH);
    }

    private static Set<String> loadLexemeList(String path) {
        try {
            String content = Files.readString(Paths.get(path), StandardCharsets.UTF_8);
            // Formato: ["lexuno", "lexdos", ...]
            JSONArray array = new JSONArray(content);
            Set<String> result = new HashSet<>();
            for (int i = 0; i < array.length(); i++) {
                result.add(array.getString(i));
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error al leer el archivo de lexemas a eliminar: " + path, e);
        }
    }
}
