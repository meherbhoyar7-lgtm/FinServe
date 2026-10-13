package com.finserve.module5_np;

import com.finserve.util.JsonUtil;
import java.util.*;

/**
 * RuleConstraintValidator — Uses SAT / 3-SAT Solver
 * 
 * Financial Use Case: Validating combinations of financial rules and
 * constraints. Given a set of regulatory rules (expressed as boolean
 * clauses), determine if a valid configuration exists.
 * 
 * Algorithm: Brute-force SAT solver that checks all 2^n assignments.
 * Also converts to 3-SAT form for demonstration.
 * 
 * Time Complexity:  O(2^n × m) where n=variables, m=clauses
 * Space Complexity: O(n + m)
 */
public class RuleConstraintValidator {

    /**
     * A clause is a disjunction of literals.
     * Each literal is represented as an int: positive = variable, negative = negation.
     */
    public static class Clause {
        int[] literals;
        String description;

        public Clause(int[] literals, String description) {
            this.literals = literals;
            this.description = description;
        }
    }

    /**
     * Check if a given assignment satisfies all clauses.
     */
    public static boolean isSatisfied(List<Clause> clauses, boolean[] assignment) {
        for (Clause clause : clauses) {
            boolean clauseSatisfied = false;
            for (int lit : clause.literals) {
                int var = Math.abs(lit) - 1;
                boolean val = assignment[var];
                if (lit > 0 && val) { clauseSatisfied = true; break; }
                if (lit < 0 && !val) { clauseSatisfied = true; break; }
            }
            if (!clauseSatisfied) return false;
        }
        return true;
    }

    /**
     * Brute-force SAT solver: try all 2^n assignments.
     */
    public static boolean[] solve(List<Clause> clauses, int numVars) {
        int total = 1 << numVars;
        for (int mask = 0; mask < total; mask++) {
            boolean[] assignment = new boolean[numVars];
            for (int i = 0; i < numVars; i++) {
                assignment[i] = ((mask >> i) & 1) == 1;
            }
            if (isSatisfied(clauses, assignment)) {
                return assignment;
            }
        }
        return null; // UNSATISFIABLE
    }

    /**
     * Count all satisfying assignments.
     */
    public static int countSolutions(List<Clause> clauses, int numVars) {
        int count = 0;
        int total = 1 << numVars;
        for (int mask = 0; mask < total; mask++) {
            boolean[] assignment = new boolean[numVars];
            for (int i = 0; i < numVars; i++) {
                assignment[i] = ((mask >> i) & 1) == 1;
            }
            if (isSatisfied(clauses, assignment)) count++;
        }
        return count;
    }

    /**
     * Financial use case: Validate financial rule constraints.
     */
    public static Map<String, Object> validateRules() {
        Map<String, Object> result = JsonUtil.buildResult(
            "SAT / 3-SAT Solver (Brute Force)",
            "Financial Rule Constraint Validation",
            "O(2^n × m)", "O(n + m)"
        );

        // Financial rule variables
        String[] variables = {
            "x1: High-Risk Account Flag",
            "x2: KYC Verified",
            "x3: Transaction > 10 Lakh",
            "x4: International Transfer",
            "x5: New Account (< 6 months)",
            "x6: Multiple Beneficiaries"
        };

        int numVars = variables.length;
        result.put("variables", variables);
        result.put("totalVariables", numVars);
        result.put("totalAssignments", 1 << numVars);

        // Financial rules as clauses (3-SAT form)
        List<Clause> clauses = new ArrayList<>();

        // Rule 1: If high-risk, must be KYC verified: (!x1 OR x2)
        clauses.add(new Clause(new int[]{-1, 2}, "If high-risk, must be KYC verified"));

        // Rule 2: Large transactions need KYC or cannot be international: (!x3 OR x2 OR !x4)
        clauses.add(new Clause(new int[]{-3, 2, -4}, "Large transactions need KYC or must be domestic"));

        // Rule 3: New accounts with multiple beneficiaries need review: (!x5 OR !x6 OR x2)
        clauses.add(new Clause(new int[]{-5, -6, 2}, "New accounts with multiple beneficiaries need KYC"));

        // Rule 4: International transfers need KYC: (!x4 OR x2)
        clauses.add(new Clause(new int[]{-4, 2}, "International transfers require KYC"));

        // Rule 5: At least one positive indicator: (x2 OR !x1 OR !x3)
        clauses.add(new Clause(new int[]{2, -1, -3}, "Must have KYC or not be high-risk/high-value"));

        // Rule 6: High-risk new accounts cannot do international: (!x1 OR !x5 OR !x4)
        clauses.add(new Clause(new int[]{-1, -5, -4}, "High-risk new accounts cannot do international transfers"));

        result.put("totalClauses", clauses.size());

        // Display rules
        List<Map<String, Object>> ruleList = new ArrayList<>();
        for (int i = 0; i < clauses.size(); i++) {
            Map<String, Object> rule = new LinkedHashMap<>();
            rule.put("ruleId", "R" + (i + 1));
            rule.put("description", clauses.get(i).description);
            StringBuilder formula = new StringBuilder("(");
            for (int j = 0; j < clauses.get(i).literals.length; j++) {
                if (j > 0) formula.append(" OR ");
                int lit = clauses.get(i).literals[j];
                formula.append(lit < 0 ? "NOT x" + Math.abs(lit) : "x" + lit);
            }
            formula.append(")");
            rule.put("formula", formula.toString());
            ruleList.add(rule);
        }
        result.put("rules", ruleList);

        // Solve
        long start = System.nanoTime();
        boolean[] solution = solve(clauses, numVars);
        int solutionCount = countSolutions(clauses, numVars);
        long elapsed = System.nanoTime() - start;

        result.put("satisfiable", solution != null);
        result.put("totalSolutions", solutionCount);
        result.put("computeTimeMs", elapsed / 1_000_000.0);

        if (solution != null) {
            Map<String, Object> assignment = new LinkedHashMap<>();
            for (int i = 0; i < numVars; i++) {
                assignment.put(variables[i], solution[i]);
            }
            result.put("satisfyingAssignment", assignment);

            // Interpretation
            List<String> interpretation = new ArrayList<>();
            if (solution[0]) interpretation.add("Account IS flagged as high-risk");
            else interpretation.add("Account is NOT high-risk");
            if (solution[1]) interpretation.add("KYC IS verified ✓");
            else interpretation.add("KYC is NOT verified ✗");
            if (solution[2]) interpretation.add("Transaction exceeds 10 lakh");
            else interpretation.add("Transaction is within 10 lakh");
            if (solution[3]) interpretation.add("International transfer");
            else interpretation.add("Domestic transfer");
            if (solution[4]) interpretation.add("New account (< 6 months)");
            else interpretation.add("Established account");
            if (solution[5]) interpretation.add("Multiple beneficiaries");
            else interpretation.add("Single beneficiary");
            result.put("interpretation", interpretation);
        }

        return result;
    }
}
