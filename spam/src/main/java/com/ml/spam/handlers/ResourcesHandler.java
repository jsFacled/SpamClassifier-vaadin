package com.ml.spam.handlers;

import com.ml.spam.utils.FileLoader;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Centralizar la carga de datos desde recursos como archivos JSON o CSV.
 * Transformar datos en formatos básicos (JSONObject, List<String>) para que
 * otras clases no se preocupen por los detalles de acceso o transformación inicial.
 */

public class ResourcesHandler {

    /**
     * Carga el contenido de un archivo en recursos como String.
     * @param resourcePath Ruta relativa dentro de los recursos (classpath).
     * @return Contenido del archivo como String.
     * @throws RuntimeException si el archivo no se encuentra o hay un error de lectura.
     */
    public String loadResourceAsString(String resourcePath) {
        try {
            return FileLoader.loadResourceAsString(resourcePath);
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar archivo como String desde recursos: " + resourcePath, e);
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
     * Persiste un mapa (Map) como un archivo JSON en el sistema de archivos.
     * @param data Map a persistir.
     * @param filePath Ruta del archivo donde se guardará el JSON.
     */
    public void saveJson(Map<String, Object> data, String filePath) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            Files.write(Paths.get(filePath), jsonObject.toString(4).getBytes());
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
