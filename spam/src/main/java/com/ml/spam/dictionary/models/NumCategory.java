package com.ml.spam.dictionary.models;

public enum NumCategory {
    NUM_DIM("numdim"),       // Dimensiones y medidas (kg, L, cm, m²)
    NUM_CAL("numcal"),       // Tiempo y fechas (15 hs, 30 min, 12-06-2024)
    NUM_MONEY("numm"),       // Cantidades monetarias ($50, €100)
    NUM_STAT("numstat"),     // Estadísticas y porcentajes (80%, promedio 4.5)
    NUM_COD("numcod"),       // Códigos y referencias (REF123, 456789)
    NUM_URL("numurl"),       // Enlaces con números (http://offer123.com)
    NUM_TEL("numtel"),       // Números de teléfono (+54 9 111 2222)
    NUM_IP("numip"),         // Direcciones IP (192.168.0.1)
    NUM_LOW("numlow");       // Números pequeños (1, 3 veces, 2 intentos)

    private final String name;

    NumCategory(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
