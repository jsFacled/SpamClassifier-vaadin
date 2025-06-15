package com.ml.spam.datasetProcessor.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase utilitaria para detectar y eliminar mensajes duplicados en distintos formatos de archivo.
 */
public class DuplicateMessageChecker {
    public enum InputFormat {
        LINE_BY_LINE,
        TRIPLE_QUOTED
    }

    private boolean ignoreCase = false;
    private boolean stripQuotes = false;
    private boolean normalizeWhitespace = true;
    private boolean removeLabel = false;

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    public void setStripQuotes(boolean stripQuotes) {
        this.stripQuotes = stripQuotes;
    }

    public void setNormalizeWhitespace(boolean normalizeWhitespace) {
        this.normalizeWhitespace = normalizeWhitespace;
    }

    public void setRemoveLabel(boolean removeLabel) {
        this.removeLabel = removeLabel;
    }

    public int countDuplicates(String path, InputFormat format) throws IOException {
        if (format == InputFormat.LINE_BY_LINE) {
            return countDuplicatesFromLineByLineFile(path);
        } else if (format == InputFormat.TRIPLE_QUOTED) {
            return countDuplicatesFromTripleQuotedFile(path);
        }
        throw new IllegalArgumentException("Formato no soportado: " + format);
    }

    public void removeDuplicates(String inputPath, String outputPath, InputFormat format) throws IOException {
        if (format == InputFormat.LINE_BY_LINE) {
            removeDuplicatesFromFile(inputPath, outputPath);
        } else if (format == InputFormat.TRIPLE_QUOTED) {
            removeDuplicatesFromTripleQuotedFile(inputPath, outputPath);
        } else {
            throw new IllegalArgumentException("Formato no soportado: " + format);
        }
    }

    public int countDuplicatesFromLineByLineFile(String path) throws IOException {
        Path file = Paths.get(path);
        Set<String> unique = new HashSet<>();
        int duplicates = 0;

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String processed = preprocess(line);
                if (!unique.add(processed)) {
                    duplicates++;
                }
            }
        }

        return duplicates;
    }

    public int countDuplicatesFromTripleQuotedFile(String path) throws IOException {
        String content = Files.readString(Paths.get(path));
        Pattern pattern = Pattern.compile("(?m)^[\\\"“”]{3}\\s*$([\\s\\S]*?)(?=^[\\\"“”]{3}\\s*$|\\z)", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(content);

        Set<String> unique = new HashSet<>();
        int duplicates = 0;

        while (matcher.find()) {
            String msg = matcher.group(1).replaceAll("\\s+", " ").trim();
            String processed = preprocess(msg);
            if (!unique.add(processed)) {
                duplicates++;
            }
        }

        return duplicates;
    }

    public void removeDuplicatesFromTripleQuotedFile(String inputPath, String outputPath) throws IOException {
        String content = Files.readString(Paths.get(inputPath));
        Pattern pattern = Pattern.compile("(?m)^[\\\"“”]{3}\\s*$([\\s\\S]*?)(?=^[\\\"“”]{3}\\s*$|\\z)", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(content);

        LinkedHashSet<String> uniqueMessages = new LinkedHashSet<>();

        while (matcher.find()) {
            String msg = matcher.group(1).replaceAll("\\s+", " ").trim();
            String processed = preprocess(msg);
            if (uniqueMessages.add(processed)) {
                // el mensaje se almacena sin espacios extra
            }
        }

        Path outputFile = Paths.get(outputPath);
        Files.createDirectories(outputFile.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8)) {
            for (String msg : uniqueMessages) {
                writer.write("\"\"\"");
                writer.newLine();
                writer.write(msg);
                writer.newLine();
                writer.write("\"\"\"");
                writer.newLine();
            }
        }

        System.out.println("✅ Archivo generado sin duplicados: " + outputPath);
        System.out.println("Total mensajes únicos: " + uniqueMessages.size());
    }

    public void removeDuplicatesFromFile(String inputPath, String outputPath) throws IOException {
        Path input = Paths.get(inputPath);
        Path output = Paths.get(outputPath);
        Path parent = output.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }


        Set<String> seen = new HashSet<>();
        int originalLines = 0;
        int uniqueLines = 0;

        try (BufferedReader reader = Files.newBufferedReader(input);
             BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {

            String line;
            while ((line = reader.readLine()) != null) {
                originalLines++;
                String processed = preprocess(line);
                if (seen.add(processed)) {
                    writer.write(line);
                    writer.newLine();
                    uniqueLines++;
                }
            }
        }

        System.out.println("✅ Archivo generado sin duplicados: " + outputPath);
        System.out.println("Total líneas originales: " + originalLines);
        System.out.println("Líneas únicas conservadas: " + uniqueLines);
    }

    private String preprocess(String text) {
        String clean = text.trim();

        if (removeLabel) {
            int comma = clean.lastIndexOf(',');
            if (comma > 0) {
                clean = clean.substring(0, comma).trim();
            }
        }

        if (stripQuotes) {
            if ((clean.startsWith("\"") && clean.endsWith("\"")) ||
                    (clean.startsWith("'") && clean.endsWith("'"))) {
                clean = clean.substring(1, clean.length() - 1).trim();
            }
        }

        if (normalizeWhitespace) {
            clean = clean.replaceAll("\\s+", " ");
        }

        if (ignoreCase) {
            clean = clean.toLowerCase();
        }

        return clean;
    }
}
