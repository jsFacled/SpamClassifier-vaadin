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
     * Método: loadCsvFile
     *
     * Descripción:
     * Este método carga un archivo CSV desde una ruta relativa especificada,
     * procesa su contenido y retorna una lista de filas válidas como arreglos de cadenas.
     * El método detecta automáticamente el delimitador utilizado y omite la cabecera
     * si está presente. Cada línea es procesada y validada antes de ser agregada
     * a la lista de resultados.
     *
     * Pasos principales:
     * 1. Inicializar estructuras para almacenar las filas procesadas.
     * 2. Resolver la ruta absoluta del archivo a partir de la ruta relativa proporcionada.
     * 3. Abrir y leer el archivo línea por línea.
     * 4. Detectar el delimitador del archivo.
     * 5. Omitir la cabecera si está presente.
     * 6. Procesar las líneas restantes, validando y agregando filas válidas a la lista de resultados.
     *
     * @param relativePath Ruta relativa del archivo CSV a procesar.
     * @return Una lista de arreglos de cadenas (List<String[]>) que representa las filas válidas.
     * @throws IOException Si ocurre un error al leer el archivo o si está vacío.
     */
    public List<String[]> loadCsvFile(String relativePath) throws IOException {
        // Paso 1: Inicializar estructuras
        List<String[]> rawRows = new ArrayList<>();

        // Paso 2: Resolver ruta
        Path absolutePath = resolvePath(relativePath);
        System.out.println("Ruta absoluta resuelta: " + absolutePath);

        // Paso 3: Abrir archivo y leer
        try (BufferedReader reader = new BufferedReader(new FileReader(absolutePath.toFile()))) {
            String line;

            // Leer la primera línea
            line = reader.readLine();
            if (line == null) {
                throw new IOException("El archivo está vacío: " + absolutePath);
            }

            // Detectar delimitador
            String delimiter = CsvUtils.detectDelimiter(absolutePath.toString());
            System.out.println("Delimitador detectado: \"" + delimiter + "\"");

            // Validar y eliminar cabecera
            if (CsvUtils.isHeaderRow(line.split(delimiter))) {
                System.out.println("Cabecera detectada y eliminada: " + line);
                line = reader.readLine(); // Saltar la cabecera
            }

            // Leer y procesar las líneas restantes
            while (line != null) {
                System.out.println("Procesando línea: " + line);
                String[] columns = line.split(delimiter);

                // Normalizar las columnas eliminando espacios extra
                for (int i = 0; i < columns.length; i++) {
                    columns[i] = columns[i].trim();
                }

                // Validar y agregar fila
                if (columns.length == 2 && CsvUtils.isValidRow(columns)) {
                    rawRows.add(columns); // Agregar la fila válida a la lista
                    System.out.println("Fila válida agregada: " + String.join(" | ", columns));
                } else {
                    System.err.println("Fila inválida ignorada: " + line); // Reportar fila inválida
                }

                // Leer la siguiente línea
                line = reader.readLine();
            }
        }

        // Retornar las filas crudas
        return rawRows;
    }


}

