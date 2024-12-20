package com.ml.spam.dictionary.models;
public enum LexCategory {
    LEX_DIM("lexdim"),       // Dimensiones y medidas (e.g., kilo, metro, litro)
    LEX_CAL("lexcal"),       // Fechas y calendarios (e.g., enero, lunes, 2024)
    LEX_MON("lexmon"),       // Monedas (e.g., dólar, peso, euro)
    LEX_SYM("lexsym"),       // Símbolos específicos (e.g., $, %)
    LEX_UNI("lexuni");       // Unidades de medida (e.g., kg, L, m²)

    private final String name;

    LexCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
