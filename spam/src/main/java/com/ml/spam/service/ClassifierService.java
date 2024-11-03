package com.ml.spam.service;

import org.springframework.stereotype.Service;
import com.ml.spam.mlmodel.Email;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.util.FileIO;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Service
public class ClassifierService {

    private final FeedForwardNetwork spamClassifier;
    private float threshold = 0.5f; // valor por defecto

    // Métod para cambiar el valor del umbral
    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    // Constructor que carga el modelo entrenado desde un archivo JSON
    public ClassifierService() throws IOException, ClassNotFoundException {
        InputStream modelStream = getClass().getClassLoader().getResourceAsStream("models/feedforward_spam_classifier.json");

        if (modelStream == null) {
            throw new IOException("No se encontró el archivo de modelo en el classpath.");
        }

        String jsonString = new String(modelStream.readAllBytes(), StandardCharsets.UTF_8);
        spamClassifier = (FeedForwardNetwork) FileIO.createFromJson(new JSONObject(jsonString));

        System.out.println("Modelo cargado exitosamente desde el archivo .json");
    }

    // Método para clasificar un correo y devolver "Spam" o "No Spam"
    public String classifyEmail(String asunto, String mensaje) {
        float probability = calculateSpamProbability(asunto, mensaje);
        return (probability > threshold) ? "Spam" : "No Spam";
    }

    // Método para obtener la probabilidad de que un correo sea spam
    public float getSpamProbability(String asunto, String mensaje) {
        return calculateSpamProbability(asunto, mensaje);
    }

    // Método privado para calcular la probabilidad de spam
    private float calculateSpamProbability(String asunto, String mensaje) {
        String contenidoCorreo = asunto + " " + mensaje;
        Email email = new Email(contenidoCorreo);

        // Obtener el vector de características sin normalizar
        float[] features = email.getClassifierInput();

        // Verificar el vector de características antes de la normalización
        System.out.println("Vector de características antes de normalización: " + Arrays.toString(features));

        // Aplicar normalización y obtener el vector normalizado
        float[] normalizedFeatures = normalizeFeatures(features);

        // Verificar el vector de características después de la normalización
        System.out.println("Vector de características después de normalización: " + Arrays.toString(normalizedFeatures));

        // Realizar la predicción y devolver la probabilidad de spam
        return spamClassifier.predict(normalizedFeatures)[0];
    }

    // Método separado para normalizar las características
    private float[] normalizeFeatures(float[] features) {
        float[] normalized = new float[features.length];
        for (int i = 0; i < features.length; i++) {
            normalized[i] = features[i] / (1 + features[i]);  // Escalar al rango [0, 1]
        }
        return normalized;
    }
}
