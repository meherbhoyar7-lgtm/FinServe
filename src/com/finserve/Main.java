package com.finserve;

import com.finserve.server.ApiServer;

/**
 * FinServe - Intelligent Banking Analytics Platform
 * 
 * Main entry point. Starts the API server on port 8080.
 * 
 * Modules implemented:
 *   M1 - String Algorithms (KMP, Z-Function, Rabin-Karp, Aho-Corasick)
 *   M2 - Suffix Structures (Suffix Array, SA-IS, Kasai LCP, Suffix Automaton)
 *   M3 - Advanced DP (Levenshtein, Weighted Edit Distance, Bitmask DP, MCM)
 *   M4 - Network Flow (Ford-Fulkerson, Edmonds-Karp, Dinic's, Bipartite, Min-Cut)
 *   M5 - NP-Completeness (SAT, 3-SAT→CLIQUE, CLIQUE→IS→VC, Vertex Cover Approx)
 *   M6 - Randomised & Parallel (Monte Carlo, Las Vegas, Parallel Prefix Sum, Reduce, Work-Span)
 */
public class Main {
    public static void main(String[] args) {
        try {
            int port = 8080;
            if (args.length > 0) {
                try { port = Integer.parseInt(args[0]); } catch (Exception e) {}
            }

            ApiServer server = new ApiServer(port);
            server.start();

            // Graceful shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nShutting down FinServe server...");
                server.stop();
            }));

        } catch (Exception e) {
            System.err.println("Failed to start FinServe server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
