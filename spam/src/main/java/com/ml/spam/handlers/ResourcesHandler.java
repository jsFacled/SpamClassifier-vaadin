package com.ml.spam.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ml.spam.utils.CsvUtils;
import com.ml.spam.utils.JsonUtils;
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
     * Métod: loadCsvFile
     *
     * Descripción:
     * Este métod carga un archivo CSV desde una ruta relativa especificada,
     * procesa su contenido y retorna una lista de filas válidas como arreglos de cadenas.
     * El métod detecta automáticamente el delimitador utilizado y omite la cabecera
     * si está presente. Cada línea es procesada y validada antes de ser agregada
     * a la lista de resultados. Las filas inválidas son registradas y, si es posible,
     * se intenta rescatarlas.
     *
     * Pasos principales:
     * 1. Inicializar estructuras para almacenar las filas procesadas.
     * 2. Resolver la ruta absoluta del archivo a partir de la ruta relativa proporcionada.
     * 3. Abrir y leer el archivo línea por línea.
     * 4. Detectar el delimitador del archivo.
     * 5. Omitir la cabecera si está presente.
     * 6. Procesar las líneas restantes, validando y agregando filas válidas a la lista de resultados.
     * 7. Intentar rescatar filas inválidas, si es posible.
     *
     * @param relativePath Ruta relativa del archivo CSV a procesar.
     * @return Una lista de arreglos de cadenas (List<String[]>) que representa las filas válidas.
     * @throws IOException Si ocurre un error al leer el archivo o si está vacío.
     */
    public List<String[]> loadCsvFile(String relativePath) throws IOException {
        // Paso 1: Inicializar estructuras
        List<String[]> rawRows = new ArrayList<>();
        List<String> invalidRows = new ArrayList<>(); // Lista para filas inválidas ignoradas

        // Paso 2: Resolver ruta
        Path absolutePath = resolvePath(relativePath);
        System.out.println("\n [Info LoadCsvFile] >> Ruta absoluta resuelta: " + absolutePath + "\n");

        // Paso 3: Abrir archivo y leer
        try (BufferedReader reader = new BufferedReader(new FileReader(absolutePath.toFile()))) {
            // Leer la primera línea
            String line = reader.readLine();
            if (line == null) {
                throw new IOException("El archivo está vacío: " + absolutePath);
            }

            // Detectar delimitador
            String delimiter = CsvUtils.detectDelimiter(absolutePath.toString());
            System.out.println("[Info LoadCsvFile] >> Delimitador detectado: \"" + delimiter + "\"");

            // Validar y eliminar cabecera
            if (CsvUtils.isHeaderRow(line.split(delimiter))) {
                System.out.println("[Info LoadCsvFile] >> Cabecera detectada y eliminada: " + line);
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
                    System.out.println("[Info LoadCsvFile] >> Fila válida agregada: " + String.join(" | ", columns));
                } else {

                    // Intentar rescatar la fila
                    String[] rescuedColumns = attemptRescue(line, delimiter);
                    System.err.println("[Info LoadCsvFile] >> Fila inválida detectada, intentando rescatar: " + "^" + line + "^" + ". . . . . . . . . . . \n");


                    if (rescuedColumns != null && rescuedColumns.length == 2 && CsvUtils.isValidRow(rescuedColumns)) {
                        rawRows.add(rescuedColumns); // Agregar la fila rescatada si es válida

                        System.out.println(" [Info LoadCsvFile] >> Fila rescatada y agregada: " + String.join(" | ", rescuedColumns));
                    } else {
                        invalidRows.add(line); // Agregar a la lista de filas inválidas
                        System.err.println("Fila inválida ignorada: " + line); // Ignorar si no se puede rescatar
                    }
                }

                // Leer la siguiente línea
                line = reader.readLine();
            }
        }

        // Mostrar el resumen por consola
        showSummary(rawRows, invalidRows);

        // Retornar las filas crudas
        return rawRows;
    }

    /**
     * Intenta rescatar una fila marcada como inválida.
     *
     * @param line      La línea original del archivo CSV.
     * @param delimiter El delimitador detectado.
     * @return Un arreglo de cadenas con los datos rescatados, o null si no se puede rescatar.
     */
    private String[] attemptRescue(String line, String delimiter) {
        int lastDelimiterIndex = line.lastIndexOf(delimiter);
        if (lastDelimiterIndex != -1) {
            String message = line.substring(0, lastDelimiterIndex).trim();
            String label = line.substring(lastDelimiterIndex + 1).trim();
            if (!message.isEmpty() && (label.equalsIgnoreCase("spam") || label.equalsIgnoreCase("ham"))) {
                return new String[]{message, label};
            }
        }
        return null; // No se pudo rescatar
    }

    /**
     * Muestra un resumen del procesamiento del archivo CSV.
     *
     * @param validRows   Lista de filas válidas procesadas.
     * @param invalidRows Lista de filas inválidas ignoradas.
     */
    private void showSummary(List<String[]> validRows, List<String> invalidRows) {
        System.out.println("\n [ REPORT INIT ]................. * * * SUMMARY: loadCsvFile * * * .......................... ");
        System.out.println("\n[INFO] Archivo procesado exitosamente.");
        System.out.println("[INFO] Número de filas válidas (incluyendo rescatadas): " + validRows.size());


        if (!invalidRows.isEmpty()) {
            System.out.println("\n[WARNING] Número de filas inválidas ignoradas: " + invalidRows.size());
            for (String invalidRow : invalidRows) {
                System.err.println("[WARNING] Fila inválida ignorada: " + invalidRow);
            }
        } else {
            System.out.println("[INFO] No se encontraron filas inválidas.");
        }
        System.out.println("\n [ REPORT END]................. * * * SUMMARY: loadCsvFile FINISHED * * * .......................... ");
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

    /**
     * Carga un archivo JSON desde resources.
     *
     * @param resourcePath Ruta relativa del archivo JSON en resources.
     * @return Objeto JSONObject con el contenido del archivo.
     */
    public JSONObject loadJson(String resourcePath) {
        try {
            Path absolutePath = resolvePath(resourcePath);
            String content = Files.readString(absolutePath, StandardCharsets.UTF_8);
            return new JSONObject(content);
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar archivo JSON: " + resourcePath, e);
        }
    }


    public JSONObject loadJsonViejo(String resourcePath) {
        // Intenta cargar un recurso JSON desde el classpath
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            // Verifica si el archivo existe en el classpath
            if (inputStream == null) {
                throw new RuntimeException("Archivo no encontrado en el classpath: " + resourcePath);
            }

            // Lee todo el contenido del archivo como una cadena (UTF-8 para evitar problemas de codificación)
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            // Crea un JSONObject a partir del contenido del archivo
            JSONObject jsonObject = new JSONObject(content);

            // Aplica la normalización de claves y valores (elimina acentos)
            // Se utiliza el método de JsonUtils para mantener la lógica centralizada
            JSONObject normalizedJson = JsonUtils.normalizeJson(jsonObject);

            // Retorna el JSON normalizado
            return normalizedJson;

        } catch (IOException e) {
            // Si ocurre un error al leer el archivo, lanza una excepción personalizada con información adicional
            throw new RuntimeException("Error al leer el archivo JSON desde el classpath: " + resourcePath, e);
        }
    }

    //Se agrega jackson para guardar json con palabras ordenadas.
    public void saveJson(JSONObject jsonObject, String relativePath) {
        try {
            Path path = resolvePath(relativePath);
            Files.createDirectories(path.getParent()); // Crear directorios si no existen

            // Convertir JSONObject a un Map para usar Jackson
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS); // Ordenar claves
            String jsonString = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(jsonObject.toMap());

            Files.writeString(path, jsonString, StandardCharsets.UTF_8);
            System.out.println("JSON guardado en: " + path);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo JSON: " + relativePath, e);
        }
    }

    // Método para guardar un archivo JSON
    public void saveJsonViejo(JSONObject jsonObject, String relativePath) {
        try {
            Path path = resolvePath(relativePath);
            Files.createDirectories(path.getParent()); // Crear directorios si no existen
            Files.writeString(path, jsonObject.toString(4), StandardCharsets.UTF_8); // Formato con indentación
            System.out.println("JSON guardado en: " + path);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo JSON: " + relativePath, e);
        }
    }

}
