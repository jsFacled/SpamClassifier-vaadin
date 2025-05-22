package com.ml.spam.mlmodel;

import deepnetts.data.DataSets;
import deepnetts.data.MLDataItem;
import deepnetts.data.preprocessing.scale.MaxScaler;
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
            int numInputs = 462;
            int numOutputs = 1;

            // Cargar datasets
            DataSet<MLDataItem> trainingSet = DataSets.readCsv("spam/src/main/resources/static/mlDatasets/generated_dataset_train.csv", numInputs, numOutputs, true);
            DataSet<MLDataItem> testSet = DataSets.readCsv("spam/src/main/resources/static/mlDatasets/generated_dataset_test.csv", numInputs, numOutputs, true);
            DataSet<MLDataItem> spamOnlySet = DataSets.readCsv("spam/src/main/resources/static/mlDatasets/generated_dataset_comillas.csv", numInputs, numOutputs, true);

            // Normalización
            MaxScaler scaler = new MaxScaler(trainingSet);
            scaler.apply(trainingSet);
            scaler.apply(testSet);
            scaler.apply(spamOnlySet);

            // Crear red neuronal
            FeedForwardNetwork neuralNet = FeedForwardNetwork.builder()
                    .addInputLayer(numInputs)
                    .addFullyConnectedLayer(30, ActivationType.TANH)
                    .addFullyConnectedLayer(15, ActivationType.TANH)
                    .addOutputLayer(numOutputs, ActivationType.SIGMOID)
                    .lossFunction(LossType.CROSS_ENTROPY)
                    .randomSeed(123)
                    .build();

            // Configuración del entrenamiento
            neuralNet.getTrainer()
                    .setMaxError(0.03f)
                    .setLearningRate(0.01f)
                    .setMaxEpochs(15000);

            // Entrenamiento
            System.out.println("Entrenando...");
            neuralNet.train(trainingSet);

            // Evaluación estándar
            System.out.println("\nEvaluación en test set:");
            System.out.println(Evaluators.evaluateClassifier(neuralNet, testSet));

            // Evaluación con mensajes spam solamente
            System.out.println("\nEvaluación SOLO en mensajes spam:");
            System.out.println(Evaluators.evaluateClassifier(neuralNet, spamOnlySet));

            // Guardar modelo
            String modelPath = "spam/src/main/resources/models/feedforward_spam_classifier.json";
            new File(modelPath).getParentFile().mkdirs();
            FileIO.writeToFileAsJson(neuralNet, modelPath);
            System.out.println("\nModelo guardado en: " + modelPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
