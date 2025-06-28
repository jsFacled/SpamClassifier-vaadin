package com.ml.spam.service;

import org.springframework.stereotype.Service;
import com.ml.spam.mlmodel.Email;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.util.FileIO;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClassifierService {

    private final FeedForwardNetwork spamClassifier;

    /**
     * Constructor que carga el modelo entrenado de clasificación desde un archivo JSON
     * ubicado en el classpath (src/main/resources/models).
     *
     * @throws IOException Si ocurre un error al leer el archivo.
     * @throws ClassNotFoundException Si hay problemas al cargar el modelo.
     */
    public ClassifierService() throws IOException, ClassNotFoundException {
        // Cargar el archivo JSON del modelo desde el classpath usando un InputStream
        InputStream modelStream = getClass().getClassLoader().getResourceAsStream("models/feedforward_spam_classifier.json");

        // Validar que el archivo realmente exista en el classpath
        if (modelStream == null) {
            throw new IOException("No se encontró el archivo de modelo en el classpath.");
        }

        // Leer el contenido del InputStream y convertirlo a String
        String jsonString = new String(modelStream.readAllBytes(), StandardCharsets.UTF_8);

        // Convertir el String a un JSONObject y cargar el modelo desde JSON
        spamClassifier = (FeedForwardNetwork) FileIO.createFromJson(new JSONObject(jsonString));

        System.out.println("Modelo cargado exitosamente desde el archivo .json");
    }

    /**
     * Clasifica un correo electrónico como "Spam" o "No Spam" basado en el contenido
     * del asunto y mensaje.
     *
     * @param remitente El remitente del correo (no utilizado en la clasificación actual).
     * @param asunto El asunto del correo electrónico.
     * @param mensaje El contenido del mensaje del correo electrónico.
     * @return "Spam" si el correo es clasificado como spam, "No Spam" en caso contrario.
     */
    public String classifyEmail(String remitente, String asunto, String mensaje) {
        try {
            String contenidoCorreo = asunto + " " + mensaje;
            float[] features = extractFeaturesWithSentencePiece(contenidoCorreo);
            float output = spamClassifier.predict(features)[0];
            System.out.println("Salida del modelo: " + output);

            return (output >= 0.1) ? "Spam" : "No Spam";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al clasificar";
        }
    }

    private float[] extractFeaturesWithSentencePiece(String texto) throws IOException, InterruptedException {
        String pythonScript = "spam/src/main/resources/sentencepiece-tokenizer/tokenizar_mensaje_individual.py";
        String modelPath = "F:\\JAVA GENERAL\\MACHINE LEARNING JAVA\\Código-ejemplos-intellij\\Clasificador Spam\\SpamClassifier-vaadin\\messages_spamham_tokenizer.model"; // o la ruta absoluta si lo preferís

        // Construir proceso
        ProcessBuilder pb = new ProcessBuilder("python", pythonScript, modelPath, texto);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        // Leer salida
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = reader.readLine(); // esperamos una línea tipo: 54,22,1,...
        int exitCode = process.waitFor();

        if (exitCode != 0 || line == null || line.contains("Traceback")) {
            throw new RuntimeException("Error en ejecución del tokenizador Python: " + line);
        }

        // Convertir tokens a float[], con padding a 80
        int inputSize = spamClassifier.getInputLayer().getWidth();
        float[] features = new float[inputSize];
        String[] parts = line.split(",");
        for (int i = 0; i < Math.min(parts.length, inputSize); i++) {
            features[i] = Float.parseFloat(parts[i]);
        }

        return features;
    }

    private float[] extractFeaturesWithCustomDictionary(String texto) {
        int inputSize = spamClassifier.getInputLayer().getWidth();//Obtiene la cantidad de columnas que surgieron del entrenamiento

        float[] features = new float[inputSize];

        // TODO: implementar lógica real usando diccionario y extracción de 462 features
        // Por ahora, devolver todo ceros
        return features;
    }

    /**
     * Método para probar el front únicamente. Determina si el correo parece ser spam
     * basado en palabras clave.
     *
     * @param origen El remitente del correo.
     * @param titulo El título del correo.
     * @param texto El contenido del mensaje.
     * @return Un mensaje indicando si el correo parece spam o legítimo.
     */
    public String processEmail(String origen, String titulo, String texto) {
        // Lógica simple para clasificar basado en palabras clave
        if (texto.toLowerCase().contains("oferta") || texto.toLowerCase().contains("gana dinero")) {
            return "Este correo parece ser spam.";
        } else {
            return "Este correo parece ser legítimo.";
        }
    }
}
