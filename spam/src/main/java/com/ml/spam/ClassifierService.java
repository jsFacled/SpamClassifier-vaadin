package com.ml.spam;

import org.springframework.stereotype.Service;

@Service
public class ClassifierService {

    public String processEmail(String origen, String titulo, String texto) {
        // Lógica de clasificación básica
        if (texto.toLowerCase().contains("oferta") || texto.toLowerCase().contains("gana dinero")) {
            return "Este correo parece ser spam.";
        } else {
            return "Este correo parece ser legítimo.";
        }
    }
}
