package com.ml.spam.handlers;

import com.ml.spam.utils.CsvUtils;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * ResourcesHandler:
 * Clase responsable de manejar la interacción con los recursos externos del sistema,
 * como archivos CSV o JSON, centralizando la carga de datos.
 *
 * Responsabilidades principales:
 * - Leer y cargar datos desde archivos, como CSV o JSON, en formatos básicos y utilizables
 *   (por ejemplo, JSONObject o List<String[]> para filas crudas).
 * - Transformar datos en estructuras básicas para que otras clases no se preocupen
 *   por los detalles de acceso o transformación inicial.
 * - Proveer métodos utilitarios para manejar errores de lectura y validaciones básicas.
 * - Aislar la lógica de acceso a datos del resto de la aplicación, promoviendo
 *   la separación de responsabilidades y facilitando pruebas independientes.
 *
 * Nota Importante:
 * - Uso en Entornos Empaquetados: Una vez empaquetado como JAR/WAR,
 *   escribir en src/main/resources no es posible porque estará dentro del archivo empaquetado.
 *   En producción, sería mejor escribir en un directorio externo configurable
 *   (por ejemplo, definido en el archivo application.properties o como argumento del programa).
 *
 * Ejemplo de uso:
 * List<String[]> rawRows = ResourcesHandler.loadCsvFile("path/al/archivo.csv");
 */


public class ResourcesHandler {
    private static final String RESOURCES_DIR = "spam/src/main/resources";

    // Resuelve rutas relativas al classpath
    private Path resolvePath(String relativePath) {
        return Paths.get(RESOURCES_DIR).resolve(relativePath).toAbsolutePath();
    }

    /**
     * Carga el contenido de un archivo desde los recursos del classpath como String.
     * @param resourcePath Ruta relativa dentro del classpath.
     * @return El contenido del archivo como String.
     * @throws RuntimeException Si el archivo no existe o no se puede leer.
     */
    public String loadResourceAsString(String resourcePath) {
        try {
            Path path = Paths.get(RESOURCES_DIR).resolve(resourcePath).toAbsolutePath();
            if (Files.notExists(path)) {
                throw new IOException("Archivo no encontrado en recursos: " + resourcePath);
            }

            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar archivo desde recursos: " + resourcePath, e);
        }
    }


    public JSONObject loadJson(String resourcePath) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new RuntimeException("Archivo no encontrado en el classpath: " + resourcePath);
            }
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return new JSONObject(content);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo JSON desde el classpath: " + resourcePath, e);
        }
    }


    // Método para guardar un archivo JSON
    public void saveJson(JSONObject jsonObject, String relativePath) {
        try {
            Path path = resolvePath(relativePath);
            Files.createDirectories(path.getParent()); // Crear directorios si no existen
            Files.writeString(path, jsonObject.toString(4), StandardCharsets.UTF_8); // Formato con indentación
            System.out.println("JSON guardado en: " + path);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo JSON: " + relativePath, e);
        }
    }


    /**
     * Carga un archivo CSV desde el sistema de archivos y devuelve una lista de filas crudas como List<String[]>.
     *
     * @param relativePath Ruta relativa al archivo CSV.
     * @return Una lista de arreglos de cadenas, donde cada arreglo representa una fila.
     * @throws IOException Si ocurre un error al leer el archivo.
     */
    public List<String[]> loadCsvFile(String relativePath) throws IOException {
        List<String[]> rawRows = new ArrayList<>();

        // Resuelve la ruta relativa
        Path absolutePath = resolvePath(relativePath);

        // Detectar el delimitador
        String delimiter = CsvUtils.detectDelimiter(absolutePath.toString());
        System.out.println("Delimitador detectado: \"" + delimiter + "\"");

        try (BufferedReader reader = new BufferedReader(new FileReader(absolutePath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Divide la línea usando el delimitador detectado
                String[] row = line.split(delimiter);
                rawRows.add(row);
            }
        }

        return rawRows;
    }
}

