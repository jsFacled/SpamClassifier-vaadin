package com.ml.spam.dictionary.models;


public enum TokenType {
    NUM,              // Número puro
    TEXT,             // Palabra alfabética pura
    NUM_TEXT,         // Combinación de número y texto (e.g., "35hs")
    TEXT_NUM_SYMBOL,  // Combinación compleja de texto, números y símbolos (e.g., "!token--58//n")
    CHAR,             // Caracter único
    SYMBOL,           // Símbolo raro o especial (e.g., "@", "!")
    MIX,              // Token mixto no clasificable directamente
    UNASSIGNED        // Token que no pudo clasificarse
}
