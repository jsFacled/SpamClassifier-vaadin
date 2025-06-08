package com.ml.spam.mlmodel;

import deepnetts.data.DataSets;
import deepnetts.data.MLDataItem;
import deepnetts.eval.Evaluators;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.util.FileIO;

import javax.visrec.ml.data.DataSet;
import java.io.File;

public class FeedForwardSpamClassifierModel {

    public static void main(String[] args) {
        try {
            int numInputs = 466;
            int numOutputs = 1;

            // Cargar el dataset combinado
            DataSet<MLDataItem> combinedDataset = DataSets.readCsv(
                    "spam/src/main/resources/static/mlDatasets/mix_combined_full_dataset.csv",
                    numInputs, numOutputs, true);

            // Dividir automáticamente en entrenamiento y prueba (80%-20%)
            DataSet<?>[] split = DataSets.trainTestSplit(combinedDataset, 0.8);
            DataSet<MLDataItem> trainingSet = (DataSet<MLDataItem>) split[0];
            DataSet<MLDataItem> testSet = (DataSet<MLDataItem>) split[1];

            // Normalización de los datos
            DataSets.scaleMax(trainingSet); // o DataSets.standardize(trainingSet);
            DataSets.scaleMax(testSet);

            FeedForwardNetwork neuralNet = FeedForwardNetwork.builder()
                    .addInputLayer(numInputs)
                    .addFullyConnectedLayer(50, ActivationType.TANH)//30
                    .addFullyConnectedLayer(25, ActivationType.TANH)//15
                    .addFullyConnectedLayer(15, ActivationType.TANH)//15
                    .addOutputLayer(numOutputs, ActivationType.SIGMOID)
                    .lossFunction(LossType.CROSS_ENTROPY)
                    .randomSeed(123)
                    .build();

            for (MLDataItem item : trainingSet) {
                for (float val : item.getInput().getValues()) {
                    if (Float.isNaN(val) || Float.isInfinite(val)) {
                        System.out.println("⚠ Valor inválido detectado: " + val);
                    }
                }
            }

            neuralNet.getTrainer()
                    .setMaxError(0.0003f)//0.03f
                    .setLearningRate(0.00001f)//0.001f
                    .setMaxEpochs(20000);

            System.out.println("Entrenando...");
            neuralNet.train(trainingSet);

            System.out.println("\nEvaluación en test set:");
            System.out.println(Evaluators.evaluateClassifier(neuralNet, testSet));

            String modelPath = "spam/src/main/resources/models/feedforward_spam_classifier.json";
            new File(modelPath).getParentFile().mkdirs();
            FileIO.writeToFileAsJson(neuralNet, modelPath);
            System.out.println("\nModelo guardado en: " + modelPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
