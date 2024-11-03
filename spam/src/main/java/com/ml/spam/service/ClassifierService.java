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

    // Métod para clasificar un correo y devolver "Spam" o "No Spam"
    public String classifyEmail(String asunto, String mensaje) {
        float probability = calculateSpamProbability(asunto, mensaje);
        return (probability > 0.5) ? "Spam" : "No Spam";
    }

    // Métod para obtener la probabilidad de que un correo sea spam
    public float getSpamProbability(String asunto, String mensaje) {
        return calculateSpamProbability(asunto, mensaje);
    }

    // Métod privado para calcular la probabilidad de spam
    private float calculateSpamProbability(String asunto, String mensaje) {
        String contenidoCorreo = asunto + " " + mensaje;
        Email email = new Email(contenidoCorreo);

        // Obtener el vector de características
        float[] features = email.getClassifierInput();

        // Imprimir el vector de características para depuración
        System.out.println("Vector de características antes de normalización: " + Arrays.toString(features));

        // Validación de las características
        if (features == null || features.length == 0) {
            System.err.println("Error: El vector de características está vacío o es nulo.");
            return Float.NaN;
        }

        // Normalización simple (asegúrate de ajustar este paso si el modelo fue entrenado con un escalador específico)
        for (int i = 0; i < features.length; i++) {
            features[i] = features[i] / (1 + features[i]);  // Escalar a rango [0, 1]
        }
        System.out.println("Vector de características después de normalización: " + Arrays.toString(features));

        // Realizar la predicción y devolver la probabilidad de spam
        return spamClassifier.predict(features)[0];
    }
}
