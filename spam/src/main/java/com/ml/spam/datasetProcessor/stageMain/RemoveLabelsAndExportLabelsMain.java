package com.ml.spam.datasetProcessor.stageMain;

import com.ml.spam.datasetProcessor.utils.MessageNormalizerService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Separa los mensajes de sus etiquetas y exporta dos archivos:
 * - Uno con los mensajes sin etiqueta.
 * - Otro con las etiquetas (una por línea, en el mismo orden).
 */
public class RemoveLabelsAndExportLabelsMain {

    public static void main(String[] args) throws Exception {
        String inputArg = args.length > 0 ? args[0] : "F:\\JAVA GENERAL\\MACHINE LEARNING JAVA\\Código-ejemplos-intellij\\Clasificador Spam\\SpamClassifier-vaadin\\spam\\src\\main\\resources\\static\\datasets\\joined\\full_joined_normalized_noduplicates.csv";
        String outputMessagesArg = args.length > 1 ? args[1] : "joined_messages_labels_normalized_NoLabel.txt";
        String outputLabelsArg = args.length > 2 ? args[2] : "labels.csv";

        Path input = Paths.get(inputArg);
        Path outputMessages = Paths.get(outputMessagesArg);
        Path outputLabels = Paths.get(outputLabelsArg);

        List<String> lines = Files.readAllLines(input);
        List<String> messages = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (String line : lines) {
            int lastComma = line.lastIndexOf(',');
            if (lastComma != -1) {
                messages.add(line.substring(0, lastComma));
                labels.add(line.substring(lastComma + 1));
            }
        }

        MessageNormalizerService service = new MessageNormalizerService();
        service.exportToFile(messages, outputMessages);
        service.exportToFile(labels, outputLabels);

        System.out.println("✅ Mensajes sin etiquetas guardados en: " + outputMessages);
        System.out.println("✅ Etiquetas guardadas en: " + outputLabels);
    }
}
