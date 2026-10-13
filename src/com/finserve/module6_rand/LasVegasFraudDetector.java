package com.finserve.module6_rand;

import com.finserve.util.JsonUtil;
import java.util.*;

/**
 * LasVegasFraudDetector — Las Vegas Randomised Algorithm
 * 
 * Financial Use Case: Detect fraudulent transactions using randomised
 * quickselect to find median/percentile thresholds, and randomised
 * partitioning for anomaly isolation.
 * 
 * Algorithm: Las Vegas (Randomised Quickselect + Randomised Partitioning)
 * - ALWAYS produces the correct answer
 * - Runtime is random variable, but EXPECTED O(n)
 * - Uses randomisation to avoid worst-case deterministic behavior
 * 
 * Time Complexity:  Expected O(n), Worst-case O(n²)
 * Space Complexity: O(1) in-place
 */
public class LasVegasFraudDetector {

    private static final Random rng = new Random(42);

    /**
     * Randomised Quickselect (Las Vegas): finds k-th smallest element.
     * Always returns correct answer; expected O(n) time.
     */
    public static double randomisedQuickselect(double[] arr, int lo, int hi, int k) {
        if (lo == hi) return arr[lo];

        // Randomised pivot selection (key Las Vegas technique)
        int pivotIdx = lo + rng.nextInt(hi - lo + 1);
        double pivot = arr[pivotIdx];

        // Swap pivot to end
        arr[pivotIdx] = arr[hi];
        arr[hi] = pivot;

        int storeIdx = lo;
        for (int i = lo; i < hi; i++) {
            if (arr[i] <= pivot) {
                double tmp = arr[i];
                arr[i] = arr[storeIdx];
                arr[storeIdx] = tmp;
                storeIdx++;
            }
        }
        arr[hi] = arr[storeIdx];
        arr[storeIdx] = pivot;

        if (k == storeIdx) return arr[storeIdx];
        else if (k < storeIdx) return randomisedQuickselect(arr, lo, storeIdx - 1, k);
        else return randomisedQuickselect(arr, storeIdx + 1, hi, k);
    }

    /**
     * Detect anomalous transactions using Las Vegas randomised selection.
     */
    public static Map<String, Object> detectFraud() {
        Map<String, Object> result = JsonUtil.buildResult(
            "Las Vegas Randomised Algorithm (Quickselect + Random Sampling)",
            "Statistical Anomaly-Based Fraud Detection",
            "Expected O(n)", "O(n)"
        );

        // Simulated transaction amounts (₹)
        double[] rawAmounts = {
            12500, 8700, 45000, 23100, 67800, 1200, 890000, 34500,
            15600, 2300, 78900, 5600, 1250000, 42000, 9800, 31200,
            55000, 7400, 125000, 460, 88000, 2100000, 17300, 63000,
            3200, 91000, 4800, 150000, 6700, 28500, 700, 3500000,
            41000, 19800, 52000, 11200, 74000, 1800, 96000, 38500
        };

        int n = rawAmounts.length;
        result.put("totalTransactions", n);

        // Step 1: Find median using Randomised Quickselect (Las Vegas)
        long startSelect = System.nanoTime();
        double[] copy1 = Arrays.copyOf(rawAmounts, n);
        double median = randomisedQuickselect(copy1, 0, n - 1, n / 2);
        long selectTime = System.nanoTime() - startSelect;

        // Step 2: Find Q1 (25th percentile) and Q3 (75th percentile)
        double[] copy2 = Arrays.copyOf(rawAmounts, n);
        double q1 = randomisedQuickselect(copy2, 0, n - 1, n / 4);
        double[] copy3 = Arrays.copyOf(rawAmounts, n);
        double q3 = randomisedQuickselect(copy3, 0, n - 1, 3 * n / 4);

        double iqr = q3 - q1;
        double lowerFence = q1 - 1.5 * iqr;
        double upperFence = q3 + 1.5 * iqr;

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("median", median);
        stats.put("q1_25thPercentile", q1);
        stats.put("q3_75thPercentile", q3);
        stats.put("interquartileRange", iqr);
        stats.put("lowerFence", Math.max(0, lowerFence));
        stats.put("upperFence", upperFence);
        stats.put("quickselectTimeNs", selectTime);
        stats.put("algorithm", "Randomised Quickselect — always correct, expected O(n)");
        result.put("statisticalThresholds", stats);

        // Step 3: Flag anomalies using IQR method
        List<Map<String, Object>> flagged = new ArrayList<>();
        List<Map<String, Object>> normal = new ArrayList<>();
        int flagCount = 0;

        for (int i = 0; i < n; i++) {
            double amt = rawAmounts[i];
            if (amt < lowerFence || amt > upperFence) {
                Map<String, Object> f = new LinkedHashMap<>();
                f.put("transactionIndex", i);
                f.put("amount", amt);
                f.put("deviationFromMedian", String.format("%.1fx", amt / median));
                f.put("anomalyType", amt > upperFence ? "Unusually HIGH" : "Unusually LOW");
                f.put("riskScore", Math.min(100, (int)(Math.abs(amt - median) / median * 50)));
                flagged.add(f);
                flagCount++;
            }
        }

        result.put("flaggedTransactions", flagged);
        result.put("flaggedCount", flagCount);
        result.put("normalCount", n - flagCount);
        result.put("flagRate", String.format("%.1f%%", (flagCount * 100.0) / n));

        // Las Vegas guarantee explanation
        Map<String, Object> guarantee = new LinkedHashMap<>();
        guarantee.put("classification", "Las Vegas algorithm — ALWAYS correct, runtime is probabilistic");
        guarantee.put("correctness", "Quickselect ALWAYS finds the exact k-th element (100% correct)");
        guarantee.put("expectedTime", "E[T(n)] = O(n) due to random pivot selection");
        guarantee.put("worstCase", "O(n²) with astronomically small probability (bad pivot every time)");
        guarantee.put("vsMonteCarloComparison", "Las Vegas = correct but random time; Monte Carlo = bounded time but approximate result");
        result.put("lasVegasGuarantee", guarantee);

        return result;
    }
}
