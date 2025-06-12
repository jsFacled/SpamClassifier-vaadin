package com.ml.spam.datasetProcessor.stageMain;

import com.ml.spam.datasetProcessor.utils.MessageNormalizerService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class NormalizeJoinedMessagesMain {

    public static void main(String[] args) throws Exception {
        Path input  = Paths.get("spam/src/main/resources/static/datasets/joined/joined_messages_label.csv");
        Path output = Paths.get("joined_messages_labels_normalized_unique.txt");

        MessageNormalizerService service = new MessageNormalizerService();
        List<String> normalized = service.normalizeFromCsv(input);
        service.exportToFile(normalized, output);

        System.out.println("Archivo generado: " + output);
        System.out.println("Total líneas únicas: " + normalized.size());
    }
}
