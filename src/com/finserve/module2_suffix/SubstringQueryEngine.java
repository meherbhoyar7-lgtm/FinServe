package com.finserve.module2_suffix;

import com.finserve.util.JsonUtil;
import java.util.*;

/**
 * SubstringQueryEngine — Uses Suffix Automaton (SAM)
 * 
 * Financial Use Case: Answering substring queries on financial text data.
 * A suffix automaton is the smallest DFA that recognizes all suffixes of
 * a string, enabling O(m) substring checks and counting distinct substrings.
 * 
 * Algorithm: Online construction of suffix automaton. Each state represents
 * an equivalence class of substrings that occur in the same set of positions.
 * 
 * Time Complexity:  O(n) construction, O(m) per query
 * Space Complexity: O(n)
 */
public class SubstringQueryEngine {

    // Suffix Automaton state
    private static class SAMState {
        int len;                           // longest suffix length in this state
        int link;                          // suffix link
        Map<Character, Integer> next;      // transitions
        long count;                        // number of times this state's substrings occur
        boolean isClone;

        SAMState(int len, int link) {
            this.len = len;
            this.link = link;
            this.next = new HashMap<>();
            this.count = 0;
            this.isClone = false;
        }
    }

    private List<SAMState> states;
    private int last;

    public SubstringQueryEngine() {
        states = new ArrayList<>();
        states.add(new SAMState(0, -1)); // initial state
        last = 0;
    }

    /**
     * Extend the suffix automaton with one character.
     */
    public void extend(char c) {
        int cur = states.size();
        states.add(new SAMState(states.get(last).len + 1, -1));
        states.get(cur).count = 1;

        int p = last;
        while (p != -1 && !states.get(p).next.containsKey(c)) {
            states.get(p).next.put(c, cur);
            p = states.get(p).link;
        }

        if (p == -1) {
            states.get(cur).link = 0;
        } else {
            int q = states.get(p).next.get(c);
            if (states.get(p).len + 1 == states.get(q).len) {
                states.get(cur).link = q;
            } else {
                int clone = states.size();
                SAMState cloneState = new SAMState(states.get(p).len + 1, states.get(q).link);
                cloneState.next = new HashMap<>(states.get(q).next);
                cloneState.isClone = true;
                states.add(cloneState);

                while (p != -1 && states.get(p).next.getOrDefault(c, -1) == q) {
                    states.get(p).next.put(c, clone);
                    p = states.get(p).link;
                }
                states.get(q).link = clone;
                states.get(cur).link = clone;
            }
        }
        last = cur;
    }

    /**
     * Build suffix automaton from a string.
     */
    public static SubstringQueryEngine build(String text) {
        SubstringQueryEngine sam = new SubstringQueryEngine();
        for (char c : text.toCharArray()) {
            sam.extend(c);
        }
        return sam;
    }

    /**
     * Check if a substring exists in the text. O(m) query.
     */
    public boolean contains(String pattern) {
        int cur = 0;
        for (char c : pattern.toCharArray()) {
            if (!states.get(cur).next.containsKey(c)) return false;
            cur = states.get(cur).next.get(c);
        }
        return true;
    }

    /**
     * Count total number of distinct substrings.
     */
    public long countDistinctSubstrings() {
        long count = 0;
        for (int i = 1; i < states.size(); i++) {
            count += states.get(i).len - states.get(states.get(i).link).len;
        }
        return count;
    }

    /**
     * Find the longest common substring between the text and a query.
     */
    public String longestCommonSubstring(String query) {
        int cur = 0, len = 0, bestLen = 0, bestPos = 0;
        for (int i = 0; i < query.length(); i++) {
            char c = query.charAt(i);
            while (cur != 0 && !states.get(cur).next.containsKey(c)) {
                cur = states.get(cur).link;
                len = states.get(cur).len;
            }
            if (states.get(cur).next.containsKey(c)) {
                cur = states.get(cur).next.get(c);
                len++;
            }
            if (len > bestLen) {
                bestLen = len;
                bestPos = i - len + 1;
            }
        }
        return bestLen > 0 ? query.substring(bestPos, bestPos + bestLen) : "";
    }

    /**
     * Financial use case: Build a substring query engine on financial text
     * and answer various queries efficiently.
     */
    public static Map<String, Object> querySubstrings(String corpus, String query) {
        Map<String, Object> result = JsonUtil.buildResult(
            "Suffix Automaton (SAM)",
            "Financial Text Substring Queries",
            "O(n) build, O(m) per query", "O(n)"
        );

        String lower = corpus.toLowerCase();
        result.put("corpusLength", lower.length());

        long start = System.nanoTime();
        SubstringQueryEngine sam = build(lower);
        long buildTime = System.nanoTime() - start;

        result.put("buildTimeMs", buildTime / 1_000_000.0);
        result.put("totalStates", sam.states.size());
        result.put("distinctSubstrings", sam.countDistinctSubstrings());

        // Query check
        String queryLower = query.toLowerCase();
        start = System.nanoTime();
        boolean found = sam.contains(queryLower);
        long queryTime = System.nanoTime() - start;

        result.put("query", query);
        result.put("queryFound", found);
        result.put("queryTimeMs", queryTime / 1_000_000.0);

        // Longest common substring
        if (!found) {
            String lcs = sam.longestCommonSubstring(queryLower);
            result.put("longestMatchingSubstring", lcs);
            result.put("suggestion", "Did you mean: '" + lcs + "'?");
        }

        // Test several financial terms
        String[] testQueries = {"transaction", "payment", "transfer", "suspicious", "account", "wire"};
        List<Map<String, Object>> queryResults = new ArrayList<>();
        for (String tq : testQueries) {
            Map<String, Object> qr = new LinkedHashMap<>();
            qr.put("query", tq);
            qr.put("found", sam.contains(tq.toLowerCase()));
            queryResults.add(qr);
        }
        result.put("sampleQueries", queryResults);

        return result;
    }
}
