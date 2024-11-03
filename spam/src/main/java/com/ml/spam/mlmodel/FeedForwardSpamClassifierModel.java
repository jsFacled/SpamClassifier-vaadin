package com.ml.spam.mlmodel;

import deepnetts.data.DataSets;
import deepnetts.data.MLDataItem;
import deepnetts.data.preprocessing.scale.MaxScaler;
import deepnetts.eval.Evaluators;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.util.DeepNettsException;
import deepnetts.util.FileIO;

import java.io.File;
import java.io.IOException;
import javax.visrec.ml.data.DataSet;

public class FeedForwardSpamClassifierModel {

    public static void main(String[] args) {
        try {
            int numInputs = 57;
            int numOutputs = 1;

            // Cargar conjunto de datos desde el archivo CSV
            var pathCSV = "F:\\JAVA GENERAL\\MACHINE LEARNING JAVA\\Código-ejemplos-intellij\\SpamClassifier-vaadin\\spam\\src\\main\\resources\\static\\spam.csv";
            DataSet dataSet = DataSets.readCsv(pathCSV, numInputs, numOutputs, true);
            if (dataSet == null || dataSet.isEmpty()) {
                throw new IOException("Error al cargar el archivo CSV o el archivo está vacío.");
            }

            // Dividir en conjunto de entrenamiento (60%) y prueba (40%)
            DataSet<MLDataItem>[] trainAndTestSet = dataSet.split(0.6, 0.4);
            DataSet<MLDataItem> trainingSet = trainAndTestSet[0];
            DataSet<MLDataItem> testSet = trainAndTestSet[1];

            // Normalizar datos en el conjunto de entrenamiento y prueba
            MaxScaler scaler = new MaxScaler(trainingSet);
            scaler.apply(trainingSet);
            scaler.apply(testSet);

            // Crear la red neuronal de tipo Feed Forward
            FeedForwardNetwork neuralNet = FeedForwardNetwork.builder()
                    .addInputLayer(numInputs)
                    .addFullyConnectedLayer(30, ActivationType.TANH)   // Capa oculta 1
                    .addFullyConnectedLayer(15, ActivationType.TANH)   // Capa oculta 2
                    .addOutputLayer(numOutputs, ActivationType.SIGMOID)
                    .lossFunction(LossType.CROSS_ENTROPY)
                    .randomSeed(123)
                    .build();

            // Configuración del entrenamiento
            neuralNet.getTrainer()
                    .setMaxError(0.03f)
                    .setLearningRate(0.01f)
                    .setMaxEpochs(15000);

            // Entrenar la red neuronal en el conjunto de entrenamiento
            System.out.println("Entrenando la red neuronal...");
            neuralNet.train(trainingSet);

            // Evaluar el modelo en el conjunto de prueba
            var em = Evaluators.evaluateClassifier(neuralNet, testSet);
            System.out.println("Resultados de evaluación en el conjunto de prueba: " + em);

            // Guardar el modelo en formato JSON
            String modelFilePath = "spam/src/main/resources/models/feedforward_spam_classifier.json";
            new File(modelFilePath).getParentFile().mkdirs(); // Crear directorios si no existen
            try {
                FileIO.writeToFileAsJson(neuralNet, modelFilePath); // Guardar como JSON
                System.out.println("Modelo guardado exitosamente en " + modelFilePath);
            } catch (IOException e) {
                System.err.println("Error al guardar el modelo en formato JSON: " + e.getMessage());
            }

        } catch (IOException | DeepNettsException e) {
            System.err.println("Error en el entrenamiento o guardado del modelo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
