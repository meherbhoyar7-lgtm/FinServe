package com.finserve.module4_flow;

import com.finserve.util.JsonUtil;
import java.util.*;

/**
 * CriticalChannelIdentifier — Uses Min-Cut Algorithm
 * 
 * Financial Use Case: Identifying critical transaction channels whose
 * removal would most disrupt the banking network. Uses max-flow min-cut
 * theorem: max flow = min cut capacity.
 * 
 * Algorithm: After computing max flow (using Edmonds-Karp/BFS), find
 * reachable vertices from source in residual graph. Edges crossing
 * from reachable to non-reachable set form the min cut.
 * 
 * Time Complexity:  O(V × E²) (Edmonds-Karp for max flow)
 * Space Complexity: O(V²)
 */
public class CriticalChannelIdentifier {

    private int[][] capacity;
    private int[][] flow;
    private int vertices;

    public CriticalChannelIdentifier(int vertices) {
        this.vertices = vertices;
        this.capacity = new int[vertices][vertices];
        this.flow = new int[vertices][vertices];
    }

    public void addEdge(int from, int to, int cap) {
        capacity[from][to] += cap;
    }

    /**
     * BFS for Edmonds-Karp.
     */
    private int bfs(int source, int sink, int[] parent) {
        Arrays.fill(parent, -1);
        parent[source] = source;
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{source, Integer.MAX_VALUE});

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int u = curr[0], pushed = curr[1];

            for (int v = 0; v < vertices; v++) {
                if (parent[v] == -1 && capacity[u][v] - flow[u][v] > 0) {
                    parent[v] = u;
                    int newFlow = Math.min(pushed, capacity[u][v] - flow[u][v]);
                    if (v == sink) return newFlow;
                    queue.add(new int[]{v, newFlow});
                }
            }
        }
        return 0;
    }

    /**
     * Compute max flow using Edmonds-Karp.
     */
    public int maxFlow(int source, int sink) {
        int total = 0;
        int[] parent = new int[vertices];
        int pf;
        while ((pf = bfs(source, sink, parent)) > 0) {
            total += pf;
            int v = sink;
            while (v != source) {
                int u = parent[v];
                flow[u][v] += pf;
                flow[v][u] -= pf;
                v = u;
            }
        }
        return total;
    }

    /**
     * Find min-cut edges after computing max flow.
     * BFS from source in residual graph to find reachable set.
     */
    public List<int[]> findMinCut(int source) {
        boolean[] reachable = new boolean[vertices];
        Queue<Integer> queue = new LinkedList<>();
        queue.add(source);
        reachable[source] = true;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v = 0; v < vertices; v++) {
                if (!reachable[v] && capacity[u][v] - flow[u][v] > 0) {
                    reachable[v] = true;
                    queue.add(v);
                }
            }
        }

        // Min-cut edges: from reachable to non-reachable with original capacity > 0
        List<int[]> cutEdges = new ArrayList<>();
        for (int u = 0; u < vertices; u++) {
            for (int v = 0; v < vertices; v++) {
                if (reachable[u] && !reachable[v] && capacity[u][v] > 0) {
                    cutEdges.add(new int[]{u, v, capacity[u][v]});
                }
            }
        }
        return cutEdges;
    }

    /**
     * Financial use case: Identify critical transaction channels in the banking network.
     */
    public static Map<String, Object> identifyCriticalChannels() {
        Map<String, Object> result = JsonUtil.buildResult(
            "Min-Cut (Max-Flow Min-Cut Theorem)",
            "Critical Transaction Channel Identification",
            "O(V × E²)", "O(V²)"
        );

        String[] nodes = {
            "Central Bank",              // 0 (source)
            "National Clearing",         // 1
            "RTGS Gateway",              // 2
            "NEFT Gateway",              // 3
            "UPI Gateway",               // 4
            "SWIFT Gateway",             // 5
            "Banking Network"            // 6 (sink)
        };

        int n = nodes.length;
        CriticalChannelIdentifier identifier = new CriticalChannelIdentifier(n);

        int[][] edges = {
            {0, 1, 1000}, {0, 2, 700},
            {1, 3, 500}, {1, 4, 600},
            {2, 4, 300}, {2, 5, 400},
            {3, 6, 450}, {4, 6, 700}, {5, 6, 350}
        };

        List<Map<String, Object>> edgeList = new ArrayList<>();
        for (int[] e : edges) {
            identifier.addEdge(e[0], e[1], e[2]);
            Map<String, Object> edge = new LinkedHashMap<>();
            edge.put("from", nodes[e[0]]);
            edge.put("to", nodes[e[1]]);
            edge.put("capacity", e[2]);
            edgeList.add(edge);
        }

        result.put("nodes", nodes);
        result.put("edges", edgeList);

        // Compute max flow
        int maxFlowVal = identifier.maxFlow(0, n - 1);
        result.put("maxFlow", maxFlowVal);

        // Find min cut
        List<int[]> cutEdges = identifier.findMinCut(0);
        int cutCapacity = 0;

        List<Map<String, Object>> criticalChannels = new ArrayList<>();
        for (int[] ce : cutEdges) {
            Map<String, Object> channel = new LinkedHashMap<>();
            channel.put("from", nodes[ce[0]]);
            channel.put("to", nodes[ce[1]]);
            channel.put("capacity", ce[2]);
            channel.put("impact", "Removing this channel reduces network capacity by " + ce[2] + " crores/day");
            criticalChannels.add(channel);
            cutCapacity += ce[2];
        }

        result.put("criticalChannels", criticalChannels);
        result.put("minCutCapacity", cutCapacity);
        result.put("maxFlowEqualsMinCut", maxFlowVal == cutCapacity);
        result.put("verificationNote", "Max-Flow Min-Cut Theorem verified: " +
            maxFlowVal + " = " + cutCapacity);

        // Risk assessment
        Map<String, Object> risk = new LinkedHashMap<>();
        risk.put("singlePointOfFailure", cutEdges.size() == 1);
        risk.put("criticalChannelCount", cutEdges.size());
        risk.put("recommendation", cutEdges.size() <= 2 ?
            "HIGH RISK: Few critical channels. Consider adding redundant paths." :
            "MODERATE: Multiple critical channels provide some redundancy.");
        result.put("riskAssessment", risk);

        return result;
    }
}
