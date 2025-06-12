package com.ml.spam.datasetProcessor.stageMain;


import com.ml.spam.datasetProcessor.utils.MessageNormalizerService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Elimina las etiquetas de un archivo de mensajes ya normalizado y lo exporta.
 */
public class RemoveLabelsMain {

    public static void main(String[] args) throws Exception {
        String inputArg = args.length > 0 ? args[0] : "joined_messages_labels_normalized_unique.txt";
        String outputArg = args.length > 1 ? args[1] : "joined_messages_labels_normalized_NoLabel.txt";

        Path input = Paths.get(inputArg);
        Path output = Paths.get(outputArg);

        List<String> lines = Files.readAllLines(input);
        MessageNormalizerService service = new MessageNormalizerService();
        List<String> without = service.removeLabels(lines);
        service.exportToFile(without, output);
        System.out.println("✅ Archivo sin etiquetas generado en: " + output);
    }
}