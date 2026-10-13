package com.finserve.util;

import java.util.*;

/**
 * Lightweight JSON utility for serializing results without external dependencies.
 */
public class JsonUtil {

    public static String toJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) sb.append(",");
            first = false;
            sb.append("\"").append(escape(entry.getKey())).append("\":");
            sb.append(valueToJson(entry.getValue()));
        }
        sb.append("}");
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public static String valueToJson(Object value) {
        if (value == null) return "null";
        if (value instanceof String) return "\"" + escape((String) value) + "\"";
        if (value instanceof Number) return value.toString();
        if (value instanceof Boolean) return value.toString();
        if (value instanceof Map) return toJson((Map<String, Object>) value);
        if (value instanceof List) {
            List<?> list = (List<?>) value;
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(valueToJson(list.get(i)));
            }
            sb.append("]");
            return sb.toString();
        }
        if (value instanceof int[]) {
            int[] arr = (int[]) value;
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < arr.length; i++) {
                if (i > 0) sb.append(",");
                sb.append(arr[i]);
            }
            sb.append("]");
            return sb.toString();
        }
        if (value instanceof double[]) {
            double[] arr = (double[]) value;
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < arr.length; i++) {
                if (i > 0) sb.append(",");
                sb.append(arr[i]);
            }
            sb.append("]");
            return sb.toString();
        }
        if (value instanceof String[]) {
            String[] arr = (String[]) value;
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < arr.length; i++) {
                if (i > 0) sb.append(",");
                sb.append("\"").append(escape(arr[i])).append("\"");
            }
            sb.append("]");
            return sb.toString();
        }
        if (value instanceof boolean[][]) {
            boolean[][] arr = (boolean[][]) value;
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < arr.length; i++) {
                if (i > 0) sb.append(",");
                sb.append("[");
                for (int j = 0; j < arr[i].length; j++) {
                    if (j > 0) sb.append(",");
                    sb.append(arr[i][j]);
                }
                sb.append("]");
            }
            sb.append("]");
            return sb.toString();
        }
        return "\"" + escape(value.toString()) + "\"";
    }

    public static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "")
                .replace("\t", "\\t");
    }

    /**
     * Very simple JSON object parser for request bodies.
     * Handles flat JSON objects with string values.
     */
    public static Map<String, String> parseJsonObject(String json) {
        Map<String, String> result = new LinkedHashMap<>();
        if (json == null || json.trim().isEmpty()) return result;
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);

        int i = 0;
        while (i < json.length()) {
            // Skip whitespace
            while (i < json.length() && Character.isWhitespace(json.charAt(i))) i++;
            if (i >= json.length()) break;

            // Read key
            if (json.charAt(i) == '"') {
                int keyStart = ++i;
                while (i < json.length() && json.charAt(i) != '"') {
                    if (json.charAt(i) == '\\') i++;
                    i++;
                }
                String key = json.substring(keyStart, i);
                i++; // skip closing quote

                // Skip to colon
                while (i < json.length() && json.charAt(i) != ':') i++;
                i++; // skip colon

                // Skip whitespace
                while (i < json.length() && Character.isWhitespace(json.charAt(i))) i++;

                // Read value
                if (i < json.length()) {
                    if (json.charAt(i) == '"') {
                        int valStart = ++i;
                        StringBuilder val = new StringBuilder();
                        while (i < json.length() && json.charAt(i) != '"') {
                            if (json.charAt(i) == '\\' && i + 1 < json.length()) {
                                i++;
                                val.append(json.charAt(i));
                            } else {
                                val.append(json.charAt(i));
                            }
                            i++;
                        }
                        result.put(key, val.toString());
                        i++; // skip closing quote
                    } else {
                        // Number or boolean or null
                        int valStart = i;
                        while (i < json.length() && json.charAt(i) != ',' && json.charAt(i) != '}') i++;
                        result.put(key, json.substring(valStart, i).trim());
                    }
                }

                // Skip comma
                while (i < json.length() && (json.charAt(i) == ',' || Character.isWhitespace(json.charAt(i)))) i++;
            } else {
                i++;
            }
        }
        return result;
    }

    /**
     * Build a result map for API responses.
     */
    public static Map<String, Object> buildResult(String algorithm, String useCase,
                                                    String timeComplexity, String spaceComplexity) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("algorithm", algorithm);
        result.put("useCase", useCase);
        result.put("timeComplexity", timeComplexity);
        result.put("spaceComplexity", spaceComplexity);
        return result;
    }
}
