package com.finserve.module4_flow;

import com.finserve.util.JsonUtil;
import java.util.*;

/**
 * NetworkCapacityAnalyzer — Uses Edmonds-Karp Algorithm
 * 
 * Financial Use Case: Computing maximum financial network capacities
 * with guaranteed polynomial time using BFS for augmenting paths.
 * 
 * Algorithm: Edmonds-Karp is a specific implementation of Ford-Fulkerson
 * that uses BFS to find shortest augmenting paths, guaranteeing O(VE²) time.
 * 
 * Time Complexity:  O(V × E²)
 * Space Complexity: O(V²)
 */
public class NetworkCapacityAnalyzer {

    private int[][] capacity;
    private int[][] flow;
    private int vertices;
    private int augmentingPaths;

    public NetworkCapacityAnalyzer(int vertices) {
        this.vertices = vertices;
        this.capacity = new int[vertices][vertices];
        this.flow = new int[vertices][vertices];
        this.augmentingPaths = 0;
    }

    public void addEdge(int from, int to, int cap) {
        capacity[from][to] += cap;
    }

    /**
     * BFS to find shortest augmenting path (key difference from Ford-Fulkerson).
     */
    private int bfs(int source, int sink, int[] parent) {
        Arrays.fill(parent, -1);
        parent[source] = source;
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{source, Integer.MAX_VALUE});

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int u = curr[0];
            int pathFlow = curr[1];

            for (int v = 0; v < vertices; v++) {
                if (parent[v] == -1 && capacity[u][v] - flow[u][v] > 0) {
                    parent[v] = u;
                    int newFlow = Math.min(pathFlow, capacity[u][v] - flow[u][v]);
                    if (v == sink) return newFlow;
                    queue.add(new int[]{v, newFlow});
                }
            }
        }
        return 0;
    }

    /**
     * Edmonds-Karp algorithm for max flow.
     */
    public int maxFlow(int source, int sink) {
        int totalFlow = 0;
        int[] parent = new int[vertices];
        augmentingPaths = 0;

        int pathFlow;
        while ((pathFlow = bfs(source, sink, parent)) > 0) {
            augmentingPaths++;
            totalFlow += pathFlow;

            // Update flow along the path
            int v = sink;
            while (v != source) {
                int u = parent[v];
                flow[u][v] += pathFlow;
                flow[v][u] -= pathFlow;
                v = u;
            }
        }
        return totalFlow;
    }

    /**
     * Financial use case: Analyze network capacity for financial transactions.
     */
    public static Map<String, Object> analyzeNetworkCapacity() {
        Map<String, Object> result = JsonUtil.buildResult(
            "Edmonds-Karp (BFS-based Max Flow)",
            "Financial Network Capacity Analysis",
            "O(V × E²)", "O(V²)"
        );

        String[] nodes = {
            "Central Reserve",           // 0 (source)
            "Primary Clearing",          // 1
            "Secondary Clearing",        // 2
            "Regional Hub North",        // 3
            "Regional Hub South",        // 4
            "Regional Hub East",         // 5
            "Settlement Pool"            // 6 (sink)
        };

        int n = nodes.length;
        NetworkCapacityAnalyzer analyzer = new NetworkCapacityAnalyzer(n);

        int[][] edges = {
            {0, 1, 800}, {0, 2, 600},
            {1, 3, 400}, {1, 4, 300}, {1, 5, 200},
            {2, 3, 200}, {2, 4, 350}, {2, 5, 250},
            {3, 6, 500}, {4, 6, 450}, {5, 6, 350}
        };

        List<Map<String, Object>> edgeList = new ArrayList<>();
        for (int[] e : edges) {
            analyzer.addEdge(e[0], e[1], e[2]);
            Map<String, Object> edge = new LinkedHashMap<>();
            edge.put("from", nodes[e[0]]);
            edge.put("to", nodes[e[1]]);
            edge.put("capacity", e[2]);
            edgeList.add(edge);
        }

        result.put("nodes", nodes);
        result.put("edges", edgeList);

        long start = System.nanoTime();
        int maxFlowVal = analyzer.maxFlow(0, n - 1);
        long elapsed = System.nanoTime() - start;

        result.put("maxFlow", maxFlowVal);
        result.put("maxFlowUnit", "crores/day");
        result.put("augmentingPathsFound", analyzer.augmentingPaths);
        result.put("computeTimeMs", elapsed / 1_000_000.0);

        // Flow distribution
        List<Map<String, Object>> flowDist = new ArrayList<>();
        for (int[] e : edges) {
            Map<String, Object> fe = new LinkedHashMap<>();
            fe.put("from", nodes[e[0]]);
            fe.put("to", nodes[e[1]]);
            fe.put("flow", analyzer.flow[e[0]][e[1]]);
            fe.put("capacity", e[2]);
            fe.put("utilization", String.format("%.0f%%",
                (double) analyzer.flow[e[0]][e[1]] / e[2] * 100));
            flowDist.add(fe);
        }
        result.put("flowDistribution", flowDist);

        return result;
    }
}
