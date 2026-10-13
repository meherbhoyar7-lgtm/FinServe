package com.finserve.module3_dp;

import com.finserve.util.JsonUtil;
import java.util.*;

/**
 * OperationCombinator — Uses Bitmask DP
 * 
 * Financial Use Case: Selecting optimal combinations of financial operations
 * (investments, loan packages, insurance bundles) subject to constraints.
 * Each operation has a cost and benefit, and we need to find the best subset.
 * 
 * Algorithm: Iterates over all 2^n subsets using bitmask representation.
 * For each subset, checks constraints and computes total benefit.
 * 
 * Time Complexity:  O(2^n × n)
 * Space Complexity: O(2^n)
 */
public class OperationCombinator {

    /**
     * Find the optimal combination of operations within budget constraint.
     * Each operation has a cost and benefit value.
     * 
     * @param costs   cost of each operation
     * @param benefits benefit of each operation
     * @param budget  maximum total cost allowed
     * @return optimal subset and total benefit
     */
    public static Map<String, Object> findOptimalCombination(
            String[] operationNames, double[] costs, double[] benefits,
            double budget, boolean[][] conflicts) {

        int n = operationNames.length;
        int totalSubsets = 1 << n;

        double bestBenefit = 0;
        int bestMask = 0;
        List<Map<String, Object>> allValidCombinations = new ArrayList<>();
        int validCount = 0;
        int conflictPruned = 0;
        int budgetPruned = 0;

        for (int mask = 1; mask < totalSubsets; mask++) {
            double totalCost = 0;
            double totalBenefit = 0;
            List<String> selected = new ArrayList<>();
            List<Integer> selectedIdx = new ArrayList<>();
            boolean valid = true;

            // Extract selected operations
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    totalCost += costs[i];
                    totalBenefit += benefits[i];
                    selected.add(operationNames[i]);
                    selectedIdx.add(i);
                }
            }

            // Check budget constraint
            if (totalCost > budget) {
                budgetPruned++;
                continue;
            }

            // Check conflict constraints
            if (conflicts != null) {
                for (int i = 0; i < selectedIdx.size() && valid; i++) {
                    for (int j = i + 1; j < selectedIdx.size() && valid; j++) {
                        if (conflicts[selectedIdx.get(i)][selectedIdx.get(j)]) {
                            valid = false;
                            conflictPruned++;
                        }
                    }
                }
            }

            if (!valid) continue;

            validCount++;

            if (totalBenefit > bestBenefit) {
                bestBenefit = totalBenefit;
                bestMask = mask;
            }

            // Store top combinations for display (limit to keep response manageable)
            if (allValidCombinations.size() < 20) {
                Map<String, Object> combo = new LinkedHashMap<>();
                combo.put("operations", selected);
                combo.put("totalCost", Math.round(totalCost * 100.0) / 100.0);
                combo.put("totalBenefit", Math.round(totalBenefit * 100.0) / 100.0);
                combo.put("roi", totalCost > 0 ?
                    String.format("%.1f%%", (totalBenefit / totalCost) * 100) : "N/A");
                combo.put("bitmask", Integer.toBinaryString(mask));
                allValidCombinations.add(combo);
            }
        }

        // Sort by benefit descending
        allValidCombinations.sort((a, b) ->
            Double.compare((double) b.get("totalBenefit"), (double) a.get("totalBenefit")));

        // Build optimal result
        List<String> optimalOps = new ArrayList<>();
        double optimalCost = 0;
        for (int i = 0; i < n; i++) {
            if ((bestMask & (1 << i)) != 0) {
                optimalOps.add(operationNames[i]);
                optimalCost += costs[i];
            }
        }

        Map<String, Object> optimal = new LinkedHashMap<>();
        optimal.put("selectedOperations", optimalOps);
        optimal.put("totalCost", Math.round(optimalCost * 100.0) / 100.0);
        optimal.put("totalBenefit", Math.round(bestBenefit * 100.0) / 100.0);
        optimal.put("bitmask", Integer.toBinaryString(bestMask));
        optimal.put("roi", optimalCost > 0 ?
            String.format("%.1f%%", (bestBenefit / optimalCost) * 100) : "N/A");

        return optimal;
    }

    /**
     * Financial use case: Select the best combination of financial operations
     * within a given budget, considering conflicts between operations.
     */
    public static Map<String, Object> selectOperations(double budget) {
        Map<String, Object> result = JsonUtil.buildResult(
            "Bitmask DP (Subset Enumeration)",
            "Optimal Financial Operation Selection",
            "O(2^n × n)", "O(2^n)"
        );

        // Define available financial operations
        String[] operations = {
            "Fixed Deposit (FD)",
            "Mutual Fund SIP",
            "Stock Portfolio",
            "Government Bonds",
            "Real Estate Fund",
            "Gold ETF",
            "Corporate Bonds",
            "Insurance Bundle"
        };

        double[] costs = {100000, 50000, 200000, 150000, 300000, 75000, 125000, 80000};
        double[] benefits = {8000, 12000, 35000, 11000, 45000, 9000, 15000, 10000};

        // Conflict matrix: some operations conflict with each other
        boolean[][] conflicts = new boolean[operations.length][operations.length];
        conflicts[2][4] = conflicts[4][2] = true; // Stock Portfolio & Real Estate Fund
        conflicts[0][6] = conflicts[6][0] = true; // FD & Corporate Bonds (similar risk)

        result.put("budget", budget);
        result.put("totalOperations", operations.length);
        result.put("totalPossibleSubsets", 1 << operations.length);

        // List available operations
        List<Map<String, Object>> opList = new ArrayList<>();
        for (int i = 0; i < operations.length; i++) {
            Map<String, Object> op = new LinkedHashMap<>();
            op.put("name", operations[i]);
            op.put("cost", costs[i]);
            op.put("expectedBenefit", benefits[i]);
            op.put("roi", String.format("%.1f%%", (benefits[i] / costs[i]) * 100));
            opList.add(op);
        }
        result.put("availableOperations", opList);

        // List conflicts
        List<String> conflictList = new ArrayList<>();
        conflictList.add("Stock Portfolio ↔ Real Estate Fund");
        conflictList.add("Fixed Deposit ↔ Corporate Bonds");
        result.put("conflicts", conflictList);

        // Find optimal combination
        Map<String, Object> optimal = findOptimalCombination(
            operations, costs, benefits, budget, conflicts);

        result.put("optimalCombination", optimal);

        return result;
    }
}
