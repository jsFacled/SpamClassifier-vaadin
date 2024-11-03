package com.ml.spam.service;

import org.springframework.stereotype.Service;
import com.ml.spam.mlmodel.Email;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.util.FileIO;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

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
        // Combinar asunto y mensaje para crear el contenido del correo
        String contenidoCorreo = asunto + " " + mensaje;

        // Crear el objeto Email con el contenido combinado
        Email email = new Email(contenidoCorreo);

        // Obtener el vector de características del email para clasificarlo
        float[] features = email.getClassifierInput();
        float output = spamClassifier.predict(features)[0]; // Predicción directa con FeedForwardNetwork

        // Interpretar el resultado de clasificación
        return (output > 0.5) ? "Spam" : "No Spam";
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
