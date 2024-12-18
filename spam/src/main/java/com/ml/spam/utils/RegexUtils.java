package com.ml.spam.utils;

public class RegexUtils {

    // Detecta dimensiones y medidas (kg, L, cm, m²)
    public static boolean isNumDim(String word) {
        return word.matches("\\b\\d+(\\.\\d+)?(kg|m²|cm|km|L|litros|ml)\\b");
    }

    // Detecta tiempo y fechas (ej.: 15 hs, 30 min, 12-06-2024)
    public static boolean isNumCal(String word) {
        return word.matches("\\b(\\d{1,2}[-/.]\\d{1,2}[-/.]\\d{2,4}|\\d+ (hs|min|días|años|meses))\\b");
    }

    // Detecta cantidades monetarias ($50, €100, 300 pesos)
    public static boolean isNumm(String word) {
        return word.matches("\\b\\d+(\\.\\d+)?(\\$|USD|€|euros|pesos|dólares)\\b");
    }

    // Detecta estadísticas y porcentajes (80%, promedio 4.5)
    public static boolean isNumStat(String word) {
        return word.matches("\\b\\d+(\\.\\d+)?(%|promedio|rating|calificación|valoración)\\b");
    }

    // Detecta códigos y referencias (ej.: REF123, 456789, códigos aleatorios)
    public static boolean isNumCod(String word) {
        return word.matches("\\b([A-Z0-9]{3,})\\b");
    }

    // Detecta números en URLs o enlaces (ej.: offer123.com, http://promo456.net)
    public static boolean isNumUrl(String word) {
        return word.matches("\\b(https?://|www\\.)\\S*\\d+\\S*\\b");
    }

    // Detecta números con formato de teléfono (ej.: +54 9 111 2222, 123-4567)
    public static boolean isNumTel(String word) {
        return word.matches("\\b(\\+?\\d{1,3}[- ]?)?(\\(?\\d{1,4}\\)?[- ]?)?\\d{3,4}[- ]\\d{3,4}\\b");
    }

    // Detecta direcciones IP (ej.: 192.168.0.1)
    public static boolean isNumIp(String word) {
        return word.matches("\\b(\\d{1,3}\\.){3}\\d{1,3}\\b");
    }

    // Detecta números pequeños y frecuencias (1, 2 intentos, 3 veces)
    public static boolean isNumLow(String word) {
        return word.matches("\\b\\d{1,2}\\b");
    }
}
