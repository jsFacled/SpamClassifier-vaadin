package com.ml.spam.datasetProcessor.stageMain;

import com.ml.spam.datasetProcessor.utils.FileJoiner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Une varios archivos de texto (uno por línea) en un solo archivo.
 * Por defecto mezcla las líneas resultantes para generar un corpus variado.
 */
public class JoinTextFilesMain {

    public static void main(String[] args) throws Exception {
        // Archivos de entrada por defecto
        String[] defaults = {
                "joined_messages_labels_normalized_unique.txt",
                "joined_messages_triplecomillas_ham_normalized.txt",
                "joined_messages_triplecomillas_spam_normalized.txt"
        };
        String outputArg;
        List<Path> inputs = new ArrayList<>();
        if (args.length >= 2) {
            for (int i = 0; i < args.length - 1; i++) {
                inputs.add(Paths.get(args[i]));
            }
            outputArg = args[args.length - 1];
        } else {
            for (String d : defaults) {
                inputs.add(Paths.get(d));
            }
            outputArg = "combined_messages.txt";
        }

        Path output = Paths.get(outputArg);
        FileJoiner.joinFiles(inputs, output, FileJoiner.Format.TEXT, true);
        System.out.println("✅ Archivos combinados en: " + output);
    }
}