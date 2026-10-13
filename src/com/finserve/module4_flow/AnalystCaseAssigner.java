package com.finserve.module4_flow;

import com.finserve.util.JsonUtil;
import java.util.*;

/**
 * AnalystCaseAssigner — Uses Bipartite Matching (Hungarian / Hopcroft-Karp)
 * 
 * Financial Use Case: Optimally assigning financial analysts to investigation
 * cases based on expertise, availability, and case requirements.
 * 
 * Algorithm: Uses augmenting paths in a bipartite graph to find maximum
 * matching between analysts and cases.
 * 
 * Time Complexity:  O(V × E)
 * Space Complexity: O(V)
 */
public class AnalystCaseAssigner {

    /**
     * Find maximum bipartite matching using augmenting paths.
     * 
     * @param adj adjacency list: adj[analyst] = list of cases they can handle
     * @param numAnalysts number of analysts (left side)
     * @param numCases number of cases (right side)
     * @return matching array: matchCase[case] = analyst assigned, or -1
     */
    public static int[] findMaxMatching(List<List<Integer>> adj, int numAnalysts, int numCases) {
        int[] matchCase = new int[numCases];
        Arrays.fill(matchCase, -1);

        int matchCount = 0;
        for (int analyst = 0; analyst < numAnalysts; analyst++) {
            boolean[] visited = new boolean[numCases];
            if (augment(analyst, adj, matchCase, visited)) {
                matchCount++;
            }
        }
        return matchCase;
    }

    private static boolean augment(int analyst, List<List<Integer>> adj,
                                    int[] matchCase, boolean[] visited) {
        for (int caseId : adj.get(analyst)) {
            if (!visited[caseId]) {
                visited[caseId] = true;
                if (matchCase[caseId] == -1 || augment(matchCase[caseId], adj, matchCase, visited)) {
                    matchCase[caseId] = analyst;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Financial use case: Assign analysts to investigation cases.
     */
    public static Map<String, Object> assignAnalystsToCases() {
        Map<String, Object> result = JsonUtil.buildResult(
            "Bipartite Matching (Augmenting Paths)",
            "Financial Analyst to Case Assignment",
            "O(V × E)", "O(V)"
        );

        String[] analysts = {
            "Analyst Priya (Fraud Expert)",
            "Analyst Rahul (AML Specialist)",
            "Analyst Sneha (Compliance)",
            "Analyst Vikram (Risk Analysis)",
            "Analyst Ananya (Forensics)",
            "Analyst Arjun (Digital Fraud)"
        };

        String[] cases = {
            "Case #101: Wire Fraud Investigation",
            "Case #102: AML Compliance Review",
            "Case #103: Identity Theft Ring",
            "Case #104: Insider Trading Alert",
            "Case #105: Cyber Fraud Scheme",
            "Case #106: Tax Evasion Network",
            "Case #107: Ponzi Scheme Detection"
        };

        // Analyst capabilities (which cases each analyst can handle)
        int[][] capabilities = {
            {0, 2, 3},       // Priya: Wire Fraud, Identity Theft, Insider Trading
            {1, 5},           // Rahul: AML, Tax Evasion
            {1, 3, 5},       // Sneha: AML, Insider Trading, Tax Evasion
            {0, 3, 4, 6},    // Vikram: Wire Fraud, Insider Trading, Cyber Fraud, Ponzi
            {2, 4, 6},       // Ananya: Identity Theft, Cyber Fraud, Ponzi
            {4, 5, 6}        // Arjun: Cyber Fraud, Tax Evasion, Ponzi
        };

        List<List<Integer>> adj = new ArrayList<>();
        for (int[] caps : capabilities) {
            List<Integer> list = new ArrayList<>();
            for (int c : caps) list.add(c);
            adj.add(list);
        }

        result.put("analysts", analysts);
        result.put("cases", cases);
        result.put("totalAnalysts", analysts.length);
        result.put("totalCases", cases.length);

        // Show capabilities
        List<Map<String, Object>> capList = new ArrayList<>();
        for (int i = 0; i < analysts.length; i++) {
            Map<String, Object> cap = new LinkedHashMap<>();
            cap.put("analyst", analysts[i]);
            List<String> canHandle = new ArrayList<>();
            for (int c : capabilities[i]) canHandle.add(cases[c]);
            cap.put("canHandle", canHandle);
            capList.add(cap);
        }
        result.put("analystCapabilities", capList);

        // Find maximum matching
        long start = System.nanoTime();
        int[] matching = findMaxMatching(adj, analysts.length, cases.length);
        long elapsed = System.nanoTime() - start;

        // Build assignment result
        List<Map<String, Object>> assignments = new ArrayList<>();
        int matched = 0;
        List<String> unassignedCases = new ArrayList<>();

        for (int c = 0; c < cases.length; c++) {
            if (matching[c] != -1) {
                matched++;
                Map<String, Object> assignment = new LinkedHashMap<>();
                assignment.put("case", cases[c]);
                assignment.put("assignedTo", analysts[matching[c]]);
                assignments.add(assignment);
            } else {
                unassignedCases.add(cases[c]);
            }
        }

        result.put("assignments", assignments);
        result.put("totalAssigned", matched);
        result.put("unassignedCases", unassignedCases);
        result.put("matchingEfficiency",
            String.format("%.0f%%", (double) matched / cases.length * 100));
        result.put("computeTimeMs", elapsed / 1_000_000.0);

        return result;
    }
}
