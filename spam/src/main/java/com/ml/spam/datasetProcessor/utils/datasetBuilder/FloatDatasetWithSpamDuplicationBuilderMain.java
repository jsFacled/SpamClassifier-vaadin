package com.ml.spam.datasetProcessor.utils.datasetBuilder;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FloatDatasetWithSpamDuplicationBuilderMain {

    private static final List<String> SPAM_KEY_TOKENS = Arrays.asList("461", "405", "259", "317", "458"); // compra, descuento, gratis, dinero, oferta
    private static final int FIXED_LENGTH = 80;

    public static void main(String[] args) throws IOException {
        Path tokensFile = Paths.get("F:\\JAVA GENERAL\\MACHINE LEARNING JAVA\\Código-ejemplos-intellij\\Clasificador Spam\\SpamClassifier-vaadin\\spam\\src\\main\\resources\\static\\datasets\\separateMessagesAndLabels\\messages_spamham_numtokens.txt");
        Path labelsFile = Paths.get("F:\\JAVA GENERAL\\MACHINE LEARNING JAVA\\Código-ejemplos-intellij\\Clasificador Spam\\SpamClassifier-vaadin\\spam\\src\\main\\resources\\static\\datasets\\separateMessagesAndLabels\\labels.csv");
        Path outputFile = Paths.get("spam/src/main/resources/static/mlDatasets/messages_spamham_dataset_float_DUPLICATED.csv");

        Files.createDirectories(outputFile.getParent());
        try (
                BufferedReader tokenReader = Files.newBufferedReader(tokensFile);

                BufferedWriter writer = Files.newBufferedWriter(outputFile)
        ) {
            List<String> labelsList = Files.readAllLines(labelsFile);
            String tokenLine;
            int lineIndex = 0;

            while ((tokenLine = tokenReader.readLine()) != null && lineIndex < labelsList.size()) {
                String[] tokens = tokenLine.trim().split("\\s+");
                String label = labelsList.get(lineIndex).trim().equalsIgnoreCase("spam") ? "1.0" : "0.0";

                List<String> padded = new ArrayList<>();
                for (int i = 0; i < FIXED_LENGTH; i++) {
                    padded.add(i < tokens.length ? tokens[i] + ".0" : "0.0");
                }
                padded.add(label);
                String row = String.join(",", padded);

                writer.write(row);
                writer.newLine();

                if (label.equals("1.0") && containsSpamKeyword(tokens)) {
                    writer.write(row);
                    writer.newLine(); // duplicar si es spam y tiene tokens clave
                }

                lineIndex++;
            }

            System.out.println("✅ Dataset duplicado generado en: " + outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean containsSpamKeyword(String[] tokens) {
        for (String t : tokens) {
            if (SPAM_KEY_TOKENS.contains(t)) return true;
        }
        return false;
    }
}
