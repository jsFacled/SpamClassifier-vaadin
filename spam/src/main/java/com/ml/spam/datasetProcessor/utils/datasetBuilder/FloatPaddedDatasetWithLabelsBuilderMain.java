package com.ml.spam.datasetProcessor.utils.datasetBuilder;

import com.ml.spam.handlers.ResourcesHandler;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FloatPaddedDatasetWithLabelsBuilderMain {

    public static void main(String[] args) {
        Path tokensFile = Paths.get("F:\\JAVA GENERAL\\MACHINE LEARNING JAVA\\Código-ejemplos-intellij\\Clasificador Spam\\SpamClassifier-vaadin\\spam\\src\\main\\resources\\static\\datasets\\separateMessagesAndLabels\\messages_spamham_numtokens.txt");
        Path labelsFile = Paths.get("F:\\JAVA GENERAL\\MACHINE LEARNING JAVA\\Código-ejemplos-intellij\\Clasificador Spam\\SpamClassifier-vaadin\\spam\\src\\main\\resources\\static\\datasets\\separateMessagesAndLabels\\labels.csv");
        Path outputFile = Paths.get("spam/src/main/resources/static/datasets/mlDatasets/messages_spamham_dataset_float.csv");

        int fixedLength = 80;
        ResourcesHandler handler = new ResourcesHandler();

        try (
                BufferedReader tokenReader = Files.newBufferedReader(tokensFile);
                BufferedWriter writer = Files.newBufferedWriter(outputFile)
        ) {
            List<String> labelsList = Files.readAllLines(labelsFile);


            String tokenLine;
            int lineIndex = 0;

            while ((tokenLine = tokenReader.readLine()) != null && lineIndex < labelsList.size()) {
                String[] tokens = tokenLine.trim().split("\\s+");
                List<String> padded = new ArrayList<>();

                for (int i = 0; i < fixedLength; i++) {
                    padded.add(i < tokens.length ? tokens[i] + ".0" : "0.0");
                }
                //Convierte cada label a float
                String label = labelsList.get(lineIndex).trim().equalsIgnoreCase("spam") ? "1.0" : "0.0";
                padded.add(label);


                writer.write(String.join(",", padded));
                writer.newLine();
                lineIndex++;
            }

            System.out.println("✅ Dataset generado correctamente: " + outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
