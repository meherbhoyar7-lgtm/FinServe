package com.finserve.module1_string;

import com.finserve.model.Transaction;
import com.finserve.util.JsonUtil;
import java.util.*;

/**
 * PatternAnalyzer — Uses Z-Function Algorithm
 * 
 * Financial Use Case: Finding repeated transaction-description patterns
 * to identify recurring payments, duplicate transactions, or systematic
 * transfer patterns that might indicate automated fraud.
 * 
 * Algorithm: The Z-function for a string S computes an array Z where Z[i]
 * is the length of the longest substring starting from S[i] which is also
 * a prefix of S. This enables efficient pattern matching in O(n + m) time.
 * 
 * Time Complexity:  O(n + m)
 * Space Complexity: O(n + m)
 */
public class PatternAnalyzer {

    /**
     * Compute the Z-array for the given string.
     * Z[i] = length of the longest substring starting at i
     * that matches a prefix of the string.
     */
    public static int[] computeZArray(String s) {
        int n = s.length();
        int[] z = new int[n];
        if (n == 0) return z;

        int left = 0, right = 0;
        for (int i = 1; i < n; i++) {
            if (i < right) {
                z[i] = Math.min(right - i, z[i - left]);
            }
            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) {
                z[i]++;
            }
            if (i + z[i] > right) {
                left = i;
                right = i + z[i];
            }
        }
        return z;
    }

    /**
     * Find all occurrences of pattern in text using Z-function.
     */
    public static List<Integer> findPattern(String text, String pattern) {
        List<Integer> positions = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty()) return positions;

        String combined = pattern + "$" + text;
        int[] z = computeZArray(combined);
        int patLen = pattern.length();

        for (int i = patLen + 1; i < combined.length(); i++) {
            if (z[i] == patLen) {
                positions.add(i - patLen - 1);
            }
        }
        return positions;
    }

    /**
     * Financial use case: Analyze transaction descriptions for repeated patterns.
     * Detects recurring payment descriptions that might indicate automated
     * transfers, subscription payments, or suspicious repetitive activity.
     */
    public static Map<String, Object> analyzePatterns(List<Transaction> transactions, String pattern) {
        Map<String, Object> result = JsonUtil.buildResult(
            "Z-Function",
            "Repeated Transaction-Description Pattern Detection",
            "O(n + m)", "O(n + m)"
        );

        result.put("searchPattern", pattern);

        // Search each transaction description
        List<Map<String, Object>> matches = new ArrayList<>();
        String patLower = pattern.toLowerCase();

        for (Transaction txn : transactions) {
            String desc = txn.getDescription().toLowerCase();
            List<Integer> positions = findPattern(desc, patLower);

            if (!positions.isEmpty()) {
                Map<String, Object> match = new LinkedHashMap<>();
                match.put("transactionId", txn.getTransactionId());
                match.put("description", txn.getDescription());
                match.put("amount", txn.getAmount());
                match.put("type", txn.getTransactionType());
                match.put("positions", positions);
                matches.add(match);
            }
        }

        result.put("matches", matches);
        result.put("totalMatches", matches.size());
        result.put("totalSearched", transactions.size());

        // Also find repeated substrings within concatenated descriptions
        StringBuilder corpus = new StringBuilder();
        for (Transaction txn : transactions) {
            corpus.append(txn.getDescription()).append(" | ");
        }
        String corpusStr = corpus.toString();

        // Show Z-array sample for educational purposes
        if (corpusStr.length() > 200) corpusStr = corpusStr.substring(0, 200);
        int[] zSample = computeZArray(corpusStr);
        int maxZ = 0;
        int maxZIndex = 0;
        for (int i = 1; i < zSample.length; i++) {
            if (zSample[i] > maxZ) {
                maxZ = zSample[i];
                maxZIndex = i;
            }
        }
        result.put("longestRepeatedPrefixLength", maxZ);
        if (maxZ > 0 && maxZIndex + maxZ <= corpusStr.length()) {
            result.put("longestRepeatedPrefix", corpusStr.substring(maxZIndex, maxZIndex + maxZ));
        }

        return result;
    }
}
