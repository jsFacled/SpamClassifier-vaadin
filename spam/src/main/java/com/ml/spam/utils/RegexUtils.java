package com.ml.spam.utils;

import com.ml.spam.config.RegexConfig;

import java.util.regex.Pattern;

public class RegexUtils {

    /**
     *  *   *   *   *   *   *   *   *   *   *   *   *   *   *
     ************ Tratamiento de números ********************
     *  *   *   *   *   *   *   *   *   *   *   *   *   *   *
     */

        // Dimensiones y medidas (e.g., 15kg, 3.5m, 12L)
        public static boolean isNumDim(String input) {
            return Pattern.matches(RegexConfig.NUM_DIM, input);
        }

        // Tiempos y fechas (e.g., 15:30, 30min, 12-06-2024)
        public static boolean isNumCal(String input) {
            return Pattern.matches(RegexConfig.NUM_CAL, input);
        }

        // Cantidades monetarias (e.g., $50, €100)
        public static boolean isNumMoney(String input) {
            return Pattern.matches(RegexConfig.NUM_MONEY, input);
        }

        // Estadísticas y porcentajes (e.g., 80%, promedio 4.5)
        public static boolean isNumStat(String input) {
            return Pattern.matches(RegexConfig.NUM_STAT, input);
        }

        // Códigos y referencias (e.g., REF123, SKU4567)
        public static boolean isNumCod(String input) {
            return Pattern.matches(RegexConfig.NUM_COD, input);
        }

        // Enlaces con números (e.g., http://offer123.com)
        public static boolean isNumUrl(String input) {
            return Pattern.matches(RegexConfig.NUM_URL, input);
        }

        // Números de teléfono (e.g., +54 9 111 2222)
        public static boolean isNumTel(String input) {
            return Pattern.matches(RegexConfig.NUM_TEL, input);
        }

        // Direcciones IP (e.g., 192.168.0.1)
        public static boolean isNumIp(String input) {
            return Pattern.matches(RegexConfig.NUM_IP, input);
        }

        // Números pequeños (e.g., 1 vez, 3 intentos)
        public static boolean isNumLow(String input) {
            return Pattern.matches(RegexConfig.NUM_LOW, input);
        }
    }
