package com.ml.spam.dictionary.models;
public enum WordCategory {
    SPAM_WORDS,    // Palabras relacionadas con spam
    RARE_SYMBOLS,  // Símbolos raros
    STOP_WORDS,    // Palabras irrelevantes
    UNASSIGNED_WORDS      // Palabras nuevas NO ASIGNADAS TODAVÍA
}
