package com.ml.spam.dictionary.models;


public enum TokenType {
    NUM,              // Número puro
    TEXT,             // Palabra alfabética pura
    TEXT_SYMBOL,      // Palabra con Símbolos (e.g., "Ahora!")
    NUM_TEXT,         // Combinación de número y texto (e.g., "35hs")
    NUM_SYMBOL,       // Número con Símbolos (e.g., "$100")
    TEXT_NUM_SYMBOL,  // Combinación compleja de texto, números y símbolos (e.g., "!token--58//n")
    CHAR,             // Caracter único
    SYMBOL,           // Símbolo raro o especial (e.g., "@", "!")
    UNASSIGNED        // Token que no pudo clasificarse
}
