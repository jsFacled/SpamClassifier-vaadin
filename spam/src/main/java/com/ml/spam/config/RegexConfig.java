package com.ml.spam.config;

public class RegexConfig {

    public static final String NUM_DIM = "\\d+(\\.\\d+)?(kg|g|mg|cm|mm|m|km|L|m²|ft|in|yd|mi)";
    public static final String NUM_CAL = "\\b(?:\\d{1,2}[:hsm]?\\d{0,2}|\\d{1,2}-\\d{1,2}-\\d{4})\\b";
    public static final String NUM_MONEY = "[\\$€¥£]\\d+(\\.\\d{1,2})?";
    public static final String NUM_STAT = "\\b\\d+(\\.\\d+)?%|promedio\\s+\\d+(\\.\\d+)?\\b";
    public static final String NUM_COD = "\\b(?:REF|[A-Z]{2,5})\\d+\\b";
    public static final String NUM_URL = "https?://[\\w.-]*\\d+[\\w.-]*";
    public static final String NUM_TEL = "\\+?\\d{1,4}[\\s-]?\\d{1,4}[\\s-]?\\d{2,5}(?:[\\s-]\\d{2,5})*";
    public static final String NUM_IP = "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b";
    public static final String NUM_LOW = "\\b\\d{1,2}(\\s(?:veces|intentos|meses))?\\b";


}