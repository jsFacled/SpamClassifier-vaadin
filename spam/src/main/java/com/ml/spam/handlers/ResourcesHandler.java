package com.ml.spam.handlers;

import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ml.spam.dictionary.models.CharSize;
import com.ml.spam.dictionary.models.WordData;
import com.ml.spam.utils.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.*;

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
      //  System.out.println("\n [Info LoadCsvFile] >> Ruta absoluta resuelta: " + absolutePath + "\n");

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
              //  System.out.println("Procesando línea: " + line);
                String[] columns = line.split(delimiter);

                // Normalizar las columnas eliminando espacios extra
                for (int i = 0; i < columns.length; i++) {
                    columns[i] = columns[i].trim();
                }

                // Validar y agregar fila
                if (columns.length == 2 && CsvUtils.isValidRow(columns)) {
                    rawRows.add(columns); // Agregar la fila válida a la lista
                   // System.out.println("[Info LoadCsvFile] >> Fila válida agregada: " + String.join(" | ", columns));
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


    /**
     * Extrae mensajes completos de un archivo TXT, separados por triple comillas (""" o “”“),
     * y para cada mensaje agrega el label indicado como segunda columna.
     *
     * Ejemplo de retorno: [mensaje, label]
     */
    public List<String[]> extractMessagesAndAddLabelFromTxt(String txtFilePath, String label) throws Exception {
        List<String[]> rows = new ArrayList<>();
        List<String> mensajes = loadTripleQuotedTxtFileAsMessages(txtFilePath); // Método robusto para triple comillas
        for (String msg : mensajes) {
            rows.add(new String[] {msg, label});
        }
        return rows;
    }

   // Este métod lee mensajes separados por triple comillas (de cualquier tipo)
    public List<String> loadTripleQuotedTxtFileAsMessages(String txtFilePath) throws Exception {

        Path absolutePath = resolvePath(txtFilePath);
        String content = Files.readString(absolutePath);

        return TripleQuoteUtils.extractMessages(content);
    }


    /* Este métod lee mensajes de un txt que están en una única fila, con label spam/ham.
        * El mensaje puede estar encerrado entre comillas o sin ellas:
            * Con comillas: "Mensaje en comillas",ham
            * Sin comillas: Mensaje sin comillas,spam
    */
    public List<String[]> loadQuotedOrPlainLabeledTxtFileAsMessages(String txtFilePath) {
        List<String[]> rows = new ArrayList<>();
        try {
            Path absolutePath = resolvePath(txtFilePath);
            List<String> lines = Files.readAllLines(absolutePath, StandardCharsets.UTF_8);

            for (String line : lines) {
                int lastCommaIndex = line.lastIndexOf(',');
                if (lastCommaIndex == -1) continue;

                String rawMessage = line.substring(0, lastCommaIndex).trim();
                String label = line.substring(lastCommaIndex + 1).trim();

                // Elimina comillas si están presentes
                if ((rawMessage.startsWith("\"") && rawMessage.endsWith("\"")) ||
                        (rawMessage.startsWith("“") && rawMessage.endsWith("”"))) {
                    rawMessage = rawMessage.substring(1, rawMessage.length() - 1).trim();
                }

                if (!rawMessage.isEmpty() && (label.equals("spam") || label.equals("ham"))) {
                    rows.add(new String[]{rawMessage, label});
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo archivo: " + txtFilePath, e);
        }
        return rows;
    }


    /**
     * Exporta una lista de líneas ya formateadas como mensaje,label al archivo indicado.
     *
     * @param labeledLines lista de líneas con formato CSV: mensaje,label
     * @param outputPath ruta de salida relativa
     */
    public void exportLabeledLinesToCsvFile(List<String> labeledLines, String outputPath) {
        try {
            Path absolutePath = resolvePath(outputPath);
            Files.createDirectories(absolutePath.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(absolutePath, StandardCharsets.UTF_8)) {
                for (String line : labeledLines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error exportando archivo a: " + outputPath, e);
        }
    }


    /**
     * Exporta una lista de mensajes con su etiqueta al archivo indicado, en formato CSV: mensaje,label
     *
     * @param rows lista de arreglos [mensaje, label]
     * @param outputPath ruta de salida relativa
     */
    public void exportLabeledRowsToCsvFile(List<String[]> rows, String outputPath) {
        try {
            Path absolutePath = resolvePath(outputPath);
            Files.createDirectories(absolutePath.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(absolutePath, StandardCharsets.UTF_8)) {
                for (String[] row : rows) {
                    if (row.length == 2) {
                        String message = row[0].replaceAll("\\s+", " ").trim();
                        String label = row[1].trim();
                        writer.write(message + "," + label);
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error exportando archivo a: " + outputPath, e);
        }
    }


    ///////////////////////////////////////////////////
    public Map<String, WordData> loadWordDataMapFromJson(String path) throws IOException {
        JSONObject json = loadJson(path);
        Map<String, WordData> map = new HashMap<>();

        for (String key : json.keySet()) {
            JSONObject obj = json.getJSONObject(key);
            WordData wd = new WordData(key);
            wd.incrementSpamFrequency(obj.optInt("spamFrequency", 0));
            wd.incrementHamFrequency(obj.optInt("hamFrequency", 0));
            map.put(key, wd);
        }
        return map;
    }

    public void saveJson(JSONObject jsonObject, String relativePath) {
        try {
            Path path = resolvePath(relativePath);
            Files.createDirectories(path.getParent()); // Crear directorios si no existen

            // Depuración para verificar contenido original
            //System.out.println("[DEBUG] JSONObject original: " + jsonObject.toString(4));

            // Sanitiza el contenido del JSON
            JSONObject sanitizedJson = sanitizeJsonObject(jsonObject);

            // Configuración de Jackson
            ObjectMapper mapper = new ObjectMapper();

            // Permitir caracteres Unicode y evitar escapes innecesarios
            mapper.getFactory().configure(JsonWriteFeature.ESCAPE_NON_ASCII.mappedFeature(), false);

            // Ordena las claves del JSON para mayor legibilidad
            mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);

            // Convierte a JSON formateado
            String jsonString = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(sanitizedJson.toMap());

            // Guarda el archivo JSON con codificación UTF-8
            Files.writeString(path, jsonString, StandardCharsets.UTF_8);

            System.out.println("//////////////////////////////////////////////////////////////////////////////////////////////////////////////");

          //  System.out.println("[DEBUG jsonObject] JSON original: " + jsonObject.toString(4));
          //  System.out.println("[DEBUG sanitizedJson] JSON sanitizado: " + sanitizedJson.toString(4));
            System.out.println("//////////////////////////////////////////////////////////////////////////////////////////////////////////////");

            System.out.println("[INFO] JSON guardado correctamente en: " + path);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo JSON: " + relativePath, e);
        }
    }

    /**
     * Sanitiza el contenido de un JSONObject, incluyendo claves y valores.
     */
    private JSONObject sanitizeJsonObject(JSONObject jsonObject) {
        JSONObject sanitizedJson = new JSONObject();
        for (String key : jsonObject.keySet()) {
            String sanitizedKey = sanitizeString(key);

            // Verifica si la clave se convierte en algo importante como `$`
            if (sanitizedKey.equals("invalid_key")) {
                System.out.println("[WARNING] Clave problemática encontrada y omitida: " + key);
                continue;
            }

            // Procesa recursivamente los valores
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                sanitizedJson.put(sanitizedKey, sanitizeJsonObject((JSONObject) value));
            } else if (value instanceof String) {
                sanitizedJson.put(sanitizedKey, sanitizeString((String) value));
            } else {
                sanitizedJson.put(sanitizedKey, value);
            }
        }
        return sanitizedJson;
    }

    /**
     * Elimina caracteres no válidos para UTF-8 de una cadena.
     */
    private String sanitizeString(String input) {
        if (input == null) {
            return null;
        }

        // Normaliza la cadena para manejar combinaciones Unicode
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFC);

        // Elimina caracteres de control invisibles que no deberían aparecer
        normalized = normalized.replaceAll("[\\u0000-\\u001F\\u007F-\\u009F\\u200B-\\u200D\\uFEFF\\u202F\\u200C]", " ");

        // Permite caracteres Unicode válidos, incluyendo símbolos y puntuaciones
        normalized = normalized.replaceAll("[^\\p{L}\\p{N}\\p{P}\\p{Z}\\p{Sc}\\p{Sm}\\p{So}\\s]", "").trim();

        return normalized.isEmpty() ? "invalid_key" : normalized;
    }

    public void exportFilteredWordDataList(List<WordData> wordList, String outputPath) {
        JSONObject json = new JSONObject();
        for (WordData wd : wordList) {
            JSONObject obj = new JSONObject();
            obj.put("hamFrequency", wd.getHamFrequency());
            obj.put("spamFrequency", wd.getSpamFrequency());
            json.put(wd.getWord(), obj);
        }
        saveJson(json, outputPath);
    }


///////////////////////////////////////////////////////////////////////////////////



    /*
//----------------------------------------------------------------------------
///  ----------------- SaveJson que funciona correctamente --------------------
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

*/
    /*
    // Se agrega Jackson para guardar JSON con palabras ordenadas.
    public void saveJson(JSONObject jsonObject, String relativePath) {
        try {
            Path path = resolvePath(relativePath);
            Files.createDirectories(path.getParent()); // Crear directorios si no existen

            // Convertir JSONObject a un Map para usar Jackson
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS); // Ordenar claves

            // Generar el JSON como String
            String jsonString = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(jsonObject.toMap());

            // Normalizar contenido para evitar caracteres no mapeables
            String sanitizedContent = jsonString.replaceAll("[^\\x20-\\x7E]", "");

            // Escribir el archivo en UTF-8
            Files.writeString(path, sanitizedContent, StandardCharsets.UTF_8);
            System.out.println("JSON guardado en: " + path);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo JSON: " + relativePath, e);
        }
    }
*/



    /**
     * Genera una ruta de archivo única dentro de `resources/static`.
     *
     * @param relativePath Ruta relativa base dentro de `resources/static`.
     * @return Ruta única del archivo como una String.
     */
    public String getUniqueFilePath(String relativePath) {
        Path basePath = resolvePath(relativePath); // Resolver la ruta base
        File file = basePath.toFile();

        String baseName = basePath.toString();
        String extension = "";

        // Separar nombre base y extensión correctamente
        int dotIndex = baseName.lastIndexOf(".");
        if (dotIndex > 0) {
            extension = baseName.substring(dotIndex); // Extraer extensión incluyendo el punto
            baseName = baseName.substring(0, dotIndex); // Nombre base sin la extensión
        }

        // Incrementar el sufijo numérico hasta encontrar un nombre único
        int count = 1;
        while (file.exists()) {
            file = new File(baseName + "_" + count + extension);
            System.out.println("[DEBUG] Generando archivo único: " + file.getAbsolutePath()); // Mensaje de depuración
            count++;
        }

        return file.getAbsolutePath();
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



    public void loadStructuredLexemesJsonAndExportUniqueLexemes(String inputFilePath, String outputFilePath) {
        try {
            // Cargar el archivo JSON estructurado desde el repositorio de lexemas
            JSONObject structuredLexemesJson = loadJson(inputFilePath);

            // Extraer los lexemas únicos del JSON estructurado
            Set<String> uniqueLexemes = JsonUtils.extractUniqueLexemesFromStructuredLexemes(structuredLexemesJson);

            // Crear el JSON de salida con los lexemas únicos
            JSONObject outputJson = new JSONObject();
            outputJson.put("lexemes", uniqueLexemes); // Cambio clave para reflejar lexemas

            // Guardar el archivo JSON de salida
            saveJson(outputJson, outputFilePath);
            System.out.println("[INFO] Exportación exitosa de lexemas únicos a: " + outputFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar y exportar lexemas únicos desde lexemas estructurados.", e);
        }
    }

    public void exportLexemesWithWords(String inputFilePath, String outputFilePath) {
        try {
            // Cargar el archivo JSON estructurado desde el repositorio de lexemas
            JSONObject structuredLexemesJson = loadJson(inputFilePath);

            // Extraer los lexemes y sus palabras sin la estructura de CharSize
            Map<String, List<String>> lexemesWithWords = JsonUtils.getLexemesWithWordsFromStructuredLexemes(structuredLexemesJson);

            // Crear el JSON de salida
            JSONObject outputJson = new JSONObject();
            lexemesWithWords.forEach(outputJson::put);

            // Guardar el archivo JSON de salida
            saveJson(outputJson, outputFilePath);
            System.out.println("[INFO] Exportación exitosa de lexemes con palabras a: " + outputFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Error al exportar lexemes con palabras desde lexemas estructurados.", e);
        }
    }



    public void loadStructuredLexemesJsonAndExportLexemesWithWords(String inputFilePath, String outputFilePath) {
        validateAndProcessStructuredLexemes(inputFilePath);

        JSONObject jsonObject = loadJson(inputFilePath);
        Map<String, List<String>> lexemesWithWords = JsonUtils.getLexemesWithWordsFromStructuredLexemes(jsonObject);

        JSONObject outputJson = new JSONObject(lexemesWithWords);
        saveJson(outputJson, outputFilePath);

        System.out.println("[INFO] Exportación de lexemas con palabras exitosa a: " + outputFilePath);
    }

    public ValidationResult validateAndProcessStructuredLexemes(String filePath) {
        try {
            JSONObject jsonObject = loadJson(filePath);
            return JsonUtils.validateStructuredLexemesRepository(jsonObject);
        } catch (Exception e) {
            throw new RuntimeException("Error durante la validación del archivo JSON: " + e.getMessage(), e);
        }
    }

    public void removeInvalidDuplicatesByCharSize(String filePath) {
        try {
            JSONObject jsonObject = loadJson(filePath);
            JsonUtils.removeInvalidDuplicatesByCharSize(jsonObject);
            saveJson(jsonObject, filePath);
            System.out.println("[INFO] Duplicados inválidos eliminados y JSON actualizado.");
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar duplicados inválidos: " + e.getMessage(), e);
        }
    }


    public void extractAndSaveCategorizedWordOnly(String inputFilePath, String outputDirectory) {
        try {
            // Cargar el JSON desde el archivo de entrada
            JSONObject jsonObject = loadJson(inputFilePath);

            // Validar la estructura del JSON
            JsonUtils.validateWordCategoryJsonStructure(jsonObject);

            // Extraer palabras categorizadas
            Map<String, List<String>> categorizedWords = JsonUtils.extractCategorizedWords(jsonObject);

            // Crear un JSONObject para la salida
            JSONObject outputJson = new JSONObject(categorizedWords);

            // Generar una ruta de archivo única para el archivo de salida
            String baseFileName = "updated_categorized_words_base_only.json";
            String uniqueOutputPath = getUniqueFilePath(baseFileName);

            // Guardar el JSON resultante
            saveJson(outputJson, uniqueOutputPath);

            System.out.println("[INFO] Exportación de palabras categorizadas exitosa a: " + uniqueOutputPath);
        } catch (Exception e) {
            throw new RuntimeException("Error al extraer y guardar palabras categorizadas: " + e.getMessage(), e);
        }
    }


    /**
     * Guarda las palabras categorizadas sin frecuencias y genera un reporte de palabras omitidas.
     * Si los archivos ya existen, se generarán nombres únicos con un sufijo (_1, _2, etc.).
     *
     * @param categorizedWords Palabras categorizadas sin frecuencias.
     * @param omittedWordsReport Lista de palabras omitidas con sus categorías.
     * @param outputPathBase Ruta base para el archivo JSON de palabras categorizadas.
     * @param reportPathBase Ruta base para el archivo de reporte.
     */
    public void saveCategorizedWordsOnlyAndReport(
            Map<String, List<String>> categorizedWords,
            List<String> omittedWordsReport,
            String outputPathBase,
            String reportPathBase) {

        try {
            // Generar rutas únicas para los archivos
            String uniqueOutputPath = getUniqueFilePath(outputPathBase);
            String uniqueReportPath = getUniqueFilePath(reportPathBase);

            // Guardar las palabras categorizadas como JSON
            JSONObject jsonOutput = new JSONObject(categorizedWords);
            saveJson(jsonOutput, uniqueOutputPath);

            // Guardar el reporte de palabras omitidas
            Path reportFilePath = resolvePath(uniqueReportPath);
            Files.write(reportFilePath, omittedWordsReport, StandardCharsets.UTF_8);

            System.out.println("[INFO] JSON exportado a: " + uniqueOutputPath);
            System.out.println("[INFO] Reporte guardado en: " + uniqueReportPath);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar datos y reporte: " + e.getMessage(), e);
        }
    }


    /**
     * Actualiza el repositorio de lexemas desde un archivo JSON con múltiples categorías y palabras.
     * Si una categoría dentro del CharSize no existe, se crea antes de agregar la palabra.
     *
     * @param lexemeRepositoryPath Ruta del archivo JSON del repositorio de lexemas.
     * @param inputJsonListPath Ruta del archivo JSON con lexemas y palabras a agregar.
     */
    public void updateAddingLexemeRepositoryFromJsonList(String lexemeRepositoryPath, String inputJsonListPath) {

        try {
            // Cargar los archivos JSON
            JSONObject lexemeRepository = loadJson(lexemeRepositoryPath);
            JSONObject inputJson = loadJson(inputJsonListPath);

            // Iterar sobre cada categoría en el JSON de entrada
            for (String lexeme : inputJson.keySet()) {
                JSONArray wordsToAdd = inputJson.optJSONArray(lexeme);
                if (wordsToAdd == null) continue;

                for (int i = 0; i < wordsToAdd.length(); i++) {
                    String word = wordsToAdd.getString(i);

                    // Determinar el CharSize basado en la longitud de la palabra
                    CharSize charSize = TextUtils.determineCharSize(word);

                    // Verificar que el CharSize ya existe en el JSON (no lo creamos)
                    if (!lexemeRepository.has(charSize.getJsonKey())) {
                        throw new IllegalArgumentException("[ERROR] CharSize '" + charSize.getJsonKey() + "' no existe en el repositorio.");
                    }

                    // Obtener la categoría principal (CharSize)
                    JSONObject charSizeJson = lexemeRepository.getJSONObject(charSize.getJsonKey());

                    // Obtener o crear la subcategoría del lexeme
                    JSONArray existingWords = charSizeJson.optJSONArray(lexeme);
                    if (existingWords == null) {
                        existingWords = new JSONArray();
                        charSizeJson.put(lexeme, existingWords);
                    }

                    // Agregar la palabra si no está duplicada
                    if (!existingWords.toList().contains(word)) {
                        existingWords.put(word);
                        System.out.println("[INFO] '" + word + "' agregado a '" + lexeme + "' en CharSize '" + charSize.getJsonKey() + "'.");
                    }
                }
            }

            // Guardar el repositorio actualizado
            saveJson(lexemeRepository, lexemeRepositoryPath);
            System.out.println("[INFO] Repositorio de lexemas actualizado desde " + inputJsonListPath);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el repositorio de lexemas: " + e.getMessage(), e);
        }
    }


    /**
     * Agrega una palabra al repositorio de lexemas bajo una categoría específica.
     * Si la categoría dentro del CharSize no existe, se crea antes de agregar la palabra.
     *
     * @param lexemeRepositoryPath Ruta del archivo JSON del repositorio de lexemas.
     * @param word Palabra a agregar.
     * @param lexeme Categoría donde se agregará la palabra.
     */
    public void addWordToLexemeRepository(String lexemeRepositoryPath, String word, String lexeme) {
        try {
            // Cargar el repositorio de lexemas
            JSONObject lexemeRepository = loadJson(lexemeRepositoryPath);

            // Determinar el CharSize según la longitud de la palabra
            CharSize charSize = TextUtils.determineCharSize(word);

            // Verificar que el CharSize ya existe en el JSON (no lo creamos)
            if (!lexemeRepository.has(charSize.getJsonKey())) {
                throw new IllegalArgumentException("[ERROR] CharSize '" + charSize.getJsonKey() + "' no existe en el repositorio.");
            }

            // Obtener la categoría principal (CharSize)
            JSONObject charSizeJson = lexemeRepository.getJSONObject(charSize.getJsonKey());

            // Obtener o crear la subcategoría del lexeme
            JSONArray wordsArray = charSizeJson.optJSONArray(lexeme);
            if (wordsArray == null) {
                wordsArray = new JSONArray();
                charSizeJson.put(lexeme, wordsArray);
            }

            // Agregar la palabra si no existe
            if (!wordsArray.toList().contains(word)) {
                wordsArray.put(word);
                System.out.println("[INFO] Palabra '" + word + "' agregada a '" + lexeme + "' en CharSize '" + charSize.getJsonKey() + "'.");
            } else {
                System.out.println("[INFO] La palabra '" + word + "' ya existe en '" + lexeme + "'.");
            }

            // Guardar el repositorio actualizado
            saveJson(lexemeRepository, lexemeRepositoryPath);
        } catch (Exception e) {
            throw new RuntimeException("Error al agregar palabra al repositorio de lexemas: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina lexemas completos del repositorio, según una lista provista en un archivo JSON.
     * El archivo debe tener el formato: { "lexemesToDelete": ["lex1", "lex2", ...] }
     *
     * @param lexemeRepositoryPath Ruta del archivo JSON del repositorio de lexemas.
     * @param inputJsonPath Ruta del archivo JSON con la lista de lexemas a eliminar.
     */
    public void updateDeletingLexemesFromJsonList(String lexemeRepositoryPath, String inputJsonPath) {
        try {
            JSONObject inputJson = loadJson(inputJsonPath);
            JSONArray lexemesArray = inputJson.optJSONArray("lexemesToDelete");
            if (lexemesArray == null) {
                System.out.println("[INFO] No se encontró la clave 'lexemesToDelete'. Nada que eliminar.");
                return;
            }

            List<String> lexemesToDelete = lexemesArray.toList().stream()
                    .map(Object::toString)
                    .toList();

            for (String lexeme : lexemesToDelete) {
                removeLexeme(lexemeRepositoryPath, lexeme);
            }

            System.out.println("[INFO] Lexemas eliminados correctamente desde: " + inputJsonPath);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar lexemas desde archivo JSON: " + e.getMessage(), e);
        }
    }
    /**
     * Elimina completamente un lexema (clave) del repositorio en todos los bloques CharSize.
     *
     * @param lexemeRepositoryPath Ruta al archivo JSON del repositorio de lexemas.
     * @param lexeme Nombre del lexema a eliminar.
     * @throws RuntimeException Si ocurre un error al leer o guardar el JSON.
     */
    public void removeLexeme(String lexemeRepositoryPath, String lexeme) {
        try {
            JSONObject lexemeRepository = loadJson(lexemeRepositoryPath);
            boolean found = false;

            for (CharSize charSize : CharSize.values()) {
                String key = charSize.getJsonKey();
                if (!lexemeRepository.has(key)) continue;

                JSONObject charSizeJson = lexemeRepository.getJSONObject(key);
                if (charSizeJson.has(lexeme)) {
                    charSizeJson.remove(lexeme);
                    System.out.println("[INFO] Lexema '" + lexeme + "' eliminado del bloque '" + key + "'.");
                    found = true;
                }
            }

            if (found) {
                saveJson(lexemeRepository, lexemeRepositoryPath);
            } else {
                System.out.println("[INFO] Lexema '" + lexeme + "' no se encontró en ningún bloque.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar lexema: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina palabras específicas dentro de lexemas del repositorio, según un archivo JSON.
     * El archivo debe tener el formato: { "lex1": ["palabra1", "palabra2"], "lex2": ["p1"] }
     *
     * @param lexemeRepositoryPath Ruta del archivo JSON del repositorio de lexemas.
     * @param inputJsonPath Ruta del archivo JSON con las palabras a eliminar por lexema.
     */
    public void updateDeletingWordsFromLexemesJsonList(String lexemeRepositoryPath, String inputJsonPath) {
        try {
            JSONObject inputJson = loadJson(inputJsonPath);

            for (String lexeme : inputJson.keySet()) {
                JSONArray wordsArray = inputJson.optJSONArray(lexeme);
                if (wordsArray == null) continue;

                List<String> wordsToRemove = wordsArray.toList().stream()
                        .map(Object::toString)
                        .toList();

                removeWordsFromLexeme(lexemeRepositoryPath, lexeme, wordsToRemove);
            }

            System.out.println("[INFO] Palabras eliminadas de lexemas desde: " + inputJsonPath);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar palabras desde archivo JSON: " + e.getMessage(), e);
        }
    }
    /**
     * Elimina una lista de palabras de un lexema existente dentro del bloque correspondiente por CharSize.
     * Si alguna palabra no está, simplemente se ignora.
     *
     * @param lexemeRepositoryPath Ruta al archivo JSON del repositorio de lexemas.
     * @param lexeme Nombre del lexema del cual se eliminarán las palabras.
     * @param words Lista de palabras a eliminar.
     * @throws RuntimeException Si ocurre un error al leer o guardar el JSON.
     */
    public void removeWordsFromLexeme(String lexemeRepositoryPath, String lexeme, List<String> words) {
        try {
            JSONObject lexemeRepository = loadJson(lexemeRepositoryPath);

            for (String word : words) {
                CharSize charSize = TextUtils.determineCharSize(word);

                if (!lexemeRepository.has(charSize.getJsonKey())) continue;

                JSONObject charSizeJson = lexemeRepository.getJSONObject(charSize.getJsonKey());
                JSONArray wordArray = charSizeJson.optJSONArray(lexeme);
                if (wordArray == null) continue;

                List<Object> currentWords = wordArray.toList();
                if (currentWords.remove(word)) {
                    System.out.println("[INFO] Palabra '" + word + "' eliminada del lexema '" + lexeme + "'.");
                    charSizeJson.put(lexeme, new JSONArray(currentWords));
                }
            }

            saveJson(lexemeRepository, lexemeRepositoryPath);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar palabras del lexema: " + e.getMessage(), e);
        }
    }


    ////////////////////////////////////////////////////////////////


    //Recibe el path y el label ya que los archivos txt solamente tienen mensaje sin label.
    //Es para archivos tipo csv pero en formato .txt
    public List<String[]> loadTxtFileAsRows(String txtFilePath, String label) {
        List<String[]> rows = new ArrayList<>();

        try {
            // Resolver el path absoluto
            Path absolutePath = resolvePath(txtFilePath);

            // Leer el contenido del archivo como un String
            String content = Files.readString(absolutePath, StandardCharsets.UTF_8);

            int totalMessages = 0;
            List<String> messages = TripleQuoteUtils.extractMessages(content);
            for (String message : messages) {
                rows.add(new String[]{message, label});
                totalMessages++;

            }
              } catch (Exception e) {
            throw new RuntimeException("Error al leer el archivo TXT: " + txtFilePath, e);
        }
        return rows;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////// Para implementar en futuras correcciones //////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Carga un corpus desde un archivo CSV (con etiquetas) o TXT (sin etiquetas, requiere label externo).
     *
     * @param path Ruta relativa al archivo.
     * @param format Formato del archivo: "csv" o "txt".
     * @param labelIfTxt Etiqueta a aplicar a los mensajes si es un archivo TXT (ej. "spam" o "ham").
     * @return Lista de arreglos [mensaje, label].
     */
    public List<String[]> loadCorpusRows(String path, String format, String labelIfTxt) throws IOException {
        if ("csv".equalsIgnoreCase(format)) {
            List<String[]> rawRows = loadCsvFile(path);
            return rawRows.stream()
                    .filter(TextUtils::isRawRow)
                    .toList();
        } else if ("txt".equalsIgnoreCase(format)) {
            try {
                return extractMessagesAndAddLabelFromTxt(path, labelIfTxt);
            } catch (Exception e) {
                throw new IOException("Error procesando archivo TXT: " + e.getMessage(), e);
            }
        } else {
            throw new IllegalArgumentException("Formato no soportado: " + format);
        }
    }


    public void exportWordDataMapToJson(Map<String, WordData> wordMap, String outputPath) {
        JSONObject outputJson = new JSONObject();
        for (Map.Entry<String, WordData> entry : wordMap.entrySet()) {
            WordData wd = entry.getValue();
            JSONObject freqJson = new JSONObject();
            freqJson.put("hamFrequency", wd.getHamFrequency());
            freqJson.put("spamFrequency", wd.getSpamFrequency());
            outputJson.put(entry.getKey(), freqJson);
        }
        saveJson(outputJson, outputPath); // método ya implementado
    }


    ///////////////////////////////////////////////////////////////////
    /*
       * Lee todas las líneas como texto plano
       * Devuelve una lista de String, cada una representando una línea (mensaje tokenizado en tu caso).
    */
    public List<String> loadPlainTextLines(String relativePath) throws IOException {
       Path absolutePath = resolvePath(relativePath);

        return Files.readAllLines(absolutePath, StandardCharsets.UTF_8);
    }


}//end
