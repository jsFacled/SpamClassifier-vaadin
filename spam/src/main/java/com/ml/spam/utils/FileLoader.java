package com.ml.spam.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;

public class FileLoader {

    // Método para cargar un archivo JSON como InputStream desde recursos
    public static InputStream loadResourceAsStream(String resourcePath) {
        InputStream inputStream = FileLoader.class.getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new RuntimeException("Archivo no encontrado: " + resourcePath);
        }
        return inputStream;
    }

    // Método para leer el contenido de un archivo y devolverlo como String
    public static String readFile(InputStream inputStream) throws IOException {
        return new String(inputStream.readAllBytes());
    }
}
