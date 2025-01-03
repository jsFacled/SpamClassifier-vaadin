package com.ml.spam.datasetProcessor.testersMain;

import com.ml.spam.dictionary.models.TokenType;
import com.ml.spam.utils.TextUtils;

public class RegexTest {

    public static void main(String[] args) {

        String[] tokens = {"5m2","casa123", "8cm3"};

        for (String token : tokens) {
            // Probar splitNumberAndText
            String[] parts = TextUtils.splitNumberAndText(token);
            String numberPart = parts[0];
            String textPart = parts[1];
            System.out.println("[DEBUG  processNumText ] token: <" + token + ">");
            System.out.println("[DEBUG  processNumText ] numberPart: <" + numberPart + ">");
            System.out.println("[DEBUG  processNumText ] textPart: <" + textPart + ">");

            // Probar isNumTextToken
            boolean isNumText = TextUtils.isNumTextToken(token);
            System.out.println("[DEBUG] isNumTextToken: " + isNumText);

            // Probar classifyToken
            TokenType tokenType = TextUtils.classifyToken(token);
            System.out.println("[DEBUG] classifyToken result: " + tokenType);

            System.out.println("------------------------------------------------");

        }
    }
}
