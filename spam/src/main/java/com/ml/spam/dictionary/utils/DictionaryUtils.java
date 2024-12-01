package com.ml.spam.dictionary.utils;

import com.ml.spam.dictionary.service.SpamDictionaryService;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Clase de utilidades para operaciones comunes con el diccionario.
 */
public class DictionaryUtils {

    /**
     * Crea el diccionario desde un archivo JSON con palabras sueltas.
     * @param service Instancia del SpamDictionaryService.
     * @param jsonPath Ruta del archivo JSON base.
     */
    public static void createDictionary(SpamDictionaryService service, String jsonPath) {
        try {
            // Leer y crear el diccionario desde el JSON base
            System.out.println("Leyendo archivo JSON desde: " + jsonPath);
            service.createDictionaryFromWords(jsonPath);

        } catch (Exception e) {
            System.err.println("Error durante la creación del diccionario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Muestra el contenido del diccionario en memoria.
     * @param service Instancia del SpamDictionaryService.
     */
    public static void displayDictionary(SpamDictionaryService service) {
        System.out.println("\n=== Contenido del Diccionario en Memoria ===");
        service.displayDictionary();
    }

    /**
     * Muestra el contenido de un archivo JSON persistido.
     * @param jsonPath Ruta del archivo JSON persistido.
     */
    public static void displayPersistedDictionary(String jsonPath) {
        try {
            // Leer y mostrar el contenido del archivo persistido
            System.out.println("\n=== Contenido del Archivo JSON Persistido ===");
            String content = Files.readString(Paths.get(jsonPath), StandardCharsets.UTF_8);
            System.out.println(content);

        } catch (Exception e) {
            System.err.println("Error al leer el archivo JSON persistido: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Exporta el diccionario inicializado a un archivo JSON persistido.
     * Este método es útil en etapas posteriores cuando se trabaja con un diccionario completo.
     * @param service Instancia del SpamDictionaryService.
     * @param exportPath Ruta del archivo de exportación.
     */
    public static void exportDictionary(SpamDictionaryService service, String exportPath) {
        try {
            // Asegurar que el directorio de exportación existe
            Files.createDirectories(Paths.get(exportPath).getParent());

            // Exportar el diccionario a un archivo JSON
            System.out.println("\nExportando el diccionario a: " + exportPath);
            service.exportToJson(exportPath);

            System.out.println("\n=== Exportación completada con éxito ===");

        } catch (Exception e) {
            System.err.println("Error durante la exportación del diccionario: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
