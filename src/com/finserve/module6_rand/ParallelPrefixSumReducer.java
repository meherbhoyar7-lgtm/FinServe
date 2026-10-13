package com.finserve.module6_rand;

import com.finserve.util.JsonUtil;
import java.util.*;

/**
 * ParallelPrefixSumReducer — Parallel Prefix Sum & Parallel Reduce
 * 
 * Financial Use Case: Compute running account balances (prefix sum),
 * aggregate portfolio statistics (parallel reduce), and perform
 * work-span analysis for parallelism efficiency.
 * 
 * Algorithms:
 * 1. Parallel Prefix Sum (Blelloch Scan) — O(n) work, O(log n) span
 * 2. Parallel Reduce (Tree Reduction)    — O(n) work, O(log n) span
 * 
 * Work-Span Model:
 * - Work W(n):  total number of operations (= sequential time)
 * - Span S(n):  longest critical path (= time with ∞ processors)
 * - Parallelism: P = W(n) / S(n)
 */
public class ParallelPrefixSumReducer {

    /**
     * Simulates Blelloch-style parallel prefix sum (exclusive scan).
     * Up-sweep (reduce) + Down-sweep phases.
     */
    public static double[] parallelPrefixSum(double[] input) {
        int n = input.length;
        // Pad to next power of 2 for clean binary tree
        int size = 1;
        while (size < n) size <<= 1;
        double[] tree = new double[size];
        System.arraycopy(input, 0, tree, 0, n);

        // Up-sweep (reduce phase) — builds partial sums up the tree
        for (int stride = 1; stride < size; stride <<= 1) {
            for (int i = 2 * stride - 1; i < size; i += 2 * stride) {
                tree[i] = tree[i - stride] + tree[i];
            }
        }

        // Down-sweep phase — distributes prefix sums down
        tree[size - 1] = 0;
        for (int stride = size >> 1; stride >= 1; stride >>= 1) {
            for (int i = 2 * stride - 1; i < size; i += 2 * stride) {
                double tmp = tree[i - stride];
                tree[i - stride] = tree[i];
                tree[i] = tree[i] + tmp;
            }
        }

        // Convert to inclusive prefix sum
        double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            result[i] = tree[i] + input[i];
        }
        return result;
    }

    /**
     * Simulates parallel tree reduction to compute aggregate statistics.
     * Returns sum, min, max simultaneously.
     */
    public static double[] parallelReduce(double[] input) {
        int n = input.length;
        // Reduce for sum
        double sum = 0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        // Simulate tree-based parallel reduction levels
        double[] currentSum = Arrays.copyOf(input, n);
        double[] currentMin = Arrays.copyOf(input, n);
        double[] currentMax = Arrays.copyOf(input, n);

        int levels = 0;
        int size = n;
        while (size > 1) {
            int half = (size + 1) / 2;
            for (int i = 0; i < size / 2; i++) {
                currentSum[i] = currentSum[2 * i] + currentSum[2 * i + 1];
                currentMin[i] = Math.min(currentMin[2 * i], currentMin[2 * i + 1]);
                currentMax[i] = Math.max(currentMax[2 * i], currentMax[2 * i + 1]);
            }
            if (size % 2 == 1) {
                currentSum[half - 1] = currentSum[size - 1];
                currentMin[half - 1] = currentMin[size - 1];
                currentMax[half - 1] = currentMax[size - 1];
            }
            size = half;
            levels++;
        }

        return new double[]{currentSum[0], currentMin[0], currentMax[0], levels};
    }

    /**
     * Financial use case: Compute running balances and portfolio analytics.
     */
    public static Map<String, Object> computeParallelAnalytics() {
        Map<String, Object> result = JsonUtil.buildResult(
            "Parallel Prefix Sum (Blelloch Scan) & Parallel Reduce (Tree Reduction)",
            "Running Balance Computation & Portfolio Aggregation",
            "O(n) work, O(log n) span", "O(n)"
        );

        // Daily transaction flows for a corporate account (₹ lakhs)
        String[] dates = {
            "2024-01-02", "2024-01-03", "2024-01-04", "2024-01-05",
            "2024-01-08", "2024-01-09", "2024-01-10", "2024-01-11",
            "2024-01-12", "2024-01-15", "2024-01-16", "2024-01-17",
            "2024-01-18", "2024-01-19", "2024-01-22", "2024-01-23"
        };
        double[] dailyFlows = {
            150.5, -45.2, 220.0, -180.3,
            95.7, -30.0, 310.5, -125.8,
            75.0, -210.5, 180.2, -55.0,
            425.0, -90.3, 65.8, -200.0
        };

        int n = dailyFlows.length;
        double openingBalance = 500.0; // ₹500 lakhs

        result.put("openingBalance", openingBalance);
        result.put("tradingDays", n);

        // ====== PARALLEL PREFIX SUM: Running Balances ======
        long startPrefixSum = System.nanoTime();
        double[] runningBalances = parallelPrefixSum(dailyFlows);
        long prefixSumTime = System.nanoTime() - startPrefixSum;

        // Adjust for opening balance
        for (int i = 0; i < n; i++) {
            runningBalances[i] += openingBalance;
        }

        List<Map<String, Object>> balanceEntries = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("date", dates[i]);
            entry.put("dailyFlow", dailyFlows[i]);
            entry.put("runningBalance", Math.round(runningBalances[i] * 100.0) / 100.0);
            entry.put("status", runningBalances[i] >= 0 ? "POSITIVE" : "OVERDRAWN");
            balanceEntries.add(entry);
        }

        Map<String, Object> prefixResult = new LinkedHashMap<>();
        prefixResult.put("algorithm", "Blelloch Parallel Prefix Sum (Exclusive Scan + Adjust)");
        prefixResult.put("phases", "Up-sweep (reduce) → Down-sweep (distribute)");
        prefixResult.put("runningBalances", balanceEntries);
        prefixResult.put("closingBalance", Math.round(runningBalances[n - 1] * 100.0) / 100.0);
        prefixResult.put("executionTimeNs", prefixSumTime);
        result.put("parallelPrefixSum", prefixResult);

        // ====== PARALLEL REDUCE: Aggregate Stats ======
        long startReduce = System.nanoTime();
        double[] reduceResult = parallelReduce(dailyFlows);
        long reduceTime = System.nanoTime() - startReduce;

        Map<String, Object> reduceStats = new LinkedHashMap<>();
        reduceStats.put("algorithm", "Tree-Based Parallel Reduction");
        reduceStats.put("totalNetFlow", Math.round(reduceResult[0] * 100.0) / 100.0);
        reduceStats.put("minDailyFlow", reduceResult[1]);
        reduceStats.put("maxDailyFlow", reduceResult[2]);
        reduceStats.put("reductionLevels", (int) reduceResult[3]);
        reduceStats.put("averageDailyFlow", Math.round((reduceResult[0] / n) * 100.0) / 100.0);
        reduceStats.put("executionTimeNs", reduceTime);
        result.put("parallelReduce", reduceStats);

        // ====== WORK-SPAN ANALYSIS ======
        int logN = (int) Math.ceil(Math.log(n) / Math.log(2));

        Map<String, Object> workSpan = new LinkedHashMap<>();
        workSpan.put("n", n);

        Map<String, Object> prefixAnalysis = new LinkedHashMap<>();
        prefixAnalysis.put("work_W", "O(n) = " + (2 * n) + " operations");
        prefixAnalysis.put("span_S", "O(log n) = " + (2 * logN) + " parallel steps");
        prefixAnalysis.put("parallelism_P", "W/S = " + String.format("%.1f", (2.0 * n) / (2 * logN)));
        prefixAnalysis.put("explanation", "With unlimited processors, prefix sum runs in O(log n) time");
        workSpan.put("prefixSum", prefixAnalysis);

        Map<String, Object> reduceAnalysis = new LinkedHashMap<>();
        reduceAnalysis.put("work_W", "O(n) = " + (n - 1) + " operations");
        reduceAnalysis.put("span_S", "O(log n) = " + logN + " parallel steps");
        reduceAnalysis.put("parallelism_P", "W/S = " + String.format("%.1f", (n - 1.0) / logN));
        reduceAnalysis.put("explanation", "Tree reduce halves active elements each level → logarithmic depth");
        workSpan.put("reduce", reduceAnalysis);

        workSpan.put("brentTheorem", "T_p ≤ W/p + S — actual time on p processors is bounded by work/processors + span");
        workSpan.put("maxParallelism", "P = W/S ≈ n/log(n) — the maximum useful number of processors");
        result.put("workSpanAnalysis", workSpan);

        return result;
    }
}
