package com.finserve.module4_flow;

import com.finserve.util.JsonUtil;
import java.util.*;

/**
 * TransactionFlowAnalyzer — Uses Ford-Fulkerson Method
 * 
 * Financial Use Case: Analyzing maximum transaction flow capacity between
 * banking nodes (branches, clearing houses, correspondent banks).
 * Determines the maximum volume of transactions that can flow through
 * the network from a source to a sink.
 * 
 * Algorithm: Ford-Fulkerson uses DFS to find augmenting paths from source
 * to sink, incrementally increasing the flow until no more paths exist.
 * 
 * Time Complexity:  O(E × maxFlow) with DFS
 * Space Complexity: O(V²) for adjacency matrix
 */
public class TransactionFlowAnalyzer {

    private int[][] capacity;
    private int[][] flow;
    private int vertices;

    public TransactionFlowAnalyzer(int vertices) {
        this.vertices = vertices;
        this.capacity = new int[vertices][vertices];
        this.flow = new int[vertices][vertices];
    }

    public void addEdge(int from, int to, int cap) {
        capacity[from][to] += cap;
    }

    /**
     * DFS to find an augmenting path from source to sink.
     */
    private boolean dfs(int source, int sink, boolean[] visited, int[] parent) {
        visited[source] = true;
        if (source == sink) return true;

        for (int v = 0; v < vertices; v++) {
            if (!visited[v] && capacity[source][v] - flow[source][v] > 0) {
                parent[v] = source;
                if (dfs(v, sink, visited, parent)) return true;
            }
        }
        return false;
    }

    /**
     * Ford-Fulkerson algorithm to find maximum flow.
     */
    public int maxFlow(int source, int sink) {
        int totalFlow = 0;
        int[] parent = new int[vertices];

        while (true) {
            boolean[] visited = new boolean[vertices];
            Arrays.fill(parent, -1);

            if (!dfs(source, sink, visited, parent)) break;

            // Find minimum residual capacity along the path
            int pathFlow = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, capacity[u][v] - flow[u][v]);
            }

            // Update flow along the path
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                flow[u][v] += pathFlow;
                flow[v][u] -= pathFlow;
            }

            totalFlow += pathFlow;
        }
        return totalFlow;
    }

    /**
     * Financial use case: Analyze transaction flow capacity in a banking network.
     */
    public static Map<String, Object> analyzeTransactionFlow() {
        Map<String, Object> result = JsonUtil.buildResult(
            "Ford-Fulkerson (DFS-based Max Flow)",
            "Transaction Flow Analysis in Banking Network",
            "O(E × maxFlow)", "O(V²)"
        );

        // Define banking network nodes
        String[] nodes = {
            "Source Bank (HQ)",          // 0
            "Mumbai Branch",              // 1
            "Delhi Branch",               // 2
            "Bangalore Branch",           // 3
            "Chennai Branch",             // 4
            "Clearing House",             // 5
            "Correspondent Bank",         // 6
            "Destination Bank"            // 7 (sink)
        };

        int n = nodes.length;
        TransactionFlowAnalyzer analyzer = new TransactionFlowAnalyzer(n);

        // Define edges (transaction capacities in lakhs per day)
        int[][] edges = {
            {0, 1, 500}, {0, 2, 400}, {0, 3, 300},  // HQ to branches
            {1, 4, 200}, {1, 5, 300},                 // Mumbai
            {2, 4, 150}, {2, 5, 250},                 // Delhi
            {3, 5, 200}, {3, 6, 150},                 // Bangalore
            {4, 7, 300},                               // Chennai to dest
            {5, 6, 350}, {5, 7, 200},                 // Clearing House
            {6, 7, 400}                                // Correspondent Bank
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
        result.put("totalNodes", n);
        result.put("totalEdges", edges.length);

        // Compute max flow
        long start = System.nanoTime();
        int maxFlow = analyzer.maxFlow(0, n - 1);
        long elapsed = System.nanoTime() - start;

        result.put("maxFlow", maxFlow);
        result.put("maxFlowUnit", "lakhs/day");
        result.put("computeTimeMs", elapsed / 1_000_000.0);

        // Show flow on each edge
        List<Map<String, Object>> flowEdges = new ArrayList<>();
        for (int[] e : edges) {
            Map<String, Object> fe = new LinkedHashMap<>();
            fe.put("from", nodes[e[0]]);
            fe.put("to", nodes[e[1]]);
            fe.put("flow", analyzer.flow[e[0]][e[1]]);
            fe.put("capacity", e[2]);
            fe.put("utilization", String.format("%.0f%%",
                (double) analyzer.flow[e[0]][e[1]] / e[2] * 100));
            flowEdges.add(fe);
        }
        result.put("flowDistribution", flowEdges);

        // Identify bottlenecks (edges at full capacity)
        List<String> bottlenecks = new ArrayList<>();
        for (int[] e : edges) {
            if (analyzer.flow[e[0]][e[1]] == e[2]) {
                bottlenecks.add(nodes[e[0]] + " → " + nodes[e[1]] +
                    " (capacity: " + e[2] + " lakhs)");
            }
        }
        result.put("bottlenecks", bottlenecks);

        return result;
    }
}
