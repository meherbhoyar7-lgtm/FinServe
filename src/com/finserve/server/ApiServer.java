package com.finserve.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.finserve.model.*;
import com.finserve.data.SampleDataGenerator;
import com.finserve.data.DatasetManager;
import com.finserve.util.JsonUtil;
import com.finserve.module1_string.*;
import com.finserve.module2_suffix.*;
import com.finserve.module3_dp.*;
import com.finserve.module4_flow.*;
import com.finserve.module5_np.*;
import com.finserve.module6_rand.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * REST API server for FinServe Banking Analytics Platform.
 * Uses Java's built-in HttpServer — no external dependencies.
 */
public class ApiServer {

    private HttpServer server;
    private DatasetManager datasetManager;

    public ApiServer(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // Initialize the central dataset manager (loads default dataset)
        datasetManager = DatasetManager.getInstance();

        // Register API endpoints
        registerEndpoints();

        server.setExecutor(null);
    }

    // Convenience accessors that always read from the active dataset
    private List<Transaction> getTransactions() { return datasetManager.getTransactions(); }
    private List<Account> getAccounts() { return datasetManager.getAccounts(); }
    private List<FinancialDocument> getDocuments() { return datasetManager.getDocuments(); }

    private void registerEndpoints() {
        // Health check
        // ===== DATASET MANAGEMENT ENDPOINTS =====

        server.createContext("/api/dataset/presets", exchange -> {
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("presets", DatasetManager.getAvailablePresets());
            resp.put("activeDatasetId", datasetManager.getActiveDatasetId());
            sendJson(exchange, resp);
        });

        server.createContext("/api/dataset/active", exchange -> {
            sendJson(exchange, datasetManager.getSummary());
        });

        server.createContext("/api/dataset/switch", exchange -> {
            Map<String, String> params = parseBody(exchange);
            String presetId = params.getOrDefault("presetId", "default");
            datasetManager.loadPreset(presetId);
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("success", true);
            resp.put("message", "Switched to dataset: " + datasetManager.getActiveDatasetName());
            resp.put("dataset", datasetManager.getSummary());
            sendJson(exchange, resp);
        });

        server.createContext("/api/dataset/upload", exchange -> {
            // Read the raw body
            InputStream bodyStream = exchange.getRequestBody();
            byte[] bodyBytes = bodyStream.readAllBytes();
            String body = new String(bodyBytes, StandardCharsets.UTF_8).trim();

            Map<String, Object> resp = new LinkedHashMap<>();
            try {
                List<Transaction> uploaded;
                String datasetName = "Custom Uploaded Dataset";

                if (body.startsWith("[")) {
                    // JSON array of transactions
                    uploaded = DatasetManager.parseJsonTransactions(body);
                } else if (body.startsWith("{")) {
                    // JSON object with optional name and data fields
                    Map<String, String> wrapper = JsonUtil.parseJsonObject(body);
                    datasetName = wrapper.getOrDefault("name", datasetName);
                    String data = wrapper.getOrDefault("data", "");
                    if (data.startsWith("[")) {
                        uploaded = DatasetManager.parseJsonTransactions(data);
                    } else {
                        uploaded = DatasetManager.parseCSV(data);
                    }
                } else {
                    // Treat as CSV
                    uploaded = DatasetManager.parseCSV(body);
                }

                if (uploaded.isEmpty()) {
                    resp.put("success", false);
                    resp.put("error", "No valid transactions could be parsed from the uploaded data.");
                } else {
                    datasetManager.loadCustomDataset(datasetName, uploaded, null, null);
                    resp.put("success", true);
                    resp.put("message", "Loaded " + uploaded.size() + " transactions from upload.");
                    resp.put("dataset", datasetManager.getSummary());
                }
            } catch (Exception e) {
                resp.put("success", false);
                resp.put("error", "Failed to parse uploaded data: " + e.getMessage());
            }
            sendJson(exchange, resp);
        });

        server.createContext("/api/dataset/transactions", exchange -> {
            Map<String, String> params = parseBody(exchange);
            String search = params.getOrDefault("search", "").toLowerCase().trim();
            String type = params.getOrDefault("type", "").toUpperCase().trim();
            String status = params.getOrDefault("status", "").toUpperCase().trim();
            int page = 1;
            int limit = 25;
            try { page = Math.max(1, Integer.parseInt(params.getOrDefault("page", "1"))); } catch (Exception ignored) {}
            try { limit = Math.max(5, Math.min(500, Integer.parseInt(params.getOrDefault("limit", "25")))); } catch (Exception ignored) {}

            List<Transaction> all = getTransactions();
            List<Transaction> filtered = new ArrayList<>();
            for (Transaction t : all) {
                if (!type.isEmpty() && !type.equals("ALL") && !t.getTransactionType().equalsIgnoreCase(type)) continue;
                if (!status.isEmpty() && !status.equals("ALL") && !t.getStatus().equalsIgnoreCase(status)) continue;
                if (!search.isEmpty()) {
                    boolean matches = (t.getTransactionId() != null && t.getTransactionId().toLowerCase().contains(search)) ||
                                      (t.getSenderAccount() != null && t.getSenderAccount().toLowerCase().contains(search)) ||
                                      (t.getReceiverAccount() != null && t.getReceiverAccount().toLowerCase().contains(search)) ||
                                      (t.getDescription() != null && t.getDescription().toLowerCase().contains(search)) ||
                                      (t.getPaymentCode() != null && t.getPaymentCode().toLowerCase().contains(search));
                    if (!matches) continue;
                }
                filtered.add(t);
            }

            int total = filtered.size();
            int totalPages = Math.max(1, (int) Math.ceil((double) total / limit));
            int from = Math.min((page - 1) * limit, total);
            int to = Math.min(from + limit, total);
            List<Transaction> pageItems = filtered.subList(from, to);

            StringBuilder json = new StringBuilder("{");
            json.append("\"total\":").append(total).append(",");
            json.append("\"page\":").append(page).append(",");
            json.append("\"limit\":").append(limit).append(",");
            json.append("\"totalPages\":").append(totalPages).append(",");
            json.append("\"transactions\":[");
            for (int i = 0; i < pageItems.size(); i++) {
                if (i > 0) json.append(",");
                json.append(pageItems.get(i).toJson());
            }
            json.append("]}");
            sendRawJson(exchange, json.toString());
        });

        server.createContext("/api/dataset/accounts", exchange -> {
            List<Account> accts = getAccounts();
            StringBuilder json = new StringBuilder("{");
            json.append("\"total\":").append(accts.size()).append(",");
            json.append("\"accounts\":[");
            for (int i = 0; i < accts.size(); i++) {
                if (i > 0) json.append(",");
                json.append(accts.get(i).toJson());
            }
            json.append("]}");
            sendRawJson(exchange, json.toString());
        });

        server.createContext("/api/dataset/documents", exchange -> {
            List<FinancialDocument> docs = getDocuments();
            StringBuilder json = new StringBuilder("{");
            json.append("\"total\":").append(docs.size()).append(",");
            json.append("\"documents\":[");
            for (int i = 0; i < docs.size(); i++) {
                if (i > 0) json.append(",");
                json.append(docs.get(i).toJson());
            }
            json.append("]}");
            sendRawJson(exchange, json.toString());
        });

        server.createContext("/api/dataset/export", exchange -> {
            Map<String, String> params = parseBody(exchange);
            String format = params.getOrDefault("format", "csv").toLowerCase();
            List<Transaction> txns = getTransactions();

            if ("json".equals(format)) {
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < txns.size(); i++) {
                    if (i > 0) json.append(",");
                    json.append(txns.get(i).toJson());
                }
                json.append("]");
                sendRawJson(exchange, json.toString());
            } else {
                StringBuilder csv = new StringBuilder("TransactionID,SenderAccount,ReceiverAccount,Amount,Description,PaymentCode,TransactionType,Status,Timestamp,Currency\n");
                for (Transaction t : txns) {
                    csv.append(escapeCsv(t.getTransactionId())).append(",");
                    csv.append(escapeCsv(t.getSenderAccount())).append(",");
                    csv.append(escapeCsv(t.getReceiverAccount())).append(",");
                    csv.append(t.getAmount()).append(",");
                    csv.append(escapeCsv(t.getDescription())).append(",");
                    csv.append(escapeCsv(t.getPaymentCode())).append(",");
                    csv.append(escapeCsv(t.getTransactionType())).append(",");
                    csv.append(escapeCsv(t.getStatus())).append(",");
                    csv.append(t.getTimestamp() != null ? t.getTimestamp().toString() : "").append(",");
                    csv.append(escapeCsv(t.getCurrency())).append("\n");
                }
                byte[] bytes = csv.toString().getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/csv; charset=UTF-8");
                exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=\"finserve_dataset.csv\"");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            }
        });

        // Health check
        server.createContext("/api/health", exchange -> {
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("status", "running");
            resp.put("platform", "FinServe - Intelligent Banking Analytics");
            resp.put("modules", 6);
            resp.put("activeDataset", datasetManager.getActiveDatasetName());
            resp.put("transactions", getTransactions().size());
            resp.put("accounts", getAccounts().size());
            resp.put("documents", getDocuments().size());
            sendJson(exchange, resp);
        });

        // Sample data endpoint
        server.createContext("/api/data/summary", exchange -> {
            sendJson(exchange, datasetManager.getSummary());
        });

        // ===== MODULE 1: String Algorithms =====

        server.createContext("/api/module1/search-code", exchange -> {
            Map<String, String> params = parseBody(exchange);
            String code = params.getOrDefault("query", "PAY-2024");
            Map<String, Object> result = TransactionCodeSearch.searchTransactionCode(getTransactions(), code);
            sendJson(exchange, result);
        });

        server.createContext("/api/module1/analyze-patterns", exchange -> {
            Map<String, String> params = parseBody(exchange);
            String pattern = params.getOrDefault("query", "payment");
            Map<String, Object> result = PatternAnalyzer.analyzePatterns(getTransactions(), pattern);
            sendJson(exchange, result);
        });

        server.createContext("/api/module1/lookup-code", exchange -> {
            Map<String, String> params = parseBody(exchange);
            String code = params.getOrDefault("query", "SWIFT");
            Map<String, Object> result = FinancialCodeLookup.lookupFinancialCode(getTransactions(), code);
            sendJson(exchange, result);
        });

        server.createContext("/api/module1/detect-fraud", exchange -> {
            Map<String, String> params = parseBody(exchange);
            String patternsStr = params.getOrDefault("patterns", "");
            String[] patterns;
            if (patternsStr.isEmpty()) {
                patterns = SampleDataGenerator.getFraudPatterns();
            } else {
                patterns = patternsStr.split(",");
                for (int i = 0; i < patterns.length; i++) patterns[i] = patterns[i].trim();
            }
            Map<String, Object> result = FraudPatternDetector.detectFraudPatterns(getTransactions(), patterns);
            sendJson(exchange, result);
        });

        // ===== MODULE 2: Suffix Structures =====

        server.createContext("/api/module2/index-transactions", exchange -> {
            Map<String, String> params = parseBody(exchange);
            String query = params.getOrDefault("query", "transfer");
            String corpus = SampleDataGenerator.buildTransactionCorpus(getTransactions());
            Map<String, Object> result = TransactionIndexer.indexAndSearch(corpus, query);
            sendJson(exchange, result);
        });

        server.createContext("/api/module2/index-documents", exchange -> {
            Map<String, String> params = parseBody(exchange);
            String query = params.getOrDefault("query", "compliance");
            StringBuilder allDocs = new StringBuilder();
            for (FinancialDocument doc : getDocuments()) {
                allDocs.append(doc.getContent()).append(" ");
            }
            Map<String, Object> result = DocumentIndexer.indexDocument(allDocs.toString(), query);
            sendJson(exchange, result);
        });

        server.createContext("/api/module2/find-patterns", exchange -> {
            String corpus = SampleDataGenerator.buildTransactionCorpus(getTransactions());
            Map<String, Object> result = RepeatedPatternFinder.findRepeatedPatterns(corpus);
            sendJson(exchange, result);
        });

        server.createContext("/api/module2/substring-query", exchange -> {
            Map<String, String> params = parseBody(exchange);
            String query = params.getOrDefault("query", "suspicious");
            StringBuilder allDocs = new StringBuilder();
            for (FinancialDocument doc : getDocuments()) {
                allDocs.append(doc.getContent()).append(" ");
            }
            Map<String, Object> result = SubstringQueryEngine.querySubstrings(allDocs.toString(), query);
            sendJson(exchange, result);
        });

        // ===== MODULE 3: Advanced DP =====

        server.createContext("/api/module3/correct-description", exchange -> {
            Map<String, String> params = parseBody(exchange);
            String input = params.getOrDefault("query", "Salry credit fom TechCrp");
            String[] validDescriptions = {
                "Salary credit from TechCorp Solutions Pvt Ltd",
                "Wire transfer to Global Exports LLC",
                "Monthly EMI payment for home loan",
                "Online purchase from Amazon India",
                "Utility bill payment to BESCOM",
                "Insurance premium payment to LIC",
                "Mutual fund SIP investment",
                "Credit card bill payment",
                "International wire transfer",
                "Vendor payment for bulk order"
            };
            Map<String, Object> result = DescriptionCorrector.correctDescription(input, validDescriptions);
            sendJson(exchange, result);
        });

        server.createContext("/api/module3/handle-error", exchange -> {
            Map<String, String> params = parseBody(exchange);
            String errorData = params.getOrDefault("errorData", "ACC1234567890");
            String correctData = params.getOrDefault("correctData", "ACC1234557890");
            Map<String, Object> result = DataErrorHandler.handleDataError(errorData, correctData);
            sendJson(exchange, result);
        });

        server.createContext("/api/module3/combine-operations", exchange -> {
            Map<String, String> params = parseBody(exchange);
            double budget = 500000;
            try { budget = Double.parseDouble(params.getOrDefault("budget", "500000")); } catch (Exception e) {}
            Map<String, Object> result = OperationCombinator.selectOperations(budget);
            sendJson(exchange, result);
        });

        server.createContext("/api/module3/optimize-analysis", exchange -> {
            Map<String, Object> result = AnalysisOptimizer.optimizeAnalysis();
            sendJson(exchange, result);
        });

        // ===== MODULE 4: Network Flow =====

        server.createContext("/api/module4/transaction-flow", exchange -> {
            Map<String, Object> result = TransactionFlowAnalyzer.analyzeTransactionFlow();
            sendJson(exchange, result);
        });

        server.createContext("/api/module4/network-capacity", exchange -> {
            Map<String, Object> result = NetworkCapacityAnalyzer.analyzeNetworkCapacity();
            sendJson(exchange, result);
        });

        server.createContext("/api/module4/large-network", exchange -> {
            Map<String, Object> result = LargeNetworkProcessor.processLargeNetwork();
            sendJson(exchange, result);
        });

        server.createContext("/api/module4/assign-analysts", exchange -> {
            Map<String, Object> result = AnalystCaseAssigner.assignAnalystsToCases();
            sendJson(exchange, result);
        });

        server.createContext("/api/module4/critical-channels", exchange -> {
            Map<String, Object> result = CriticalChannelIdentifier.identifyCriticalChannels();
            sendJson(exchange, result);
        });

        // ===== MODULE 5: NP-Completeness =====

        server.createContext("/api/module5/validate-rules", exchange -> {
            Map<String, Object> result = RuleConstraintValidator.validateRules();
            sendJson(exchange, result);
        });

        server.createContext("/api/module5/analyze-constraints", exchange -> {
            Map<String, Object> result = ConstraintAnalyzer.analyzeConstraints();
            sendJson(exchange, result);
        });

        server.createContext("/api/module5/analyze-conflicts", exchange -> {
            Map<String, Object> result = ConflictAnalyzer.analyzeConflicts();
            sendJson(exchange, result);
        });

        server.createContext("/api/module5/solve-dependencies", exchange -> {
            Map<String, Object> result = DependencyCoverSolver.solveDependencies();
            sendJson(exchange, result);
        });

        // ===== MODULE 6: Randomised & Parallel Algorithms =====

        server.createContext("/api/module6/monte-carlo-risk", exchange -> {
            Map<String, Object> result = MonteCarloRiskEstimator.estimateRisk();
            sendJson(exchange, result);
        });

        server.createContext("/api/module6/las-vegas-fraud", exchange -> {
            Map<String, Object> result = LasVegasFraudDetector.detectFraud();
            sendJson(exchange, result);
        });

        server.createContext("/api/module6/parallel-analytics", exchange -> {
            Map<String, Object> result = ParallelPrefixSumReducer.computeParallelAnalytics();
            sendJson(exchange, result);
        });

        server.createContext("/api/module6/work-span-analysis", exchange -> {
            Map<String, Object> result = WorkSpanAnalyzer.analyzeWorkSpan();
            sendJson(exchange, result);
        });

        // ===== Swagger & OpenAPI Documentation Endpoints =====
        server.createContext("/swagger", exchange -> sendHtml(exchange, SwaggerDocs.getSwaggerHtml()));
        server.createContext("/swagger-ui", exchange -> sendHtml(exchange, SwaggerDocs.getSwaggerHtml()));
        server.createContext("/docs", exchange -> sendHtml(exchange, SwaggerDocs.getSwaggerHtml()));
        server.createContext("/api/openapi.json", exchange -> sendRawJson(exchange, SwaggerDocs.getOpenApiJson()));
        server.createContext("/swagger.json", exchange -> sendRawJson(exchange, SwaggerDocs.getOpenApiJson()));

        // Root Handler: default to Swagger UI
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/") || path.equals("/swagger") || path.equals("/docs") || path.equals("/swagger-ui")) {
                sendHtml(exchange, SwaggerDocs.getSwaggerHtml());
            } else if (path.equals("/api/openapi.json") || path.equals("/swagger.json")) {
                sendRawJson(exchange, SwaggerDocs.getOpenApiJson());
            } else {
                String notFound = "<!DOCTYPE html><html><head><title>404 - FinServe</title>" +
                    "<style>body{font-family:sans-serif;padding:40px;background:#f8fafc;color:#0f172a;}" +
                    "a{color:#2563eb;text-decoration:none;font-weight:600;}</style></head><body>" +
                    "<h2>404 - Endpoint Not Found</h2><p>Path: <code>" + path + "</code></p>" +
                    "<p>Explore available APIs in <a href='/swagger'>Swagger UI &rarr;</a></p></body></html>";
                byte[] b = notFound.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(404, b.length);
                OutputStream os = exchange.getResponseBody();
                os.write(b);
                os.close();
            }
        });
    }

    private Map<String, String> parseBody(HttpExchange exchange) throws IOException {
        // Handle both GET (query params) and POST (body)
        String method = exchange.getRequestMethod().toUpperCase();
        if (method.equals("GET")) {
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = new LinkedHashMap<>();
            if (query != null) {
                for (String param : query.split("&")) {
                    String[] kv = param.split("=", 2);
                    if (kv.length == 2) {
                        params.put(java.net.URLDecoder.decode(kv[0], "UTF-8"),
                                   java.net.URLDecoder.decode(kv[1], "UTF-8"));
                    }
                }
            }
            return params;
        }

        InputStream is = exchange.getRequestBody();
        byte[] body = is.readAllBytes();
        String bodyStr = new String(body, StandardCharsets.UTF_8).trim();
        if (bodyStr.isEmpty()) return new LinkedHashMap<>();
        if (bodyStr.startsWith("{")) {
            return JsonUtil.parseJsonObject(bodyStr);
        }
        // Treat as query string
        Map<String, String> params = new LinkedHashMap<>();
        for (String param : bodyStr.split("&")) {
            String[] kv = param.split("=", 2);
            if (kv.length == 2) params.put(kv[0].trim(), kv[1].trim());
        }
        return params;
    }

    private String escapeCsv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private void sendJson(HttpExchange exchange, Map<String, Object> data) throws IOException {
        String json = JsonUtil.toJson(data);
        sendRawJson(exchange, json);
    }

    private void sendRawJson(HttpExchange exchange, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        // CORS headers
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        exchange.sendResponseHeaders(200, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private void sendHtml(HttpExchange exchange, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        exchange.sendResponseHeaders(200, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    public void start() {
        server.start();
        int port = server.getAddress().getPort();
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║   FinServe - Intelligent Banking Analytics Platform  ║");
        System.out.println("║   API Server:  http://localhost:" + port + "                 ║");
        System.out.println("║   Swagger UI:  http://localhost:" + port + "/swagger         ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println("  Dataset: " + datasetManager.getActiveDatasetName());
        System.out.println("  Loaded: " + getTransactions().size() + " transactions, " +
            getAccounts().size() + " accounts, " + getDocuments().size() + " documents");
        System.out.println("  Modules: M1(String) M2(Suffix) M3(DP) M4(Flow) M5(NP) M6(Rand/Parallel)");
        System.out.println("  Endpoints: /api/module[1-6]/*  |  /api/dataset/*  |  Docs: /swagger");
    }

    public void stop() {
        server.stop(0);
    }
}
