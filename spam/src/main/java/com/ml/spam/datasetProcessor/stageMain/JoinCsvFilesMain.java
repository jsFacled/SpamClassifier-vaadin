package com.ml.spam.datasetProcessor.stageMain;


import com.ml.spam.datasetProcessor.utils.FileJoiner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Combina múltiples archivos CSV en un solo archivo.
 * Se asume que todos tienen el mismo formato de columnas.
 */
public class JoinCsvFilesMain {

    public static void main(String[] args) throws Exception {
        String[] defaults = {
                "spam/src/main/resources/static/mlDatasets/generated_dataset_train.csv",
                "spam/src/main/resources/static/mlDatasets/generated_dataset_test.csv"
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
            outputArg = "combined_dataset.csv";
        }

        Path output = Paths.get(outputArg);
        FileJoiner.joinFiles(inputs, output, FileJoiner.Format.CSV, true);
        System.out.println("✅ CSV combinado generado en: " + output);
    }
}