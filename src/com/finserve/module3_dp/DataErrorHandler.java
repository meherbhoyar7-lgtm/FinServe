package com.finserve.module3_dp;

import com.finserve.util.JsonUtil;
import java.util.*;

/**
 * DataErrorHandler — Uses Weighted Edit Distance
 * 
 * Financial Use Case: Handling errors in financial data where different
 * types of errors have different costs. E.g., changing a digit in an
 * account number is more critical than fixing a typo in a name.
 * 
 * Algorithm: Modified edit distance with custom cost functions for
 * insert, delete, and substitute based on character type and position.
 * 
 * Time Complexity:  O(m × n)
 * Space Complexity: O(m × n)
 */
public class DataErrorHandler {

    /**
     * Weighted edit distance with custom cost functions.
     * Costs vary based on character type:
     * - Digits: higher cost (financial amounts, account numbers)
     * - Letters: moderate cost
     * - Special chars: lower cost
     */
    public static double weightedEditDistance(String s1, String s2,
                                              double digitSubCost, double letterSubCost,
                                              double insertCost, double deleteCost) {
        int m = s1.length(), n = s2.length();
        double[][] dp = new double[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i * deleteCost;
        for (int j = 0; j <= n; j++) dp[0][j] = j * insertCost;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1]; // match, no cost
                } else {
                    double subCost = getSubstitutionCost(s1.charAt(i - 1), s2.charAt(j - 1),
                                                          digitSubCost, letterSubCost);
                    dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + deleteCost,
                                 dp[i][j - 1] + insertCost),
                        dp[i - 1][j - 1] + subCost
                    );
                }
            }
        }
        return dp[m][n];
    }

    private static double getSubstitutionCost(char c1, char c2,
                                               double digitCost, double letterCost) {
        if (Character.isDigit(c1) || Character.isDigit(c2)) return digitCost;
        if (Character.isLetter(c1) && Character.isLetter(c2)) return letterCost;
        return 0.5; // special characters
    }

    /**
     * Get the weighted DP matrix for visualization.
     */
    public static double[][] getWeightedMatrix(String s1, String s2,
                                                 double digitSub, double letterSub,
                                                 double ins, double del) {
        int m = s1.length(), n = s2.length();
        double[][] dp = new double[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i * del;
        for (int j = 0; j <= n; j++) dp[0][j] = j * ins;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    double subCost = getSubstitutionCost(s1.charAt(i - 1), s2.charAt(j - 1),
                                                          digitSub, letterSub);
                    dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + del, dp[i][j - 1] + ins),
                        dp[i - 1][j - 1] + subCost
                    );
                }
            }
        }
        return dp;
    }

    /**
     * Financial use case: Compare erroneous financial data against correct
     * data with weighted costs.
     */
    public static Map<String, Object> handleDataError(String errorData, String correctData) {
        Map<String, Object> result = JsonUtil.buildResult(
            "Weighted Edit Distance",
            "Financial Data Error Handling with Context-Sensitive Costs",
            "O(m × n)", "O(m × n)"
        );

        result.put("errorData", errorData);
        result.put("correctData", correctData);

        // Define cost weights
        double digitSubCost = 3.0;   // high cost for digit substitution
        double letterSubCost = 1.0;  // normal cost for letter substitution
        double insertCost = 1.5;
        double deleteCost = 1.5;

        Map<String, Object> weights = new LinkedHashMap<>();
        weights.put("digitSubstitutionCost", digitSubCost);
        weights.put("letterSubstitutionCost", letterSubCost);
        weights.put("insertionCost", insertCost);
        weights.put("deletionCost", deleteCost);
        result.put("costWeights", weights);

        // Compute weighted distance
        double weightedDist = weightedEditDistance(errorData, correctData,
            digitSubCost, letterSubCost, insertCost, deleteCost);

        // Also compute standard Levenshtein for comparison
        int standardDist = DescriptionCorrector.levenshteinDistance(errorData, correctData);

        result.put("weightedEditDistance", weightedDist);
        result.put("standardEditDistance", standardDist);
        result.put("costDifference", weightedDist - standardDist);

        // Analyze error types
        int digitErrors = 0, letterErrors = 0, otherErrors = 0;
        int minLen = Math.min(errorData.length(), correctData.length());
        for (int i = 0; i < minLen; i++) {
            if (errorData.charAt(i) != correctData.charAt(i)) {
                if (Character.isDigit(errorData.charAt(i)) || Character.isDigit(correctData.charAt(i)))
                    digitErrors++;
                else if (Character.isLetter(errorData.charAt(i)))
                    letterErrors++;
                else
                    otherErrors++;
            }
        }

        Map<String, Object> errorAnalysis = new LinkedHashMap<>();
        errorAnalysis.put("digitErrors", digitErrors);
        errorAnalysis.put("letterErrors", letterErrors);
        errorAnalysis.put("otherErrors", otherErrors);
        errorAnalysis.put("lengthDifference", Math.abs(errorData.length() - correctData.length()));
        errorAnalysis.put("severity", weightedDist > 10 ? "CRITICAL" : weightedDist > 5 ? "HIGH" : weightedDist > 2 ? "MEDIUM" : "LOW");
        result.put("errorAnalysis", errorAnalysis);

        // Show DP matrix sample (first 10x10)
        double[][] matrix = getWeightedMatrix(errorData, correctData,
            digitSubCost, letterSubCost, insertCost, deleteCost);
        int rows = Math.min(matrix.length, 11);
        int cols = Math.min(matrix[0].length, 11);
        List<List<Double>> matrixSample = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            List<Double> row = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                row.add(Math.round(matrix[i][j] * 100.0) / 100.0);
            }
            matrixSample.add(row);
        }
        result.put("dpMatrixSample", matrixSample);

        return result;
    }
}
