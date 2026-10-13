package com.finserve.module1_string;

import com.finserve.model.Transaction;
import com.finserve.util.JsonUtil;
import java.util.*;

/**
 * FraudPatternDetector — Uses Aho-Corasick Algorithm
 * 
 * Financial Use Case: Simultaneously detecting multiple fraud indicator
 * patterns in transaction descriptions. Unlike searching one pattern at a time,
 * Aho-Corasick searches for ALL patterns in a single pass.
 * 
 * Algorithm: Builds a trie of all patterns with failure links (similar to KMP
 * failure function but on a trie). Processes text in O(n + m + z) time where
 * z is the number of pattern matches found.
 * 
 * Time Complexity:  O(n + m + z) where n=text, m=total pattern chars, z=matches
 * Space Complexity: O(m * SIGMA) where SIGMA is alphabet size
 */
public class FraudPatternDetector {

    // Aho-Corasick automaton node
    private static class AhoNode {
        Map<Character, Integer> children = new HashMap<>();
        int failLink = 0;
        List<Integer> output = new ArrayList<>(); // pattern indices ending here
    }

    private List<AhoNode> automaton;
    private String[] patterns;

    public FraudPatternDetector(String[] patterns) {
        this.patterns = patterns;
        buildAutomaton(patterns);
    }

    /**
     * Build the Aho-Corasick automaton from the set of patterns.
     * Step 1: Build trie
     * Step 2: Compute failure links using BFS
     */
    private void buildAutomaton(String[] patterns) {
        automaton = new ArrayList<>();
        automaton.add(new AhoNode()); // root node

        // Step 1: Insert all patterns into trie
        for (int p = 0; p < patterns.length; p++) {
            int cur = 0;
            for (char c : patterns[p].toCharArray()) {
                if (!automaton.get(cur).children.containsKey(c)) {
                    automaton.get(cur).children.put(c, automaton.size());
                    automaton.add(new AhoNode());
                }
                cur = automaton.get(cur).children.get(c);
            }
            automaton.get(cur).output.add(p);
        }

        // Step 2: Build failure links via BFS
        Queue<Integer> queue = new LinkedList<>();
        for (Map.Entry<Character, Integer> entry : automaton.get(0).children.entrySet()) {
            automaton.get(entry.getValue()).failLink = 0;
            queue.add(entry.getValue());
        }

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (Map.Entry<Character, Integer> entry : automaton.get(u).children.entrySet()) {
                char c = entry.getKey();
                int v = entry.getValue();

                int f = automaton.get(u).failLink;
                while (f != 0 && !automaton.get(f).children.containsKey(c)) {
                    f = automaton.get(f).failLink;
                }
                automaton.get(v).failLink = automaton.get(f).children.getOrDefault(c, 0);
                if (automaton.get(v).failLink == v) automaton.get(v).failLink = 0;

                // Merge output
                automaton.get(v).output.addAll(automaton.get(automaton.get(v).failLink).output);

                queue.add(v);
            }
        }
    }

    /**
     * Search text for all patterns simultaneously.
     * Returns a map of pattern index -> list of positions found.
     */
    public Map<Integer, List<Integer>> search(String text) {
        Map<Integer, List<Integer>> results = new HashMap<>();
        int cur = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            while (cur != 0 && !automaton.get(cur).children.containsKey(c)) {
                cur = automaton.get(cur).failLink;
            }
            cur = automaton.get(cur).children.getOrDefault(c, 0);

            for (int patIdx : automaton.get(cur).output) {
                results.computeIfAbsent(patIdx, k -> new ArrayList<>())
                       .add(i - patterns[patIdx].length() + 1);
            }
        }
        return results;
    }

    /**
     * Financial use case: Scan all transaction descriptions for multiple
     * fraud indicator patterns simultaneously.
     */
    public static Map<String, Object> detectFraudPatterns(List<Transaction> transactions, String[] fraudPatterns) {
        Map<String, Object> result = JsonUtil.buildResult(
            "Aho-Corasick (Multi-Pattern Automaton)",
            "Multi-Pattern Fraud Detection in Transactions",
            "O(n + m + z)", "O(m × Σ)"
        );

        // Convert patterns to lowercase
        String[] patternsLower = new String[fraudPatterns.length];
        for (int i = 0; i < fraudPatterns.length; i++) {
            patternsLower[i] = fraudPatterns[i].toLowerCase();
        }

        result.put("fraudPatterns", fraudPatterns);
        result.put("totalPatterns", fraudPatterns.length);

        // Build automaton
        FraudPatternDetector detector = new FraudPatternDetector(patternsLower);
        result.put("automatonStates", detector.automaton.size());

        List<Map<String, Object>> flaggedTransactions = new ArrayList<>();
        Map<String, Integer> patternFrequency = new LinkedHashMap<>();
        for (String p : fraudPatterns) patternFrequency.put(p, 0);

        int totalPatternsFound = 0;

        for (Transaction txn : transactions) {
            String desc = txn.getDescription().toLowerCase();
            Map<Integer, List<Integer>> matches = detector.search(desc);

            if (!matches.isEmpty()) {
                Map<String, Object> flagged = new LinkedHashMap<>();
                flagged.put("transactionId", txn.getTransactionId());
                flagged.put("description", txn.getDescription());
                flagged.put("amount", txn.getAmount());
                flagged.put("status", txn.getStatus());
                flagged.put("type", txn.getTransactionType());

                List<Map<String, Object>> detectedPatterns = new ArrayList<>();
                int riskScore = 0;
                for (Map.Entry<Integer, List<Integer>> entry : matches.entrySet()) {
                    int patIdx = entry.getKey();
                    Map<String, Object> pd = new LinkedHashMap<>();
                    pd.put("pattern", fraudPatterns[patIdx]);
                    pd.put("positions", entry.getValue());
                    pd.put("occurrences", entry.getValue().size());
                    detectedPatterns.add(pd);

                    patternFrequency.merge(fraudPatterns[patIdx], entry.getValue().size(), Integer::sum);
                    riskScore += entry.getValue().size() * 20;
                    totalPatternsFound += entry.getValue().size();
                }

                flagged.put("detectedPatterns", detectedPatterns);
                flagged.put("riskScore", Math.min(riskScore, 100));
                flagged.put("riskLevel", riskScore >= 60 ? "HIGH" : riskScore >= 30 ? "MEDIUM" : "LOW");
                flaggedTransactions.add(flagged);
            }
        }

        result.put("flaggedTransactions", flaggedTransactions);
        result.put("totalFlagged", flaggedTransactions.size());
        result.put("totalPatternsFound", totalPatternsFound);
        result.put("patternFrequency", patternFrequency);

        // Efficiency comparison
        int singlePassOps = 0;
        for (Transaction t : transactions) singlePassOps += t.getDescription().length();
        int naiveOps = singlePassOps * fraudPatterns.length;
        result.put("ahoCorasickOperations", singlePassOps);
        result.put("naiveSeparateSearchOps", naiveOps);
        result.put("efficiencyGain", String.format("%.1fx faster", (double) naiveOps / Math.max(1, singlePassOps)));

        return result;
    }
}
