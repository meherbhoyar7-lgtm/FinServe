package com.finserve.module5_np;

import com.finserve.util.JsonUtil;
import java.util.*;

/**
 * ConflictAnalyzer — Demonstrates CLIQUE → INDEPENDENT-SET → VERTEX-COVER
 * 
 * Financial Use Case: Finding conflicts in financial accounts/entities
 * using graph complement properties. Finding a CLIQUE of conflicting
 * entities is equivalent to finding an INDEPENDENT-SET in the complement.
 * 
 * Reductions:
 * - CLIQUE in G ↔ INDEPENDENT-SET in complement(G)
 * - INDEPENDENT-SET ↔ VERTEX-COVER (complement of vertex cover = max independent set)
 * 
 * Time Complexity:  O(2^n) for brute-force
 * Space Complexity: O(V²)
 */
public class ConflictAnalyzer {

    /**
     * Find maximum clique in a graph (brute force).
     */
    public static List<Integer> findMaxClique(boolean[][] adj, int n) {
        List<Integer> best = new ArrayList<>();
        findMaxCliqueHelper(adj, n, new ArrayList<>(), 0, best);
        return best;
    }

    private static void findMaxCliqueHelper(boolean[][] adj, int n,
                                             List<Integer> current, int start,
                                             List<Integer> best) {
        if (current.size() > best.size()) {
            best.clear();
            best.addAll(current);
        }
        for (int v = start; v < n; v++) {
            boolean canAdd = true;
            for (int u : current) {
                if (!adj[u][v]) { canAdd = false; break; }
            }
            if (canAdd) {
                current.add(v);
                findMaxCliqueHelper(adj, n, current, v + 1, best);
                current.remove(current.size() - 1);
            }
        }
    }

    /**
     * Find maximum independent set (complement of clique in complement graph).
     */
    public static List<Integer> findMaxIndependentSet(boolean[][] adj, int n) {
        // Independent set in G = Clique in complement(G)
        boolean[][] complement = new boolean[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (i != j) complement[i][j] = !adj[i][j];

        return findMaxClique(complement, n);
    }

    /**
     * Find minimum vertex cover.
     * Vertex Cover = V - Maximum Independent Set (König's theorem for general graphs).
     */
    public static List<Integer> findMinVertexCover(boolean[][] adj, int n) {
        List<Integer> maxIS = findMaxIndependentSet(adj, n);
        Set<Integer> isSet = new HashSet<>(maxIS);
        List<Integer> cover = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (!isSet.contains(i)) cover.add(i);
        }
        return cover;
    }

    /**
     * Financial use case: Analyze conflicts between financial entities.
     */
    public static Map<String, Object> analyzeConflicts() {
        Map<String, Object> result = JsonUtil.buildResult(
            "CLIQUE → INDEPENDENT-SET → VERTEX-COVER Reductions",
            "Financial Entity Conflict Analysis",
            "O(2^n)", "O(V²)"
        );

        // Financial entities (accounts suspected of coordinated activity)
        String[] entities = {
            "ACC-2001 (Shell Corp Alpha)",
            "ACC-2002 (Shell Corp Beta)",
            "ACC-2003 (Offshore Trust)",
            "ACC-2004 (Nominee Account)",
            "ACC-2005 (Trading Firm X)",
            "ACC-2006 (Holdings LLC)",
            "ACC-2007 (Personal - Suspect A)"
        };

        int n = entities.length;

        // Conflict graph: edge = confirmed suspicious link between accounts
        boolean[][] conflicts = new boolean[n][n];
        int[][] edges = {
            {0, 1}, {0, 2}, {0, 6},  // Shell Alpha linked to Beta, Offshore, Suspect A
            {1, 2}, {1, 3},           // Shell Beta linked to Offshore, Nominee
            {2, 3}, {2, 6},           // Offshore linked to Nominee, Suspect A
            {3, 4},                    // Nominee linked to Trading Firm
            {4, 5},                    // Trading linked to Holdings
            {5, 6}                     // Holdings linked to Suspect A
        };

        for (int[] e : edges) {
            conflicts[e[0]][e[1]] = true;
            conflicts[e[1]][e[0]] = true;
        }

        result.put("entities", entities);
        result.put("totalEntities", n);

        List<Map<String, Object>> edgeList = new ArrayList<>();
        for (int[] e : edges) {
            Map<String, Object> edge = new LinkedHashMap<>();
            edge.put("entity1", entities[e[0]]);
            edge.put("entity2", entities[e[1]]);
            edge.put("linkType", "Suspicious transaction pattern");
            edgeList.add(edge);
        }
        result.put("suspiciousLinks", edgeList);

        // 1. Find Maximum Clique (largest group of mutually connected accounts)
        List<Integer> maxClique = findMaxClique(conflicts, n);
        List<String> cliqueEntities = new ArrayList<>();
        for (int idx : maxClique) cliqueEntities.add(entities[idx]);

        Map<String, Object> cliqueResult = new LinkedHashMap<>();
        cliqueResult.put("maxCliqueSize", maxClique.size());
        cliqueResult.put("entities", cliqueEntities);
        cliqueResult.put("meaning", "Largest group of accounts ALL linked to each other — likely coordinated");
        result.put("maxClique", cliqueResult);

        // 2. Find Maximum Independent Set (accounts NOT linked to each other)
        List<Integer> maxIS = findMaxIndependentSet(conflicts, n);
        List<String> isEntities = new ArrayList<>();
        for (int idx : maxIS) isEntities.add(entities[idx]);

        Map<String, Object> isResult = new LinkedHashMap<>();
        isResult.put("maxIndependentSetSize", maxIS.size());
        isResult.put("entities", isEntities);
        isResult.put("meaning", "Largest group of accounts with NO suspicious links between them — likely independent");
        result.put("maxIndependentSet", isResult);

        // 3. Find Minimum Vertex Cover (minimum set of accounts to monitor)
        List<Integer> minVC = findMinVertexCover(conflicts, n);
        List<String> vcEntities = new ArrayList<>();
        for (int idx : minVC) vcEntities.add(entities[idx]);

        Map<String, Object> vcResult = new LinkedHashMap<>();
        vcResult.put("minVertexCoverSize", minVC.size());
        vcResult.put("entities", vcEntities);
        vcResult.put("meaning", "Minimum accounts to monitor to cover ALL suspicious links");
        result.put("minVertexCover", vcResult);

        // Verify relationship
        Map<String, Object> verification = new LinkedHashMap<>();
        verification.put("relationship", "|V| = |Max Clique (complement)| + |Min Vertex Cover|");
        verification.put("totalVertices", n);
        verification.put("maxISSize", maxIS.size());
        verification.put("minVCSize", minVC.size());
        verification.put("sum", maxIS.size() + minVC.size());
        verification.put("verified", maxIS.size() + minVC.size() == n);
        result.put("theoremVerification", verification);

        return result;
    }
}
