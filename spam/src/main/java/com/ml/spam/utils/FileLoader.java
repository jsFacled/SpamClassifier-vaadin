package com.ml.spam.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileLoader {

    /**
     * Carga un archivo como InputStream desde los recursos (classpath).
     * @param resourcePath Ruta relativa dentro de los recursos (classpath).
     * @return InputStream del archivo.
     * @throws RuntimeException si el archivo no se encuentra.
     */
    public static InputStream loadResourceAsStream(String resourcePath) {
        InputStream inputStream = FileLoader.class.getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new RuntimeException("Archivo no encontrado en los recursos: " + resourcePath);
        }
        return inputStream;
    }

    /**
     * Lee el contenido de un InputStream como String.
     * @param inputStream InputStream del archivo.
     * @return Contenido del archivo como String.
     * @throws IOException si ocurre un error de lectura.
     */
    public static String readFile(InputStream inputStream) throws IOException {
        return new String(inputStream.readAllBytes());
    }

    /**
     * Lee el contenido de un archivo en el sistema de archivos como String.
     * @param filePath Ruta absoluta o relativa del archivo en el sistema.
     * @return Contenido del archivo como String.
     * @throws IOException si ocurre un error de lectura.
     */
    public static String readFile(String filePath) throws IOException {
        return Files.readString(Paths.get(filePath));
    }

    /**
     * Lee un archivo en el sistema de archivos como un array de bytes.
     * @param filePath Ruta absoluta o relativa del archivo en el sistema.
     * @return Contenido del archivo como byte[].
     * @throws IOException si ocurre un error de lectura.
     */
    public static byte[] readFileAsBytes(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath));
    }

    /**
     * Comprueba si un archivo existe en el sistema de archivos.
     * @param filePath Ruta absoluta o relativa del archivo en el sistema.
     * @return True si el archivo existe, false en caso contrario.
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * Carga el contenido de un archivo en recursos como String.
     * @param resourcePath Ruta relativa dentro de los recursos (classpath).
     * @return Contenido del archivo como String.
     * @throws RuntimeException si el archivo no se encuentra o hay un error de lectura.
     * * La ruta debe ser:
     *      *  String filePath = "static/archivo.json";
     */
    public static String loadResourceAsString(String resourcePath) {
        try (InputStream inputStream = loadResourceAsStream(resourcePath)) {
            return readFile(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo desde los recursos: " + resourcePath, e);
        }
    }
}
