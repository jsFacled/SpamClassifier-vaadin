package com.ml.spam.dictionary.testersDictionaryMain;
import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.service.SpamDictionaryService;
import com.ml.spam.handlers.ResourcesHandler;
import com.ml.spam.utils.JsonUtils;

import org.json.JSONObject;

public class InitializeLexemesTest2 {

    public static void main(String[] args) {
        System.out.println("=== Test de initializeLexemes ===");

        // Crear instancia del servicio de diccionario
        SpamDictionaryService dictionaryService = new SpamDictionaryService();
        ResourcesHandler resourcesHandler = new ResourcesHandler();

        // Ruta al archivo lexemes_repository.json
        String lexemePath = FilePathsConfig.DICTIONARY_LEXEMES_REPOSITORY_JSON_PATH;

        try {
            System.out.println("[DEBUG] Iniciando prueba de initializeLexemes");
            System.out.println("[DEBUG] Cargando JSON desde: " + lexemePath);

            // Leer y validar el JSON manualmente antes de llamar al método
            JSONObject lexemeJson = resourcesHandler.loadJson(lexemePath);
            System.out.println("[DEBUG] JSON cargado con éxito:");
            System.out.println(lexemeJson.toString(2)); // Imprime el JSON formateado

            System.out.println("[DEBUG] Validando estructura del JSON...");
            JsonUtils.validateLexemeJsonStructure(lexemeJson);
            System.out.println("[DEBUG] Estructura del JSON validada correctamente.");

            System.out.println("[DEBUG] Llamando a initializeLexemes...");
            dictionaryService.initializeLexemes(lexemePath);

            System.out.println("[DEBUG] Verificando contenido del repositorio de lexemas:");
            dictionaryService.displayLexemeRepository();

            System.out.println("=== Test finalizado correctamente ===");
        } catch (Exception e) {
            System.err.println("[ERROR] Error durante el test de initializeLexemes: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
