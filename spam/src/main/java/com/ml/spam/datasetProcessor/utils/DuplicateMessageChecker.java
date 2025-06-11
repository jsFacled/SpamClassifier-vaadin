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

    public int countDuplicatesFromLineByLineFile(String path) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(path));
        Set<String> unique = new HashSet<>();
        int duplicates = 0;
        for (String line : lines) {
            String processed = preprocess(line);
            if (!unique.add(processed)) {
                duplicates++;
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

    public void removeDuplicatesFromFile(String inputPath, String outputPath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(inputPath));
        Set<String> seen = new HashSet<>();
        List<String> output = new ArrayList<>();

        for (String line : lines) {
            String processed = preprocess(line);
            if (seen.add(processed)) {
                output.add(line); // conservar línea original
            }
        }

        Path outputFile = Paths.get(outputPath);
        Files.createDirectories(outputFile.getParent());
        Files.write(outputFile, output, StandardCharsets.UTF_8);

        System.out.println("✅ Archivo generado sin duplicados: " + outputPath);
        System.out.println("Total líneas originales: " + lines.size());
        System.out.println("Líneas únicas conservadas: " + output.size());
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
