package com.ml.spam.dictionary;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DictionaryInitializer {

    // Método para inicializar el diccionario desde una lista de palabras
    public static void initializeFromList(List<String> words) {
        SpamDictionary dictionary = SpamDictionary.getInstance();

        // Convertir la lista en un conjunto para evitar duplicados
        Set<String> uniqueWords = words.stream().collect(Collectors.toSet());

        // Inicializar el diccionario con las palabras únicas
        dictionary.initializeFromList(uniqueWords);

        System.out.println("Diccionario inicializado con " + uniqueWords.size() + " palabras.");
    }

    // Método para inicializar el diccionario desde un archivo
    public static void initializeFromFile(String filePath) throws IOException {
        List<String> words = FileUtils.readLines(new File(filePath));
        initializeFromList(words);
    }
}
