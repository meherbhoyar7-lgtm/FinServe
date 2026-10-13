package com.finserve.module5_np;

import com.finserve.util.JsonUtil;
import java.util.*;

/**
 * DependencyCoverSolver — Uses Vertex-Cover 2-Approximation
 * 
 * Financial Use Case: Finding a near-optimal set of financial nodes to
 * monitor that covers all financial dependencies. Since exact Vertex Cover
 * is NP-hard, the 2-approximation guarantees a solution within 2x of optimal.
 * 
 * Algorithm: Greedy 2-approximation — pick any uncovered edge, add both
 * endpoints to the cover, remove all covered edges. Repeat.
 * 
 * Time Complexity:  O(V + E)
 * Space Complexity: O(V + E)
 */
public class DependencyCoverSolver {

    /**
     * Vertex Cover 2-Approximation algorithm.
     * Picks an arbitrary uncovered edge and adds both endpoints to the cover.
     */
    public static List<Integer> approxVertexCover(boolean[][] adj, int n) {
        List<Integer> cover = new ArrayList<>();
        boolean[] inCover = new boolean[n];
        boolean[][] edgeCovered = new boolean[n][n];

        for (int u = 0; u < n; u++) {
            for (int v = u + 1; v < n; v++) {
                if (adj[u][v] && !edgeCovered[u][v]) {
                    // Add both endpoints
                    if (!inCover[u]) { cover.add(u); inCover[u] = true; }
                    if (!inCover[v]) { cover.add(v); inCover[v] = true; }

                    // Mark all edges involving u or v as covered
                    for (int w = 0; w < n; w++) {
                        if (adj[u][w]) { edgeCovered[u][w] = true; edgeCovered[w][u] = true; }
                        if (adj[v][w]) { edgeCovered[v][w] = true; edgeCovered[w][v] = true; }
                    }
                }
            }
        }
        return cover;
    }

    /**
     * Greedy vertex cover (pick highest degree vertex, remove its edges, repeat).
     * Not guaranteed 2-approx but often better in practice.
     */
    public static List<Integer> greedyVertexCover(boolean[][] adj, int n) {
        List<Integer> cover = new ArrayList<>();
        boolean[] inCover = new boolean[n];
        boolean[][] remaining = new boolean[n][n];
        for (int i = 0; i < n; i++)
            System.arraycopy(adj[i], 0, remaining[i], 0, n);

        while (true) {
            // Find vertex with max remaining degree
            int maxDeg = 0, maxV = -1;
            for (int v = 0; v < n; v++) {
                if (inCover[v]) continue;
                int deg = 0;
                for (int u = 0; u < n; u++)
                    if (remaining[v][u]) deg++;
                if (deg > maxDeg) { maxDeg = deg; maxV = v; }
            }
            if (maxV == -1 || maxDeg == 0) break;

            cover.add(maxV);
            inCover[maxV] = true;
            // Remove all edges involving maxV
            for (int u = 0; u < n; u++) {
                remaining[maxV][u] = false;
                remaining[u][maxV] = false;
            }
        }
        return cover;
    }

    /**
     * Financial use case: Cover all financial dependencies with minimum monitoring.
     */
    public static Map<String, Object> solveDependencies() {
        Map<String, Object> result = JsonUtil.buildResult(
            "Vertex-Cover 2-Approximation",
            "Financial Dependency Coverage Optimization",
            "O(V + E)", "O(V + E)"
        );

        // Financial dependency nodes
        String[] nodes = {
            "Core Banking System",       // 0
            "Payment Gateway",           // 1
            "Fraud Detection Engine",    // 2
            "Risk Assessment Module",    // 3
            "Compliance Checker",        // 4
            "Report Generator",          // 5
            "Audit Trail Logger",        // 6
            "Customer Portal",           // 7
            "Mobile Banking App",        // 8
            "ATM Network Interface"      // 9
        };

        int n = nodes.length;

        // Dependencies between systems
        boolean[][] deps = new boolean[n][n];
        int[][] edges = {
            {0, 1}, {0, 2}, {0, 3}, {0, 7}, {0, 8}, {0, 9},  // Core Banking deps
            {1, 2}, {1, 4},                                     // Payment Gateway
            {2, 3}, {2, 6},                                     // Fraud Detection
            {3, 4}, {3, 5},                                     // Risk Assessment
            {4, 5}, {4, 6},                                     // Compliance
            {5, 6},                                              // Report → Audit
            {7, 8}, {7, 9}                                      // Customer channels
        };

        for (int[] e : edges) {
            deps[e[0]][e[1]] = true;
            deps[e[1]][e[0]] = true;
        }

        result.put("systems", nodes);
        result.put("totalSystems", n);
        result.put("totalDependencies", edges.length);

        // Display dependencies
        List<Map<String, Object>> depList = new ArrayList<>();
        for (int[] e : edges) {
            Map<String, Object> dep = new LinkedHashMap<>();
            dep.put("system1", nodes[e[0]]);
            dep.put("system2", nodes[e[1]]);
            depList.add(dep);
        }
        result.put("dependencies", depList);

        // 2-Approximation
        long start = System.nanoTime();
        List<Integer> approxCover = approxVertexCover(deps, n);
        long approxTime = System.nanoTime() - start;

        List<String> approxNames = new ArrayList<>();
        for (int idx : approxCover) approxNames.add(nodes[idx]);

        Map<String, Object> approxResult = new LinkedHashMap<>();
        approxResult.put("coverSize", approxCover.size());
        approxResult.put("monitoredSystems", approxNames);
        approxResult.put("computeTimeMs", approxTime / 1_000_000.0);
        approxResult.put("guarantee", "At most 2× optimal size");
        result.put("twoApproximation", approxResult);

        // Greedy approach
        start = System.nanoTime();
        List<Integer> greedyCover = greedyVertexCover(deps, n);
        long greedyTime = System.nanoTime() - start;

        List<String> greedyNames = new ArrayList<>();
        for (int idx : greedyCover) greedyNames.add(nodes[idx]);

        Map<String, Object> greedyResult = new LinkedHashMap<>();
        greedyResult.put("coverSize", greedyCover.size());
        greedyResult.put("monitoredSystems", greedyNames);
        greedyResult.put("computeTimeMs", greedyTime / 1_000_000.0);
        greedyResult.put("guarantee", "O(log n) approximation");
        result.put("greedyApproach", greedyResult);

        // Comparison
        Map<String, Object> comparison = new LinkedHashMap<>();
        comparison.put("approxSize", approxCover.size());
        comparison.put("greedySize", greedyCover.size());
        comparison.put("betterApproach", approxCover.size() <= greedyCover.size() ? "2-Approximation" : "Greedy");
        comparison.put("coverageVerified", verifyCover(deps, n, approxCover) && verifyCover(deps, n, greedyCover));
        result.put("comparison", comparison);

        return result;
    }

    private static boolean verifyCover(boolean[][] adj, int n, List<Integer> cover) {
        Set<Integer> coverSet = new HashSet<>(cover);
        for (int u = 0; u < n; u++)
            for (int v = u + 1; v < n; v++)
                if (adj[u][v] && !coverSet.contains(u) && !coverSet.contains(v))
                    return false;
        return true;
    }
}
