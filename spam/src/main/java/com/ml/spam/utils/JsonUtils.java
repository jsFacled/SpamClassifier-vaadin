package com.ml.spam.utils;

import org.json.JSONArray;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JsonUtils {

    /**
     * Convierte un JSONArray en una lista de Strings.
     * @param jsonArray El JSONArray a convertir.
     * @return Una lista de Strings, o una lista vacía si el JSONArray es nulo.
     */
    public static List<String> jsonArrayToStringList(JSONArray jsonArray) {
        if (jsonArray == null) {
            return Collections.emptyList();
        }
        return jsonArray.toList().stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}
