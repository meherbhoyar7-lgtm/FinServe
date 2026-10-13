package com.finserve.module5_np;

import com.finserve.util.JsonUtil;
import java.util.*;

/**
 * ConstraintAnalyzer — Demonstrates 3-SAT → CLIQUE Reduction
 * 
 * Financial Use Case: Analyzing constraint relationships by reducing
 * the financial rule-satisfaction problem to a clique problem.
 * If a k-clique exists in the constructed graph, the original
 * 3-SAT formula is satisfiable.
 * 
 * Algorithm: Reduce 3-SAT to CLIQUE by creating a graph where:
 * - Each literal in each clause becomes a node
 * - Edges connect non-contradictory literals from different clauses
 * - k-clique (k = number of clauses) ↔ satisfying assignment
 * 
 * Time Complexity:  O(2^n) for brute-force clique finding
 * Space Complexity: O(V²) for adjacency matrix
 */
public class ConstraintAnalyzer {

    /**
     * Reduce 3-SAT clauses to a graph for CLIQUE analysis.
     * Returns adjacency matrix and node labels.
     */
    public static boolean[][] reduceToGraph(List<RuleConstraintValidator.Clause> clauses) {
        // Each node = (clause_index, literal_index_in_clause)
        int totalNodes = 0;
        for (RuleConstraintValidator.Clause c : clauses) {
            totalNodes += c.literals.length;
        }

        boolean[][] adj = new boolean[totalNodes][totalNodes];

        // Build edges: connect non-contradictory literals from different clauses
        int nodeI = 0;
        for (int i = 0; i < clauses.size(); i++) {
            int nodeJ = 0;
            for (int j = 0; j < clauses.size(); j++) {
                if (i != j) {
                    for (int li = 0; li < clauses.get(i).literals.length; li++) {
                        for (int lj = 0; lj < clauses.get(j).literals.length; lj++) {
                            int litI = clauses.get(i).literals[li];
                            int litJ = clauses.get(j).literals[lj];
                            // Connect if they are NOT contradictory
                            if (litI != -litJ) {
                                adj[nodeI + li][nodeJ + lj] = true;
                            }
                        }
                    }
                }
                nodeJ += clauses.get(j).literals.length;
            }
            nodeI += clauses.get(i).literals.length;
        }
        return adj;
    }

    /**
     * Find a clique of size k using brute force.
     */
    public static List<Integer> findClique(boolean[][] adj, int k) {
        int n = adj.length;
        return findCliqueHelper(adj, new ArrayList<>(), 0, k, n);
    }

    private static List<Integer> findCliqueHelper(boolean[][] adj, List<Integer> current,
                                                    int start, int k, int n) {
        if (current.size() == k) return new ArrayList<>(current);

        for (int v = start; v < n; v++) {
            boolean canAdd = true;
            for (int u : current) {
                if (!adj[u][v]) { canAdd = false; break; }
            }
            if (canAdd) {
                current.add(v);
                List<Integer> result = findCliqueHelper(adj, current, v + 1, k, n);
                if (result != null) return result;
                current.remove(current.size() - 1);
            }
        }
        return null;
    }

    /**
     * Financial use case: Analyze constraint relationships by reducing to CLIQUE.
     */
    public static Map<String, Object> analyzeConstraints() {
        Map<String, Object> result = JsonUtil.buildResult(
            "3-SAT → CLIQUE Reduction",
            "Financial Constraint Analysis via Graph Theory",
            "O(2^n) for clique finding", "O(V²)"
        );

        // Create 3-SAT clauses (financial rules)
        List<RuleConstraintValidator.Clause> clauses = new ArrayList<>();
        clauses.add(new RuleConstraintValidator.Clause(new int[]{1, -2, 3},
            "Allow if KYC OR not high-value OR has guarantor"));
        clauses.add(new RuleConstraintValidator.Clause(new int[]{-1, 2, -3},
            "Restrict if not KYC OR high-value OR no guarantor"));
        clauses.add(new RuleConstraintValidator.Clause(new int[]{1, 2, 3},
            "At least one positive indicator required"));
        clauses.add(new RuleConstraintValidator.Clause(new int[]{-1, -2, 3},
            "With risk factors, guarantor is essential"));

        String[] varNames = {"KYC Verified", "High-Value Transaction", "Has Guarantor"};
        result.put("variables", varNames);
        result.put("totalClauses", clauses.size());

        // Show clauses
        List<Map<String, Object>> clauseList = new ArrayList<>();
        for (int i = 0; i < clauses.size(); i++) {
            Map<String, Object> cl = new LinkedHashMap<>();
            cl.put("clauseId", "C" + (i + 1));
            cl.put("description", clauses.get(i).description);
            StringBuilder formula = new StringBuilder();
            for (int j = 0; j < clauses.get(i).literals.length; j++) {
                if (j > 0) formula.append(" ∨ ");
                int lit = clauses.get(i).literals[j];
                formula.append(lit < 0 ? "¬" + varNames[Math.abs(lit) - 1] : varNames[lit - 1]);
            }
            cl.put("formula", formula.toString());
            clauseList.add(cl);
        }
        result.put("clauses", clauseList);

        // Reduce to graph
        boolean[][] adj = reduceToGraph(clauses);
        int totalNodes = adj.length;

        result.put("graphNodes", totalNodes);

        // Count edges
        int edgeCount = 0;
        for (int i = 0; i < totalNodes; i++)
            for (int j = i + 1; j < totalNodes; j++)
                if (adj[i][j]) edgeCount++;
        result.put("graphEdges", edgeCount);

        // Find k-clique where k = number of clauses
        int k = clauses.size();
        long start = System.nanoTime();
        List<Integer> clique = findClique(adj, k);
        long elapsed = System.nanoTime() - start;

        result.put("requiredCliqueSize", k);
        result.put("cliqueFound", clique != null);
        result.put("computeTimeMs", elapsed / 1_000_000.0);

        if (clique != null) {
            result.put("cliqueNodes", clique);
            // Map clique back to literals
            List<String> cliqueLabels = new ArrayList<>();
            int nodeIdx = 0;
            for (int i = 0; i < clauses.size(); i++) {
                for (int j = 0; j < clauses.get(i).literals.length; j++) {
                    if (clique.contains(nodeIdx)) {
                        int lit = clauses.get(i).literals[j];
                        String label = (lit < 0 ? "NOT " : "") + varNames[Math.abs(lit) - 1];
                        cliqueLabels.add("C" + (i + 1) + ": " + label);
                    }
                    nodeIdx++;
                }
            }
            result.put("cliqueInterpretation", cliqueLabels);
            result.put("conclusionNote", "A " + k + "-clique was found, confirming the constraint system is satisfiable");
        } else {
            result.put("conclusionNote", "No " + k + "-clique exists — the constraints are UNSATISFIABLE");
        }

        // Explain the reduction
        Map<String, Object> reduction = new LinkedHashMap<>();
        reduction.put("step1", "Each literal in each clause becomes a graph node");
        reduction.put("step2", "Edges connect non-contradictory literals from DIFFERENT clauses");
        reduction.put("step3", "A k-clique (k = " + k + " clauses) means we can pick one true literal per clause");
        reduction.put("step4", "Non-contradictory edges ensure the selected literals are consistent");
        result.put("reductionExplanation", reduction);

        return result;
    }
}
