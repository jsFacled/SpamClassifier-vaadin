package com.ml.spam.mlmodel;

import deepnetts.data.DataSets;
import deepnetts.data.MLDataItem;
import deepnetts.data.preprocessing.scale.MaxScaler;
//import deepnetts.data.preprocessing.scale.Normalizer;
import deepnetts.eval.Evaluators;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.net.train.opt.OptimizerType;
import deepnetts.util.FileIO;
import javax.visrec.ml.data.DataSet;
import java.io.File;

public class OptimizedSpamClassifierModelByClaude {

    public static void main(String[] args) {
        try {
            int numInputs = 461;
            int numOutputs = 1;

            // Cargar datasets
            DataSet<MLDataItem> trainingSet = DataSets.readCsv(
                    "spam/src/main/resources/static/mlDatasets/generated_dataset_train.csv",
                    numInputs, numOutputs, true);
            DataSet<MLDataItem> testSet = DataSets.readCsv(
                    "spam/src/main/resources/static/mlDatasets/generated_dataset_test.csv",
                    numInputs, numOutputs, true);
            DataSet<MLDataItem> spamOnlySet = DataSets.readCsv(
                    "spam/src/main/resources/static/mlDatasets/generated_dataset_comillas_spam.csv",
                    numInputs, numOutputs, true);

            // CRÍTICO: Aplicar normalización para datos dispersos
            System.out.println("Aplicando normalización...");
            MaxScaler scaler = new MaxScaler(trainingSet);
            scaler.apply(trainingSet);
            scaler.apply(testSet);
            scaler.apply(spamOnlySet);

            // Verificación mejorada de datos
            validateDataset(trainingSet, "Training Set");
            validateDataset(testSet, "Test Set");

            // Arquitectura optimizada para datos dispersos (461 características)
            FeedForwardNetwork neuralNet = FeedForwardNetwork.builder()
                    .addInputLayer(numInputs)
                    // Reducción drástica: de 461 → 128 (captura patrones principales)
                    .addFullyConnectedLayer(128, ActivationType.RELU)  // ReLU mejor para sparsity
                    .addFullyConnectedLayer(64, ActivationType.RELU)   // Reducción progresiva
                    .addFullyConnectedLayer(32, ActivationType.RELU)   // Bottleneck para características clave
                    .addFullyConnectedLayer(16, ActivationType.TANH)   // TANH para refinamiento final
                    .addOutputLayer(numOutputs, ActivationType.SIGMOID)
                    .lossFunction(LossType.CROSS_ENTROPY)
                    .randomSeed(42)  // Semilla fija para reproducibilidad
                    .build();

            // Configuración de entrenamiento optimizada
            neuralNet.getTrainer()
                    .setMaxError(0.01f)        // Mayor precisión objetivo
                    .setLearningRate(0.01f)    // Learning rate más alto inicialmente
                    .setMaxEpochs(1000)        // Menos épocas, más eficiente
                    .setOptimizer(OptimizerType.MOMENTUM)  // Momentum para datos dispersos
                    .setMomentum(0.9f);        // Momentum factor para acelerar convergencia


            // Monitoreo del entrenamiento
            System.out.println("=== CONFIGURACIÓN DEL MODELO ===");
            System.out.println("Inputs: " + numInputs);
            System.out.println("Arquitectura: 461 → 128 → 64 → 32 → 16 → 1");
            System.out.println("Activación: ReLU (capas ocultas), Sigmoid (salida)");
            System.out.println("Optimizador: Adam");
            System.out.println("Learning Rate: 0.01");
            System.out.println("====================================");

            System.out.println("\n🚀 Iniciando entrenamiento...");
            long startTime = System.currentTimeMillis();
            neuralNet.train(trainingSet);
            long endTime = System.currentTimeMillis();

            System.out.println("✅ Entrenamiento completado en: " + (endTime - startTime) + " ms");

            // Evaluaciones detalladas
            System.out.println("\n" + "=".repeat(50));
            System.out.println("📊 EVALUACIÓN EN TEST SET:");
            System.out.println("=".repeat(50));
            System.out.println(Evaluators.evaluateClassifier(neuralNet, testSet));

            System.out.println("\n" + "=".repeat(50));
            System.out.println("🎯 EVALUACIÓN SOLO EN MENSAJES SPAM:");
            System.out.println("=".repeat(50));
            System.out.println(Evaluators.evaluateClassifier(neuralNet, spamOnlySet));

            // Análisis de características importantes (simulado)
            analyzeSparsity(trainingSet);

            // Guardar modelo
            String modelPath = "spam/src/main/resources/models/optimized_spam_classifier.json";
            new File(modelPath).getParentFile().mkdirs();
            FileIO.writeToFileAsJson(neuralNet, modelPath);
            System.out.println("\n💾 Modelo optimizado guardado en: " + modelPath);

            // Estadísticas finales
            printPerformanceStats(trainingSet, testSet);

        } catch (Exception e) {
            System.err.println("❌ Error durante el entrenamiento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Validación mejorada de datasets
     */
    private static void validateDataset(DataSet<MLDataItem> dataset, String datasetName) {
        System.out.println("\n🔍 Validando " + datasetName + "...");
        int invalidCount = 0;
        int totalSamples = 0;
        int zeroCount = 0;
        int totalFeatures = 0;

        for (MLDataItem item : dataset) {
            totalSamples++;
            float[] inputs = item.getInput().getValues();

            for (float val : inputs) {
                totalFeatures++;
                if (Float.isNaN(val) || Float.isInfinite(val)) {
                    invalidCount++;
                    System.out.println("⚠️ Valor inválido en muestra " + totalSamples + ": " + val);
                }
                if (val == 0.0f) {
                    zeroCount++;
                }
            }
        }

        double sparsityRatio = (double) zeroCount / totalFeatures * 100;
        System.out.println("✅ " + datasetName + " - Muestras: " + totalSamples);
        System.out.println("📈 Sparsity: " + String.format("%.2f", sparsityRatio) + "% (valores cero)");
        System.out.println("❌ Valores inválidos: " + invalidCount);
    }

    /**
     * Análisis de dispersión del dataset
     */
    private static void analyzeSparsity(DataSet<MLDataItem> trainingSet) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📈 ANÁLISIS DE SPARSITY DEL DATASET");
        System.out.println("=".repeat(50));

        // Contar características activas por muestra
        int[] activeFeatures = new int[10]; // Buckets para histograma
        int totalSamples = 0;

        for (MLDataItem item : trainingSet) {
            totalSamples++;
            float[] inputs = item.getInput().getValues();
            int activeCount = 0;

            for (float val : inputs) {
                if (val != 0.0f) activeCount++;
            }

            // Crear histograma simple
            int bucket = Math.min(activeCount / 50, 9); // Buckets de 50 características
            activeFeatures[bucket]++;
        }

        System.out.println("Distribución de características activas por mensaje:");
        for (int i = 0; i < activeFeatures.length; i++) {
            int rangeStart = i * 50;
            int rangeEnd = (i + 1) * 50 - 1;
            if (i == activeFeatures.length - 1) rangeEnd = Integer.MAX_VALUE;

            System.out.println(String.format("  %3d-%3s características: %4d mensajes (%.1f%%)",
                    rangeStart,
                    rangeEnd == Integer.MAX_VALUE ? "461" : String.valueOf(rangeEnd),
                    activeFeatures[i],
                    (double) activeFeatures[i] / totalSamples * 100));
        }
    }

    /**
     * Estadísticas de rendimiento
     */
    private static void printPerformanceStats(DataSet<MLDataItem> trainingSet, DataSet<MLDataItem> testSet) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📊 ESTADÍSTICAS DEL MODELO");
        System.out.println("=".repeat(50));

        int trainSize = 0;
        int testSize = 0;

        for (MLDataItem item : trainingSet) trainSize++;
        for (MLDataItem item : testSet) testSize++;

        System.out.println("🔢 Tamaño del training set: " + trainSize);
        System.out.println("🔢 Tamaño del test set: " + testSize);
        System.out.println("🎯 Características de entrada: 461");
        System.out.println("🏗️ Parámetros del modelo: ~77,000 (estimado)");
        System.out.println("⚡ Optimización: Diseñado para datos dispersos");
        System.out.println("🧠 Arquitectura: Progressive dimensionality reduction");
    }
}