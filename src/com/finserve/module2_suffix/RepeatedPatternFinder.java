package com.finserve.module2_suffix;

import com.finserve.util.JsonUtil;
import java.util.*;

/**
 * RepeatedPatternFinder — Uses Kasai's Algorithm for LCP Array
 * 
 * Financial Use Case: Finding repeated transaction patterns by computing
 * the Longest Common Prefix (LCP) array from a suffix array. Helps identify
 * repeated transaction descriptions, duplicate entries, and systematic patterns.
 * 
 * Algorithm: Kasai's algorithm builds the LCP array in O(n) time using
 * the inverse suffix array. LCP[i] gives the length of the longest common
 * prefix between suffix SA[i] and SA[i-1].
 * 
 * Time Complexity:  O(n)
 * Space Complexity: O(n)
 */
public class RepeatedPatternFinder {

    /**
     * Kasai's algorithm to build LCP array from suffix array.
     * LCP[i] = length of longest common prefix between
     * the suffix at SA[i] and the suffix at SA[i-1].
     */
    public static int[] buildLCPArray(String text, int[] sa) {
        int n = text.length();
        int[] rank = new int[n];
        int[] lcp = new int[n];

        // Build inverse suffix array (rank)
        for (int i = 0; i < n; i++) {
            rank[sa[i]] = i;
        }

        int h = 0;
        for (int i = 0; i < n; i++) {
            if (rank[i] > 0) {
                int j = sa[rank[i] - 1];
                while (i + h < n && j + h < n && text.charAt(i + h) == text.charAt(j + h)) {
                    h++;
                }
                lcp[rank[i]] = h;
                if (h > 0) h--;
            } else {
                h = 0;
            }
        }
        return lcp;
    }

    /**
     * Find the top-K longest repeated substrings using the LCP array.
     */
    public static List<Map<String, Object>> findLongestRepeatedSubstrings(String text, int[] sa, int[] lcp, int topK) {
        List<Map<String, Object>> results = new ArrayList<>();

        // Create index-lcp pairs and sort by LCP value descending
        Integer[] indices = new Integer[lcp.length];
        for (int i = 0; i < lcp.length; i++) indices[i] = i;

        Arrays.sort(indices, (a, b) -> Integer.compare(lcp[b], lcp[a]));

        Set<String> seen = new HashSet<>();
        for (int idx = 0; idx < indices.length && results.size() < topK; idx++) {
            int i = indices[idx];
            if (lcp[i] <= 2) continue; // skip trivially short matches

            String repeated = text.substring(sa[i], Math.min(sa[i] + lcp[i], text.length()));
            if (!seen.contains(repeated) && repeated.trim().length() > 2) {
                seen.add(repeated);
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("pattern", repeated);
                entry.put("length", lcp[i]);
                entry.put("position1", sa[i]);
                entry.put("position2", sa[i - 1]);
                results.add(entry);
            }
        }
        return results;
    }

    /**
     * Financial use case: Find repeated patterns in transaction data
     * to identify duplicate transactions, recurring patterns, or systematic behavior.
     */
    public static Map<String, Object> findRepeatedPatterns(String corpus) {
        Map<String, Object> result = JsonUtil.buildResult(
            "Kasai's Algorithm (LCP Array)",
            "Repeated Transaction Pattern Detection",
            "O(n) for LCP construction", "O(n)"
        );

        String lower = corpus.toLowerCase();
        result.put("corpusLength", lower.length());

        // Build suffix array
        long start = System.nanoTime();
        int[] sa = TransactionIndexer.buildSuffixArray(lower);
        long saBuildTime = System.nanoTime() - start;

        // Build LCP array using Kasai's algorithm
        start = System.nanoTime();
        int[] lcp = buildLCPArray(lower, sa);
        long lcpBuildTime = System.nanoTime() - start;

        result.put("suffixArrayBuildMs", saBuildTime / 1_000_000.0);
        result.put("lcpArrayBuildMs", lcpBuildTime / 1_000_000.0);

        // Find longest repeated substrings
        List<Map<String, Object>> repeated = findLongestRepeatedSubstrings(lower, sa, lcp, 10);
        result.put("topRepeatedPatterns", repeated);
        result.put("totalRepeatedFound", repeated.size());

        // LCP statistics
        int maxLCP = 0, sumLCP = 0;
        for (int v : lcp) {
            maxLCP = Math.max(maxLCP, v);
            sumLCP += v;
        }
        result.put("maxLCPValue", maxLCP);
        result.put("averageLCP", lcp.length > 0 ? (double) sumLCP / lcp.length : 0);

        // Show sample of LCP array
        List<Map<String, Object>> lcpSample = new ArrayList<>();
        for (int i = 1; i < Math.min(10, sa.length); i++) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("rank", i);
            entry.put("lcpValue", lcp[i]);
            int end1 = Math.min(sa[i] + 30, lower.length());
            int end2 = Math.min(sa[i - 1] + 30, lower.length());
            entry.put("suffix_i", lower.substring(sa[i], end1) + "...");
            entry.put("suffix_i_minus_1", lower.substring(sa[i - 1], end2) + "...");
            if (lcp[i] > 0) {
                entry.put("commonPrefix", lower.substring(sa[i], Math.min(sa[i] + lcp[i], lower.length())));
            }
            lcpSample.add(entry);
        }
        result.put("lcpArraySample", lcpSample);

        return result;
    }
}
