package com.ml.spam.service;

import org.springframework.stereotype.Service;


import com.ml.spam.mlmodel.Email;
import deepnetts.net.FeedForwardNetwork;
import javax.visrec.ml.classification.BinaryClassifier;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

@Service
public class ClassifierService {


    private final BinaryClassifier<float[]> spamClassifier;

    // Constructor que carga el modelo entrenado
    public ClassifierService() throws IOException, ClassNotFoundException {
        // Cargar el modelo desde el archivo serializado
        try (FileInputStream fileIn = new FileInputStream("src/main/resources/models/feedforward_spam_classifier.ser");
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            spamClassifier = (BinaryClassifier<float[]>) in.readObject();
        }
    }

    // Método para clasificar el correo
    public String classifyEmail(String remitente, String asunto, String mensaje) {
        // Combinar asunto y mensaje para crear el contenido del correo
        String contenidoCorreo = asunto + " " + mensaje;

        // Crear el objeto Email con el contenido combinado
        Email email = new Email(contenidoCorreo);

        // Obtener el vector de características y clasificarlo
        float[] features = email.getClassifierInput();
        Float resultado = spamClassifier.classify(features);

        // Interpretar el resultado de clasificación
        return (resultado > 0.5) ? "Spam" : "No Spam";
    }

    //Métod para probar el front solamente.
    public String processEmail(String origen, String titulo, String texto) {
        // Lógica de clasificación básica
        if (texto.toLowerCase().contains("oferta") || texto.toLowerCase().contains("gana dinero")) {
            return "Este correo parece ser spam.";
        } else {
            return "Este correo parece ser legítimo.";
        }
    }
}
