package com.finserve.module3_dp;

import com.finserve.util.JsonUtil;
import java.util.*;

/**
 * AnalysisOptimizer — Uses Matrix-Chain Multiplication
 * 
 * Financial Use Case: Optimizing the order of chained financial analysis
 * operations (joins, aggregations, transformations) to minimize total
 * computational cost. Similar to database query optimization.
 * 
 * Algorithm: Classic DP for matrix chain multiplication.
 * dp[i][j] = minimum cost to multiply matrices i through j.
 * 
 * Time Complexity:  O(n³)
 * Space Complexity: O(n²)
 */
public class AnalysisOptimizer {

    /**
     * Find optimal parenthesization for matrix chain multiplication.
     * dimensions[i-1] x dimensions[i] gives the dimensions of matrix i.
     * 
     * @return minimum number of scalar multiplications
     */
    public static long[][] computeOptimalOrder(int[] dimensions) {
        int n = dimensions.length - 1; // number of matrices
        long[][] dp = new long[n][n];
        // dp[i][j] = min cost to multiply matrices i..j

        // Length of chain
        for (int len = 2; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;
                dp[i][j] = Long.MAX_VALUE;
                for (int k = i; k < j; k++) {
                    long cost = dp[i][k] + dp[k + 1][j] +
                                (long) dimensions[i] * dimensions[k + 1] * dimensions[j + 1];
                    dp[i][j] = Math.min(dp[i][j], cost);
                }
            }
        }
        return dp;
    }

    /**
     * Compute the split points for optimal parenthesization.
     */
    public static int[][] computeSplitPoints(int[] dimensions) {
        int n = dimensions.length - 1;
        long[][] dp = new long[n][n];
        int[][] split = new int[n][n];

        for (int len = 2; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;
                dp[i][j] = Long.MAX_VALUE;
                for (int k = i; k < j; k++) {
                    long cost = dp[i][k] + dp[k + 1][j] +
                                (long) dimensions[i] * dimensions[k + 1] * dimensions[j + 1];
                    if (cost < dp[i][j]) {
                        dp[i][j] = cost;
                        split[i][j] = k;
                    }
                }
            }
        }
        return split;
    }

    /**
     * Build the optimal parenthesization string.
     */
    public static String buildParenthesization(int[][] split, String[] names, int i, int j) {
        if (i == j) return names[i];
        return "(" + buildParenthesization(split, names, i, split[i][j]) +
               " × " + buildParenthesization(split, names, split[i][j] + 1, j) + ")";
    }

    /**
     * Compute naive left-to-right cost for comparison.
     */
    public static long naiveCost(int[] dimensions) {
        long cost = 0;
        // Left to right: ((A1 × A2) × A3) × A4 ...
        int rows = dimensions[0];
        for (int i = 1; i < dimensions.length - 1; i++) {
            cost += (long) rows * dimensions[i] * dimensions[i + 1];
            // Result dimensions: rows × dimensions[i+1]
        }
        return cost;
    }

    /**
     * Financial use case: Optimize the order of chained financial analysis operations.
     * Each operation is represented as a matrix multiplication where dimensions
     * represent data sizes (rows = records, columns = attributes).
     */
    public static Map<String, Object> optimizeAnalysis() {
        Map<String, Object> result = JsonUtil.buildResult(
            "Matrix-Chain Multiplication (DP)",
            "Optimizing Chained Financial Analysis Operations",
            "O(n³)", "O(n²)"
        );

        // Financial analysis operations as matrix dimensions
        String[] operationNames = {
            "Transaction Filter",
            "Account Join",
            "Risk Aggregation",
            "Compliance Check",
            "Report Generation"
        };

        // Dimensions: each operation produces output of certain size
        // operations[i] takes input of dim[i] x dim[i+1]
        int[] dimensions = {1000, 500, 200, 100, 50, 10};

        result.put("operations", operationNames);

        // Show operation dimensions
        List<Map<String, Object>> opDims = new ArrayList<>();
        for (int i = 0; i < operationNames.length; i++) {
            Map<String, Object> op = new LinkedHashMap<>();
            op.put("operation", operationNames[i]);
            op.put("inputSize", dimensions[i] + " × " + dimensions[i + 1]);
            opDims.add(op);
        }
        result.put("operationDimensions", opDims);

        // Compute optimal order
        long[][] dpTable = computeOptimalOrder(dimensions);
        int[][] splitPoints = computeSplitPoints(dimensions);

        int n = operationNames.length;
        long optimalCost = dpTable[0][n - 1];
        long naiveCostVal = naiveCost(dimensions);

        result.put("optimalCost", optimalCost);
        result.put("naiveLeftToRightCost", naiveCostVal);
        result.put("costSavings", naiveCostVal - optimalCost);
        result.put("savingsPercentage",
            String.format("%.1f%%", (1.0 - (double) optimalCost / naiveCostVal) * 100));

        // Optimal parenthesization
        String parenthesization = buildParenthesization(splitPoints, operationNames, 0, n - 1);
        result.put("optimalOrder", parenthesization);

        // DP table for visualization
        List<List<Long>> dpDisplay = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            List<Long> row = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                row.add(dpTable[i][j]);
            }
            dpDisplay.add(row);
        }
        result.put("dpTable", dpDisplay);

        return result;
    }
}
