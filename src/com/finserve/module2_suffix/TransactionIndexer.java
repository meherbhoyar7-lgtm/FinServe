package com.finserve.module2_suffix;

import com.finserve.util.JsonUtil;
import java.util.*;

/**
 * TransactionIndexer — Uses Suffix Array
 * 
 * Financial Use Case: Building a searchable index of transaction descriptions
 * for fast substring lookups. Suffix arrays provide a space-efficient
 * alternative to suffix trees for indexing financial text data.
 * 
 * Algorithm: Constructs a sorted array of all suffixes of a string.
 * Binary search on the suffix array enables O(m log n) substring queries.
 * 
 * Time Complexity:  O(n log²n) construction, O(m log n) query
 * Space Complexity: O(n)
 */
public class TransactionIndexer {

    private String text;
    private int[] suffixArray;

    public TransactionIndexer(String text) {
        this.text = text;
        this.suffixArray = buildSuffixArray(text);
    }

    /**
     * Build suffix array using O(n log²n) algorithm.
     * Sorts suffixes by doubling the comparison length at each step.
     */
    public static int[] buildSuffixArray(String s) {
        int n = s.length();
        Integer[] sa = new Integer[n];
        int[] rank = new int[n];
        int[] tmp = new int[n];

        for (int i = 0; i < n; i++) {
            sa[i] = i;
            rank[i] = s.charAt(i);
        }

        for (int gap = 1; gap < n; gap <<= 1) {
            final int g = gap;
            final int[] r = rank.clone();

            Comparator<Integer> cmp = (a, b) -> {
                if (r[a] != r[b]) return Integer.compare(r[a], r[b]);
                int ra = a + g < n ? r[a + g] : -1;
                int rb = b + g < n ? r[b + g] : -1;
                return Integer.compare(ra, rb);
            };

            Arrays.sort(sa, cmp);

            tmp[sa[0]] = 0;
            for (int i = 1; i < n; i++) {
                tmp[sa[i]] = tmp[sa[i - 1]] + (cmp.compare(sa[i], sa[i - 1]) != 0 ? 1 : 0);
            }
            System.arraycopy(tmp, 0, rank, 0, n);

            if (rank[sa[n - 1]] == n - 1) break;
        }

        int[] result = new int[n];
        for (int i = 0; i < n; i++) result[i] = sa[i];
        return result;
    }

    /**
     * Binary search on suffix array to find all occurrences of pattern.
     */
    public List<Integer> search(String pattern) {
        List<Integer> positions = new ArrayList<>();
        int lo = 0, hi = suffixArray.length - 1;

        // Find left boundary
        int left = -1;
        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            String suffix = text.substring(suffixArray[mid],
                    Math.min(suffixArray[mid] + pattern.length(), text.length()));
            int cmp = suffix.compareTo(pattern);
            if (cmp >= 0) {
                if (cmp == 0 || suffix.startsWith(pattern)) left = mid;
                hi = mid - 1;
            } else {
                lo = mid + 1;
            }
        }

        if (left == -1) return positions;

        // Find right boundary
        lo = left;
        hi = suffixArray.length - 1;
        int right = left;
        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            String suffix = text.substring(suffixArray[mid],
                    Math.min(suffixArray[mid] + pattern.length(), text.length()));
            if (suffix.startsWith(pattern)) {
                right = mid;
                lo = mid + 1;
            } else if (suffix.compareTo(pattern) > 0) {
                hi = mid - 1;
            } else {
                lo = mid + 1;
            }
        }

        for (int i = left; i <= right; i++) {
            positions.add(suffixArray[i]);
        }
        Collections.sort(positions);
        return positions;
    }

    /**
     * Financial use case: Build an index of transaction descriptions
     * and perform fast substring lookups.
     */
    public static Map<String, Object> indexAndSearch(String corpus, String query) {
        Map<String, Object> result = JsonUtil.buildResult(
            "Suffix Array",
            "Transaction-Description Indexing and Search",
            "O(n log²n) build, O(m log n) query", "O(n)"
        );

        long startTime = System.nanoTime();
        TransactionIndexer indexer = new TransactionIndexer(corpus.toLowerCase());
        long buildTime = System.nanoTime() - startTime;

        result.put("corpusLength", corpus.length());
        result.put("suffixArraySize", indexer.suffixArray.length);
        result.put("buildTimeMs", buildTime / 1_000_000.0);

        startTime = System.nanoTime();
        List<Integer> positions = indexer.search(query.toLowerCase());
        long queryTime = System.nanoTime() - startTime;

        result.put("query", query);
        result.put("matchPositions", positions);
        result.put("totalMatches", positions.size());
        result.put("queryTimeMs", queryTime / 1_000_000.0);

        // Show context around first few matches
        List<Map<String, Object>> matchContexts = new ArrayList<>();
        for (int i = 0; i < Math.min(5, positions.size()); i++) {
            int pos = positions.get(i);
            int contextStart = Math.max(0, pos - 30);
            int contextEnd = Math.min(corpus.length(), pos + query.length() + 30);
            Map<String, Object> ctx = new LinkedHashMap<>();
            ctx.put("position", pos);
            ctx.put("context", "..." + corpus.substring(contextStart, contextEnd) + "...");
            matchContexts.add(ctx);
        }
        result.put("matchContexts", matchContexts);

        // Show sample of suffix array (first 10 entries)
        List<Map<String, Object>> saSample = new ArrayList<>();
        for (int i = 0; i < Math.min(10, indexer.suffixArray.length); i++) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("rank", i);
            entry.put("suffixIndex", indexer.suffixArray[i]);
            int end = Math.min(indexer.suffixArray[i] + 40, corpus.length());
            entry.put("suffixPreview", corpus.substring(indexer.suffixArray[i], end) + "...");
            saSample.add(entry);
        }
        result.put("suffixArraySample", saSample);

        return result;
    }
}
