package com.finserve.module1_string;

import com.finserve.model.Transaction;
import com.finserve.util.JsonUtil;
import java.util.*;

/**
 * TransactionCodeSearch — Uses KMP (Knuth-Morris-Pratt) Algorithm
 * 
 * Financial Use Case: Efficiently searching for specific payment codes,
 * transaction IDs, or keywords in large volumes of transaction records.
 * 
 * Algorithm: KMP builds a partial-match (failure) table from the pattern,
 * enabling O(n + m) search by avoiding redundant comparisons.
 * 
 * Time Complexity:  O(n + m) — n = text length, m = pattern length
 * Space Complexity: O(m) — for the failure function table
 */
public class TransactionCodeSearch {

    /**
     * Build the KMP failure function (partial match table).
     * lps[i] = length of the longest proper prefix of pattern[0..i]
     * which is also a suffix of pattern[0..i].
     */
    public static int[] buildFailureFunction(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;
        lps[0] = 0;

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    /**
     * KMP search: find all occurrences of pattern in text.
     * Returns list of starting indices where pattern is found.
     */
    public static List<Integer> search(String text, String pattern) {
        List<Integer> positions = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty()) return positions;

        int n = text.length();
        int m = pattern.length();
        int[] lps = buildFailureFunction(pattern);

        int i = 0, j = 0;
        while (i < n) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            }
            if (j == m) {
                positions.add(i - j);
                j = lps[j - 1];
            } else if (i < n && text.charAt(i) != pattern.charAt(j)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return positions;
    }

    /**
     * Financial use case: Search for a payment code across all transaction descriptions.
     * Returns matching transactions and their positions.
     */
    public static Map<String, Object> searchTransactionCode(List<Transaction> transactions, String searchCode) {
        Map<String, Object> result = JsonUtil.buildResult(
            "KMP (Knuth-Morris-Pratt)",
            "Transaction and Payment Code Searching",
            "O(n + m)", "O(m)"
        );

        String searchLower = searchCode.toLowerCase();
        int[] lps = buildFailureFunction(searchLower);
        result.put("failureFunction", lps);
        result.put("searchPattern", searchCode);

        List<Map<String, Object>> matches = new ArrayList<>();
        int totalComparisons = 0;
        int bruteForceComparisons = 0;

        for (Transaction txn : transactions) {
            String desc = txn.getDescription().toLowerCase();
            String code = txn.getPaymentCode().toLowerCase();
            String combined = desc + " " + code;

            bruteForceComparisons += combined.length() * searchLower.length();

            List<Integer> positions = search(combined, searchLower);
            totalComparisons += combined.length() + searchLower.length();

            if (!positions.isEmpty()) {
                Map<String, Object> match = new LinkedHashMap<>();
                match.put("transactionId", txn.getTransactionId());
                match.put("description", txn.getDescription());
                match.put("paymentCode", txn.getPaymentCode());
                match.put("amount", txn.getAmount());
                match.put("status", txn.getStatus());
                match.put("matchPositions", positions);
                match.put("matchCount", positions.size());
                matches.add(match);
            }
        }

        result.put("matches", matches);
        result.put("totalMatches", matches.size());
        result.put("totalTransactionsSearched", transactions.size());
        result.put("kmpComparisons", totalComparisons);
        result.put("bruteForceComparisons", bruteForceComparisons);
        result.put("efficiencyGain", bruteForceComparisons > 0 ?
            String.format("%.1fx faster", (double) bruteForceComparisons / totalComparisons) : "N/A");

        return result;
    }
}
