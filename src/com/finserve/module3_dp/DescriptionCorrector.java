package com.finserve.module3_dp;

import com.finserve.util.JsonUtil;
import java.util.*;

/**
 * DescriptionCorrector — Uses Levenshtein / Damerau-Levenshtein Distance
 * 
 * Financial Use Case: Correcting misspelled transaction descriptions and
 * suggesting the closest valid description. Also used for fuzzy matching
 * of beneficiary names and account details.
 * 
 * Algorithms:
 * - Levenshtein: insert, delete, substitute operations
 * - Damerau-Levenshtein: adds transposition of adjacent characters
 * 
 * Time Complexity:  O(m × n)
 * Space Complexity: O(m × n) for full matrix, O(min(m,n)) optimized
 */
public class DescriptionCorrector {

    /**
     * Standard Levenshtein edit distance.
     * Operations: insert (cost 1), delete (cost 1), substitute (cost 1)
     */
    public static int levenshteinDistance(String s1, String s2) {
        int m = s1.length(), n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                    Math.min(dp[i - 1][j] + 1,         // deletion
                             dp[i][j - 1] + 1),         // insertion
                    dp[i - 1][j - 1] + cost              // substitution
                );
            }
        }
        return dp[m][n];
    }

    /**
     * Damerau-Levenshtein distance (optimal string alignment variant).
     * Adds transposition of two adjacent characters.
     */
    public static int damerauLevenshteinDistance(String s1, String s2) {
        int m = s1.length(), n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                    Math.min(dp[i - 1][j] + 1,
                             dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                );

                // Transposition
                if (i > 1 && j > 1 &&
                    s1.charAt(i - 1) == s2.charAt(j - 2) &&
                    s1.charAt(i - 2) == s2.charAt(j - 1)) {
                    dp[i][j] = Math.min(dp[i][j], dp[i - 2][j - 2] + cost);
                }
            }
        }
        return dp[m][n];
    }

    /**
     * Get the DP matrix for visualization.
     */
    public static int[][] getEditDistanceMatrix(String s1, String s2) {
        int m = s1.length(), n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                    Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp;
    }

    /**
     * Trace back through DP matrix to find the edit operations.
     */
    public static List<String> traceOperations(String s1, String s2) {
        int m = s1.length(), n = s2.length();
        int[][] dp = getEditDistanceMatrix(s1, s2);
        List<String> ops = new ArrayList<>();

        int i = m, j = n;
        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && s1.charAt(i - 1) == s2.charAt(j - 1)) {
                ops.add(0, "MATCH '" + s1.charAt(i - 1) + "'");
                i--; j--;
            } else if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + 1) {
                ops.add(0, "SUBSTITUTE '" + s1.charAt(i - 1) + "' → '" + s2.charAt(j - 1) + "'");
                i--; j--;
            } else if (j > 0 && dp[i][j] == dp[i][j - 1] + 1) {
                ops.add(0, "INSERT '" + s2.charAt(j - 1) + "'");
                j--;
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + 1) {
                ops.add(0, "DELETE '" + s1.charAt(i - 1) + "'");
                i--;
            } else {
                break;
            }
        }
        return ops;
    }

    /**
     * Financial use case: Correct a misspelled transaction description
     * by finding the closest match from known valid descriptions.
     */
    public static Map<String, Object> correctDescription(String input, String[] validDescriptions) {
        Map<String, Object> result = JsonUtil.buildResult(
            "Levenshtein / Damerau-Levenshtein Distance",
            "Transaction Description Correction",
            "O(m × n)", "O(m × n)"
        );

        result.put("input", input);
        String inputLower = input.toLowerCase();

        List<Map<String, Object>> suggestions = new ArrayList<>();
        String bestMatch = "";
        int bestDist = Integer.MAX_VALUE;

        for (String valid : validDescriptions) {
            String validLower = valid.toLowerCase();
            int levDist = levenshteinDistance(inputLower, validLower);
            int dlDist = damerauLevenshteinDistance(inputLower, validLower);

            if (levDist < bestDist) {
                bestDist = levDist;
                bestMatch = valid;
            }

            if (levDist <= inputLower.length() / 2) { // Only include if within reasonable distance
                Map<String, Object> s = new LinkedHashMap<>();
                s.put("description", valid);
                s.put("levenshteinDistance", levDist);
                s.put("damerauLevenshteinDistance", dlDist);
                s.put("similarity", String.format("%.1f%%",
                    (1.0 - (double) levDist / Math.max(inputLower.length(), validLower.length())) * 100));
                suggestions.add(s);
            }
        }

        // Sort by distance
        suggestions.sort((a, b) -> Integer.compare(
            (int) a.get("levenshteinDistance"),
            (int) b.get("levenshteinDistance")
        ));

        // Keep top 5
        if (suggestions.size() > 5) suggestions = suggestions.subList(0, 5);

        result.put("suggestions", suggestions);
        result.put("bestMatch", bestMatch);
        result.put("bestDistance", bestDist);

        // Show detailed operations for best match
        if (!bestMatch.isEmpty()) {
            List<String> operations = traceOperations(inputLower, bestMatch.toLowerCase());
            result.put("editOperations", operations);
        }

        return result;
    }
}
