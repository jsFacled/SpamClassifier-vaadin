package com.ml.spam.utils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileWriter {

    /**
     * Escribe contenido en un archivo.
     * Si el archivo ya existe, sobrescribe su contenido.
     *
     * @param filePath Ruta del archivo donde se escribirá el contenido.
     * @param content  Contenido a escribir en el archivo.
     * @throws IOException Si ocurre un error de escritura.
     */
    public static void writeFile(String filePath, String content) throws IOException {
        Files.writeString(Paths.get(filePath), content, StandardCharsets.UTF_8);
    }

    /**
     * Añade contenido a un archivo existente.
     * Si el archivo no existe, lo crea.
     *
     * @param filePath Ruta del archivo donde se añadirá el contenido.
     * @param content  Contenido a añadir al archivo.
     * @throws IOException Si ocurre un error de escritura.
     */
    public static void appendToFile(String filePath, String content) throws IOException {
        Files.writeString(Paths.get(filePath), content, StandardCharsets.UTF_8, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    }

    /**
     * Elimina un archivo.
     *
     * @param filePath Ruta del archivo que se desea eliminar.
     * @throws IOException Si ocurre un error al eliminar el archivo.
     */
    public static void deleteFile(String filePath) throws IOException {
        Files.delete(Paths.get(filePath));
    }

    /**
     * Crea un archivo vacío si no existe.
     *
     * @param filePath Ruta del archivo que se desea crear.
     * @throws IOException Si ocurre un error al crear el archivo.
     */
    public static void createEmptyFile(String filePath) throws IOException {
        Files.createFile(Paths.get(filePath));
    }
}
