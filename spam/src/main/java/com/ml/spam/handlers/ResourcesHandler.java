package com.ml.spam.handlers;

import com.ml.spam.utils.FileLoader;
import com.ml.spam.utils.FileWriter;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

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


    /**
     * Carga un archivo JSON desde los recursos (classpath) y lo devuelve como JSONObject.
     * @param resourcePath Ruta relativa dentro de los recursos (classpath).
     * @return JSONObject representando el contenido del archivo JSON.
     */
    public JSONObject loadJson(String resourcePath) {
        try (InputStream inputStream = FileLoader.loadResourceAsStream(resourcePath)) {
            String jsonContent = FileLoader.readFile(inputStream);
            return new JSONObject(jsonContent);
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar JSON desde los recursos: " + resourcePath, e);
        }
    }

    /**
     * Guarda un objeto JSON en un archivo.
     *
     * @param jsonObject Objeto JSON que se desea guardar.
     * @param filePath   Ruta del archivo donde se guardará el JSON.
     */
    public void saveJson(JSONObject jsonObject, String filePath) {
        Path path = Paths.get(filePath);

        // Asegurar que el directorio padre exista
        if (path.getParent() != null && Files.notExists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }

        // Escribir el contenido utilizando BufferedWriter
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(content);
            System.out.println("Diccionario exportado a JSON en: " + filePath);
        }
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar JSON en el archivo: " + filePath, e);
        }
    }

    /**
     * Carga un archivo CSV desde los recursos (classpath) y devuelve su contenido como una lista de líneas.
     * @param resourcePath Ruta relativa dentro de los recursos (classpath).
     * @return Lista de líneas representando el contenido del CSV.
     */
    public List<String> loadCsv(String resourcePath) {
        try (InputStream inputStream = FileLoader.loadResourceAsStream(resourcePath)) {
            return Files.readAllLines(Paths.get(resourcePath));
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar CSV desde los recursos: " + resourcePath, e);
        }
    }
}
