package com.finserve.module1_string;

import com.finserve.model.Transaction;
import com.finserve.util.JsonUtil;
import java.util.*;

/**
 * FinancialCodeLookup — Uses Rabin-Karp Algorithm
 * 
 * Financial Use Case: Searching for financial codes (SWIFT codes, IFSC codes,
 * routing numbers, payment references) across large transaction datasets
 * using rolling hash for efficient multi-pattern searching.
 * 
 * Algorithm: Rabin-Karp uses a rolling hash function to compare hash values
 * of the pattern with substrings of the text. Only when hashes match does
 * it perform a character-by-character comparison.
 * 
 * Time Complexity:  O(n + m) average, O(nm) worst case
 * Space Complexity: O(1)
 */
public class FinancialCodeLookup {

    private static final long PRIME = 1000000007L;
    private static final int BASE = 256;

    /**
     * Rabin-Karp search for a single pattern in text.
     * Uses rolling hash for efficient substring matching.
     */
    public static List<Integer> search(String text, String pattern) {
        List<Integer> positions = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty()) return positions;

        int n = text.length();
        int m = pattern.length();
        if (m > n) return positions;

        // Compute hash of pattern and first window
        long patternHash = 0;
        long textHash = 0;
        long highPow = 1; // BASE^(m-1) mod PRIME

        for (int i = 0; i < m - 1; i++) {
            highPow = (highPow * BASE) % PRIME;
        }

        for (int i = 0; i < m; i++) {
            patternHash = (patternHash * BASE + pattern.charAt(i)) % PRIME;
            textHash = (textHash * BASE + text.charAt(i)) % PRIME;
        }

        // Slide the window
        for (int i = 0; i <= n - m; i++) {
            if (patternHash == textHash) {
                // Verify character by character
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) positions.add(i);
            }

            // Compute hash for next window
            if (i < n - m) {
                textHash = ((textHash - text.charAt(i) * highPow) * BASE + text.charAt(i + m)) % PRIME;
                if (textHash < 0) textHash += PRIME;
            }
        }
        return positions;
    }

    /**
     * Financial use case: Lookup financial codes (SWIFT, IFSC, routing numbers)
     * across transaction records using Rabin-Karp rolling hash.
     */
    public static Map<String, Object> lookupFinancialCode(List<Transaction> transactions, String code) {
        Map<String, Object> result = JsonUtil.buildResult(
            "Rabin-Karp (Rolling Hash)",
            "Financial Code Lookup (SWIFT/IFSC/Routing Numbers)",
            "O(n + m) average", "O(1)"
        );

        result.put("searchCode", code);
        String codeLower = code.toLowerCase();

        // Compute pattern hash for display
        long patHash = 0;
        for (int i = 0; i < codeLower.length(); i++) {
            patHash = (patHash * BASE + codeLower.charAt(i)) % PRIME;
        }
        result.put("patternHash", patHash);
        result.put("hashPrime", PRIME);
        result.put("hashBase", BASE);

        List<Map<String, Object>> matches = new ArrayList<>();
        int hashComparisons = 0;
        int charComparisons = 0;

        for (Transaction txn : transactions) {
            String combined = (txn.getDescription() + " " + txn.getPaymentCode()).toLowerCase();
            int n = combined.length();
            int m = codeLower.length();
            if (m > n) continue;

            // Rabin-Karp with counting
            long pHash = 0, tHash = 0, hp = 1;
            for (int i = 0; i < m - 1; i++) hp = (hp * BASE) % PRIME;
            for (int i = 0; i < m; i++) {
                pHash = (pHash * BASE + codeLower.charAt(i)) % PRIME;
                tHash = (tHash * BASE + combined.charAt(i)) % PRIME;
            }

            boolean found = false;
            List<Integer> positions = new ArrayList<>();
            for (int i = 0; i <= n - m; i++) {
                hashComparisons++;
                if (pHash == tHash) {
                    boolean match = true;
                    for (int j = 0; j < m; j++) {
                        charComparisons++;
                        if (combined.charAt(i + j) != codeLower.charAt(j)) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        positions.add(i);
                        found = true;
                    }
                }
                if (i < n - m) {
                    tHash = ((tHash - combined.charAt(i) * hp) * BASE + combined.charAt(i + m)) % PRIME;
                    if (tHash < 0) tHash += PRIME;
                }
            }

            if (found) {
                Map<String, Object> match = new LinkedHashMap<>();
                match.put("transactionId", txn.getTransactionId());
                match.put("description", txn.getDescription());
                match.put("paymentCode", txn.getPaymentCode());
                match.put("amount", txn.getAmount());
                match.put("positions", positions);
                matches.add(match);
            }
        }

        result.put("matches", matches);
        result.put("totalMatches", matches.size());
        result.put("hashComparisons", hashComparisons);
        result.put("characterComparisons", charComparisons);
        result.put("spuriousHits", Math.max(0, charComparisons / Math.max(1, codeLower.length()) - matches.size()));

        return result;
    }
}
