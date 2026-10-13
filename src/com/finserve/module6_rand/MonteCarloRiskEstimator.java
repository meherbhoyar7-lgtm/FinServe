package com.finserve.module6_rand;

import com.finserve.util.JsonUtil;
import java.util.*;

/**
 * MonteCarloRiskEstimator — Monte Carlo Randomised Algorithm
 * 
 * Financial Use Case: Estimate portfolio Value-at-Risk (VaR) using
 * Monte Carlo simulation. Randomly samples from historical return
 * distributions to estimate the worst-case loss at a given confidence.
 * 
 * Algorithm: Monte Carlo Method
 * - Random sampling to approximate a probabilistic quantity
 * - Result is approximate, but probability of large error is bounded
 * - More iterations → higher accuracy (law of large numbers)
 * 
 * Time Complexity:  O(S * N) where S = simulations, N = portfolio size
 * Space Complexity: O(S) for storing simulation results
 */
public class MonteCarloRiskEstimator {

    private static final Random rng = new Random(42);

    /**
     * Simulate a single portfolio return using random sampling.
     */
    private static double simulatePortfolioReturn(double[] weights, double[] meanReturns,
                                                    double[][] covMatrix, int n) {
        // Generate correlated random returns using Cholesky-like approach
        double[] z = new double[n];
        for (int i = 0; i < n; i++) {
            z[i] = rng.nextGaussian();
        }

        double portfolioReturn = 0.0;
        for (int i = 0; i < n; i++) {
            double assetReturn = meanReturns[i];
            for (int j = 0; j <= i; j++) {
                assetReturn += Math.sqrt(Math.abs(covMatrix[i][j])) * z[j] * 0.01;
            }
            portfolioReturn += weights[i] * assetReturn;
        }
        return portfolioReturn;
    }

    /**
     * Monte Carlo VaR estimation with configurable simulations.
     */
    public static Map<String, Object> estimateRisk() {
        Map<String, Object> result = JsonUtil.buildResult(
            "Monte Carlo Simulation (Randomised Algorithm)",
            "Portfolio Value-at-Risk (VaR) Estimation",
            "O(S × N) — S simulations, N assets", "O(S)"
        );

        // Financial portfolio setup
        String[] assets = {
            "NIFTY 50 Index Fund",
            "10Y Government Bond",
            "Gold ETF",
            "Corporate Bond Fund",
            "Real Estate Investment Trust"
        };
        int n = assets.length;
        double portfolioValue = 10_000_000.0; // ₹1 Crore

        // Portfolio weights (must sum to 1.0)
        double[] weights = {0.35, 0.25, 0.15, 0.15, 0.10};

        // Annualized mean daily returns (decimal)
        double[] meanReturns = {0.0004, 0.0002, 0.0001, 0.00025, 0.0003};

        // Simplified covariance matrix
        double[][] covMatrix = {
            {0.0004, 0.0001, 0.00005, 0.0001, 0.00015},
            {0.0001, 0.0001, 0.00002, 0.00008, 0.00005},
            {0.00005, 0.00002, 0.0002, 0.00003, 0.00004},
            {0.0001, 0.00008, 0.00003, 0.00012, 0.00006},
            {0.00015, 0.00005, 0.00004, 0.00006, 0.0003}
        };

        result.put("portfolioValue", portfolioValue);
        result.put("currency", "INR");

        List<Map<String, Object>> assetList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Map<String, Object> a = new LinkedHashMap<>();
            a.put("asset", assets[i]);
            a.put("weight", weights[i]);
            a.put("allocation", weights[i] * portfolioValue);
            a.put("dailyMeanReturn", meanReturns[i]);
            assetList.add(a);
        }
        result.put("portfolio", assetList);

        // Run Monte Carlo simulations
        int[] simulationCounts = {1000, 5000, 10000};
        double confidenceLevel = 0.95;

        List<Map<String, Object>> simResults = new ArrayList<>();

        for (int simCount : simulationCounts) {
            long startTime = System.nanoTime();

            double[] returns = new double[simCount];
            for (int s = 0; s < simCount; s++) {
                returns[s] = simulatePortfolioReturn(weights, meanReturns, covMatrix, n);
            }

            // Sort returns to find VaR percentile
            Arrays.sort(returns);
            int varIndex = (int) ((1.0 - confidenceLevel) * simCount);
            double varReturn = returns[varIndex];
            double varAmount = -varReturn * portfolioValue;

            // Calculate Expected Shortfall (CVaR)
            double sumTail = 0.0;
            for (int i = 0; i <= varIndex; i++) {
                sumTail += returns[i];
            }
            double cvarReturn = sumTail / (varIndex + 1);
            double cvarAmount = -cvarReturn * portfolioValue;

            // Statistics
            double mean = 0, stddev = 0;
            for (double r : returns) mean += r;
            mean /= simCount;
            for (double r : returns) stddev += (r - mean) * (r - mean);
            stddev = Math.sqrt(stddev / simCount);

            long elapsed = System.nanoTime() - startTime;

            Map<String, Object> sim = new LinkedHashMap<>();
            sim.put("simulations", simCount);
            sim.put("confidence", confidenceLevel);
            sim.put("varPercentile", String.format("%.4f%%", varReturn * 100));
            sim.put("varAmountINR", Math.round(varAmount * 100.0) / 100.0);
            sim.put("cvarAmountINR", Math.round(cvarAmount * 100.0) / 100.0);
            sim.put("meanReturn", String.format("%.6f", mean));
            sim.put("stdDeviation", String.format("%.6f", stddev));
            sim.put("worstCase", String.format("%.4f%%", returns[0] * 100));
            sim.put("bestCase", String.format("%.4f%%", returns[simCount - 1] * 100));
            sim.put("executionTimeMs", String.format("%.3f", elapsed / 1_000_000.0));
            sim.put("algorithmType", "Monte Carlo (approximate — error decreases as O(1/√S))");
            simResults.add(sim);
        }

        result.put("monteCarloResults", simResults);

        // Convergence analysis
        Map<String, Object> convergence = new LinkedHashMap<>();
        convergence.put("property", "Law of Large Numbers: VaR estimate converges to true value as S → ∞");
        convergence.put("errorBound", "Monte Carlo error = O(1/√S), doubling accuracy requires 4× simulations");
        convergence.put("guarantee", "Result is APPROXIMATE with probabilistic bound, NOT guaranteed exact");
        convergence.put("classification", "Monte Carlo algorithm — always runs in bounded time, may give incorrect answer with small probability");
        result.put("convergenceAnalysis", convergence);

        return result;
    }
}
