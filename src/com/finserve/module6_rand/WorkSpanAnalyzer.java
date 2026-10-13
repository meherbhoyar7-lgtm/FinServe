package com.finserve.module6_rand;

import com.finserve.util.JsonUtil;
import java.util.*;

/**
 * WorkSpanAnalyzer — Comprehensive Work-Span Analysis
 * 
 * Financial Use Case: Analyze parallelism potential of various banking
 * batch operations. Computes work (W), span (S), parallelism (P = W/S),
 * and applies Brent's theorem to predict execution times on p processors.
 * 
 * Concepts:
 * - Work W(n):       total operations (sequential time)
 * - Span S(n):       critical path length (time with ∞ processors)
 * - Parallelism:     P = W/S (max useful processors)
 * - Brent's Theorem: T_p ≤ W/p + S
 * - Speedup:         T_1 / T_p
 * - Efficiency:      Speedup / p
 * 
 * Time Complexity: O(n) for each analysis
 * Space Complexity: O(1) per analysis
 */
public class WorkSpanAnalyzer {

    /**
     * Simulate a multi-stage banking pipeline and analyze its work-span properties.
     */
    public static Map<String, Object> analyzeWorkSpan() {
        Map<String, Object> result = JsonUtil.buildResult(
            "Work-Span Analysis (Brent's Theorem)",
            "Banking Batch Pipeline Parallelism Assessment",
            "Various — per algorithm", "O(1) analysis overhead"
        );

        // Define banking batch operations with their parallel characteristics
        List<Map<String, Object>> operations = new ArrayList<>();

        // Operation 1: Prefix Sum for running balances
        {
            Map<String, Object> op = new LinkedHashMap<>();
            op.put("operation", "Daily Running Balance Computation");
            op.put("algorithm", "Parallel Prefix Sum (Blelloch Scan)");
            int n = 100_000;
            long work = 2L * n;
            int span = 2 * (int)(Math.log(n) / Math.log(2));
            op.put("inputSize", n);
            op.put("work_W", work);
            op.put("span_S", span);
            op.put("parallelism_P", String.format("%.0f", (double)work / span));
            op.put("sequentialTimeMs", 5.0);
            addBrentAnalysis(op, work, span, new int[]{4, 8, 16, 64, 256});
            operations.add(op);
        }

        // Operation 2: Tree Reduce for aggregate statistics
        {
            Map<String, Object> op = new LinkedHashMap<>();
            op.put("operation", "Portfolio Aggregate Statistics");
            op.put("algorithm", "Parallel Tree Reduction");
            int n = 500_000;
            long work = n - 1;
            int span = (int)(Math.log(n) / Math.log(2));
            op.put("inputSize", n);
            op.put("work_W", work);
            op.put("span_S", span);
            op.put("parallelism_P", String.format("%.0f", (double)work / span));
            op.put("sequentialTimeMs", 12.0);
            addBrentAnalysis(op, work, span, new int[]{4, 8, 16, 64, 256});
            operations.add(op);
        }

        // Operation 3: Parallel Map (independent operations)
        {
            Map<String, Object> op = new LinkedHashMap<>();
            op.put("operation", "Transaction Fee Calculation (Embarrassingly Parallel)");
            op.put("algorithm", "Parallel Map / ForEach");
            int n = 1_000_000;
            long work = n;
            int span = 1; // fully independent
            op.put("inputSize", n);
            op.put("work_W", work);
            op.put("span_S", span);
            op.put("parallelism_P", String.format("%.0f", (double)work / span));
            op.put("sequentialTimeMs", 25.0);
            addBrentAnalysis(op, work, span, new int[]{4, 8, 16, 64, 256});
            operations.add(op);
        }

        // Operation 4: Merge Sort (divide-and-conquer)
        {
            Map<String, Object> op = new LinkedHashMap<>();
            op.put("operation", "Transaction Sorting by Timestamp");
            op.put("algorithm", "Parallel Merge Sort");
            int n = 200_000;
            long work = (long)(n * Math.log(n) / Math.log(2));
            int span = (int)(Math.pow(Math.log(n) / Math.log(2), 2)); // O(log²n) span
            op.put("inputSize", n);
            op.put("work_W", work);
            op.put("span_S", span);
            op.put("parallelism_P", String.format("%.0f", (double)work / span));
            op.put("sequentialTimeMs", 45.0);
            addBrentAnalysis(op, work, span, new int[]{4, 8, 16, 64, 256});
            operations.add(op);
        }

        result.put("pipelineOperations", operations);

        // Summary: End-to-end pipeline analysis
        Map<String, Object> pipeline = new LinkedHashMap<>();
        long totalWork = 0;
        int maxSpan = 0;
        double totalSeqTime = 0;
        for (Map<String, Object> op : operations) {
            totalWork += ((Number) op.get("work_W")).longValue();
            maxSpan += ((Number) op.get("span_S")).intValue();
            totalSeqTime += ((Number) op.get("sequentialTimeMs")).doubleValue();
        }
        pipeline.put("totalWork", totalWork);
        pipeline.put("totalSpan", maxSpan);
        pipeline.put("overallParallelism", String.format("%.0f", (double)totalWork / maxSpan));
        pipeline.put("totalSequentialTimeMs", totalSeqTime);

        // Projected times on various processor counts
        List<Map<String, Object>> projections = new ArrayList<>();
        for (int p : new int[]{1, 4, 8, 16, 64, 256}) {
            Map<String, Object> proj = new LinkedHashMap<>();
            proj.put("processors", p);
            double tP = totalSeqTime * ((1.0 / p) + ((double)maxSpan / totalWork));
            proj.put("projectedTimeMs", Math.round(tP * 100.0) / 100.0);
            proj.put("speedup", String.format("%.2fx", totalSeqTime / tP));
            proj.put("efficiency", String.format("%.1f%%", (totalSeqTime / tP / p) * 100));
            projections.add(proj);
        }
        pipeline.put("processorProjections", projections);

        result.put("endToEndPipeline", pipeline);

        // Theoretical comparison
        Map<String, Object> theory = new LinkedHashMap<>();
        theory.put("brentsTheorem", "T_p ≤ ⌈W/p⌉ + S — actual parallel time bounded by work distribution + critical path");
        theory.put("workEfficiency", "Algorithm is work-efficient if W_parallel = Θ(W_sequential)");
        theory.put("schedulingStrategy", "Greedy scheduler achieves T_p ≤ W/p + S on p processors");
        theory.put("diminishingReturns", "Adding processors beyond P = W/S provides no additional speedup");
        theory.put("amdalComparison", "Amdahl's Law focuses on sequential fraction; Work-Span considers algorithm structure");
        result.put("theoreticalFramework", theory);

        return result;
    }

    /**
     * Helper to add Brent's theorem analysis for different processor counts.
     */
    private static void addBrentAnalysis(Map<String, Object> op, long work, int span, int[] processorCounts) {
        List<Map<String, Object>> brent = new ArrayList<>();
        double seqTime = ((Number) op.get("sequentialTimeMs")).doubleValue();

        for (int p : processorCounts) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("processors_p", p);
            double tP = seqTime * ((1.0 / p) + ((double)span / work));
            entry.put("brentBoundMs", Math.round(tP * 1000.0) / 1000.0);
            entry.put("speedup", String.format("%.2fx", seqTime / tP));
            entry.put("efficiency", String.format("%.1f%%", (seqTime / tP / p) * 100));
            brent.add(entry);
        }
        op.put("brentProjections", brent);
    }
}
