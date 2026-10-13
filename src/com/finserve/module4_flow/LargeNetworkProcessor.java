package com.finserve.module4_flow;

import com.finserve.util.JsonUtil;
import java.util.*;

/**
 * LargeNetworkProcessor — Uses Dinic's Algorithm
 * 
 * Financial Use Case: Processing maximum flow in larger transaction networks
 * where Edmonds-Karp may be too slow. Dinic's uses level graphs and blocking
 * flows for better performance.
 * 
 * Algorithm: Builds level graph using BFS, then finds blocking flows
 * using DFS. Repeats until no more augmenting paths exist.
 * 
 * Time Complexity:  O(V² × E)
 * Space Complexity: O(V + E)
 */
public class LargeNetworkProcessor {

    private int[] head, next, to, cap;
    private int[] level, iter;
    private int edgeCnt;
    private int vertices;

    public LargeNetworkProcessor(int vertices, int maxEdges) {
        this.vertices = vertices;
        head = new int[vertices];
        Arrays.fill(head, -1);
        next = new int[maxEdges * 2];
        to = new int[maxEdges * 2];
        cap = new int[maxEdges * 2];
        level = new int[vertices];
        iter = new int[vertices];
        edgeCnt = 0;
    }

    public void addEdge(int from, int toNode, int capacity) {
        to[edgeCnt] = toNode; cap[edgeCnt] = capacity; next[edgeCnt] = head[from]; head[from] = edgeCnt++;
        to[edgeCnt] = from; cap[edgeCnt] = 0; next[edgeCnt] = head[toNode]; head[toNode] = edgeCnt++;
    }

    /**
     * BFS to build level graph.
     */
    private boolean bfs(int source, int sink) {
        Arrays.fill(level, -1);
        Queue<Integer> queue = new LinkedList<>();
        level[source] = 0;
        queue.add(source);

        while (!queue.isEmpty()) {
            int v = queue.poll();
            for (int e = head[v]; e != -1; e = next[e]) {
                if (cap[e] > 0 && level[to[e]] < 0) {
                    level[to[e]] = level[v] + 1;
                    queue.add(to[e]);
                }
            }
        }
        return level[sink] >= 0;
    }

    /**
     * DFS to find blocking flow in level graph.
     */
    private int dfs(int v, int sink, int pushed) {
        if (v == sink) return pushed;
        for (; iter[v] != -1; iter[v] = next[iter[v]]) {
            int e = iter[v];
            if (cap[e] > 0 && level[to[e]] == level[v] + 1) {
                int d = dfs(to[e], sink, Math.min(pushed, cap[e]));
                if (d > 0) {
                    cap[e] -= d;
                    cap[e ^ 1] += d;
                    return d;
                }
            }
        }
        return 0;
    }

    /**
     * Dinic's algorithm for max flow.
     */
    public int maxFlow(int source, int sink) {
        int totalFlow = 0;
        while (bfs(source, sink)) {
            System.arraycopy(head, 0, iter, 0, vertices);
            int d;
            while ((d = dfs(source, sink, Integer.MAX_VALUE)) > 0) {
                totalFlow += d;
            }
        }
        return totalFlow;
    }

    /**
     * Financial use case: Process max flow in a large banking transaction network.
     */
    public static Map<String, Object> processLargeNetwork() {
        Map<String, Object> result = JsonUtil.buildResult(
            "Dinic's Algorithm (Level Graph + Blocking Flow)",
            "Large-Scale Transaction Network Processing",
            "O(V² × E)", "O(V + E)"
        );

        String[] nodes = {
            "Reserve Bank",              // 0
            "SBI Clearing",              // 1
            "HDFC Clearing",             // 2
            "ICICI Clearing",            // 3
            "Axis Clearing",             // 4
            "Kotak Clearing",            // 5
            "Payment Gateway A",         // 6
            "Payment Gateway B",         // 7
            "UPI Hub",                   // 8
            "NEFT Hub",                  // 9
            "Settlement Node"            // 10
        };

        int n = nodes.length;
        int maxEdges = 30;
        LargeNetworkProcessor processor = new LargeNetworkProcessor(n, maxEdges);

        int[][] edges = {
            {0, 1, 1000}, {0, 2, 800}, {0, 3, 700}, {0, 4, 500}, {0, 5, 400},
            {1, 6, 600}, {1, 8, 400},
            {2, 6, 500}, {2, 7, 300},
            {3, 7, 400}, {3, 8, 300},
            {4, 8, 300}, {4, 9, 200},
            {5, 7, 200}, {5, 9, 200},
            {6, 10, 800}, {7, 10, 700}, {8, 10, 600}, {9, 10, 350}
        };

        List<Map<String, Object>> edgeList = new ArrayList<>();
        for (int[] e : edges) {
            processor.addEdge(e[0], e[1], e[2]);
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

        long start = System.nanoTime();
        int maxFlowVal = processor.maxFlow(0, n - 1);
        long elapsed = System.nanoTime() - start;

        result.put("maxFlow", maxFlowVal);
        result.put("maxFlowUnit", "crores/day");
        result.put("computeTimeMs", elapsed / 1_000_000.0);

        // Compare with Edmonds-Karp timing
        NetworkCapacityAnalyzer ek = new NetworkCapacityAnalyzer(n);
        for (int[] e : edges) ek.addEdge(e[0], e[1], e[2]);
        long ekStart = System.nanoTime();
        ek.maxFlow(0, n - 1);
        long ekElapsed = System.nanoTime() - ekStart;

        result.put("edmondsKarpTimeMs", ekElapsed / 1_000_000.0);
        result.put("dinicSpeedup", ekElapsed > 0 ?
            String.format("%.2fx", (double) ekElapsed / Math.max(1, elapsed)) : "N/A");

        return result;
    }
}
