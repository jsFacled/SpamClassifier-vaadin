package com.ml.spam.handlers;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Centralizar la carga de datos desde recursos como archivos JSON o CSV.
 * Transformar datos en formatos básicos (JSONObject, List<String>) para que
 * otras clases no se preocupen por los detalles de acceso o transformación inicial.
 */

public class ResourcesHandler {

    // Resuelve rutas relativas al classpath
    private Path resolvePath(String relativePath) {
        return Paths.get("src/main/resources").resolve(relativePath).toAbsolutePath();
    }


    /**
     * Carga el contenido de un archivo en recursos como String.
     * @param resourcePath Ruta relativa dentro de los recursos (classpath).
     * @return Contenido del archivo como String.
     * @throws RuntimeException si el archivo no se encuentra o hay un error de lectura.
     */
    public String loadResourceAsString(String resourcePath) {
        try (InputStream inputStream = ResourcesHandler.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Archivo no encontrado en recursos: " + resourcePath);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public JSONObject loadJson(String filePath) {
        try {
            String content = Files.readString(Paths.get(filePath), StandardCharsets.UTF_8);
            if (content.isBlank()) {
                throw new RuntimeException("El archivo JSON está vacío: " + filePath);
            }
            return new JSONObject(content);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo JSON: " + filePath, e);
        }
    }
    public void saveJson(JSONObject jsonObject, String filePath) {
        try {
            Files.createDirectories(Paths.get(filePath).getParent()); // Asegura que el directorio exista
            Files.writeString(Paths.get(filePath), jsonObject.toString(4), StandardCharsets.UTF_8);
            System.out.println("JSON guardado en: " + filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo JSON: " + filePath, e);
        }
    }


}
