package com.finserve.server;

/**
 * Embedded Swagger UI and OpenAPI 3.0 specification for FinServe.
 * Provides interactive documentation and testing for all 21 DSA endpoints.
 */
public class SwaggerDocs {

    public static String getSwaggerHtml() {
        return "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "  <meta charset=\"UTF-8\">\n" +
            "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "  <title>FinServe - Swagger API Explorer</title>\n" +
            "  <link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/swagger-ui-dist@5/swagger-ui.css\">\n" +
            "  <link rel=\"icon\" type=\"image/svg+xml\" href=\"data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'><rect width='100' height='100' rx='20' fill='%231E40AF'/><text x='50%' y='55%' font-size='44' font-weight='bold' fill='white' dominant-baseline='middle' text-anchor='middle'>FS</text></svg>\">\n" +
            "  <style>\n" +
            "    body { margin: 0; padding: 0; background: #f8fafc; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; color: #0f172a; }\n" +
            "    .fs-header {\n" +
            "      background: #0f172a;\n" +
            "      color: #ffffff;\n" +
            "      padding: 14px 28px;\n" +
            "      display: flex;\n" +
            "      align-items: center;\n" +
            "      justify-content: space-between;\n" +
            "      border-bottom: 3px solid #2563eb;\n" +
            "      box-shadow: 0 4px 12px rgba(15, 23, 42, 0.15);\n" +
            "    }\n" +
            "    .fs-brand {\n" +
            "      display: flex;\n" +
            "      align-items: center;\n" +
            "      gap: 12px;\n" +
            "    }\n" +
            "    .fs-logo {\n" +
            "      background: linear-gradient(135deg, #2563eb, #1d4ed8);\n" +
            "      color: #ffffff;\n" +
            "      width: 34px;\n" +
            "      height: 34px;\n" +
            "      display: flex;\n" +
            "      align-items: center;\n" +
            "      justify-content: center;\n" +
            "      border-radius: 8px;\n" +
            "      font-weight: 800;\n" +
            "      font-size: 14px;\n" +
            "      box-shadow: 0 2px 6px rgba(37, 99, 235, 0.4);\n" +
            "    }\n" +
            "    .fs-title {\n" +
            "      font-size: 17px;\n" +
            "      font-weight: 700;\n" +
            "      letter-spacing: -0.01em;\n" +
            "    }\n" +
            "    .fs-badge {\n" +
            "      background: #2563eb;\n" +
            "      color: #ffffff;\n" +
            "      font-size: 11px;\n" +
            "      font-weight: 600;\n" +
            "      padding: 3px 9px;\n" +
            "      border-radius: 9999px;\n" +
            "      text-transform: uppercase;\n" +
            "      letter-spacing: 0.05em;\n" +
            "    }\n" +
            "    .fs-nav {\n" +
            "      display: flex;\n" +
            "      align-items: center;\n" +
            "      gap: 18px;\n" +
            "    }\n" +
            "    .fs-nav a {\n" +
            "      color: #94a3b8;\n" +
            "      text-decoration: none;\n" +
            "      font-size: 13px;\n" +
            "      font-weight: 500;\n" +
            "      transition: color 0.15s ease;\n" +
            "    }\n" +
            "    .fs-nav a:hover {\n" +
            "      color: #ffffff;\n" +
            "    }\n" +
            "    .fs-nav .btn-ui {\n" +
            "      background: rgba(37, 99, 235, 0.2);\n" +
            "      border: 1px solid rgba(37, 99, 235, 0.6);\n" +
            "      color: #60a5fa;\n" +
            "      padding: 6px 14px;\n" +
            "      border-radius: 6px;\n" +
            "    }\n" +
            "    .fs-nav .btn-ui:hover {\n" +
            "      background: #2563eb;\n" +
            "      color: #ffffff;\n" +
            "    }\n" +
            "    .swagger-ui .topbar { display: none; }\n" +
            "    .swagger-ui { max-width: 1240px; margin: 0 auto; padding: 24px 20px 60px; }\n" +
            "    .swagger-ui .info { margin: 24px 0; }\n" +
            "    .swagger-ui .info .title { color: #0f172a; font-weight: 800; letter-spacing: -0.02em; }\n" +
            "    .swagger-ui .opblock.opblock-post { border-color: #2563eb; background: rgba(37, 99, 235, 0.04); }\n" +
            "    .swagger-ui .opblock.opblock-post .opblock-summary-method { background: #2563eb; }\n" +
            "    .swagger-ui .opblock.opblock-get { border-color: #0284c7; background: rgba(2, 132, 199, 0.04); }\n" +
            "    .swagger-ui .opblock.opblock-get .opblock-summary-method { background: #0284c7; }\n" +
            "    .swagger-ui .btn.execute { background-color: #2563eb; border-color: #2563eb; color: #fff; }\n" +
            "    #fallback-ui { display: none; max-width: 1100px; margin: 40px auto; padding: 24px; background: #fff; border-radius: 12px; border: 1px solid #e2e8f0; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); }\n" +
            "    .ep-card { border: 1px solid #e2e8f0; border-radius: 8px; margin-bottom: 12px; overflow: hidden; }\n" +
            "    .ep-header { padding: 12px 16px; background: #f8fafc; display: flex; align-items: center; justify-content: space-between; font-family: monospace; font-size: 13px; }\n" +
            "    .ep-badge { padding: 3px 8px; border-radius: 4px; font-weight: bold; color: white; background: #2563eb; margin-right: 10px; }\n" +
            "    .ep-body { padding: 14px 16px; font-size: 13px; color: #475569; }\n" +
            "    .ep-btn { background: #2563eb; color: #fff; border: none; padding: 6px 14px; border-radius: 6px; cursor: pointer; font-size: 12px; font-weight: 600; }\n" +
            "  </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "  <header class=\"fs-header\">\n" +
            "    <div class=\"fs-brand\">\n" +
            "      <div class=\"fs-logo\">FS</div>\n" +
            "      <div>\n" +
            "        <span class=\"fs-title\">FinServe Banking Analytics</span>\n" +
            "        <span class=\"fs-badge\" style=\"margin-left: 8px;\">Swagger OpenAPI 3.0</span>\n" +
            "      </div>\n" +
            "    </div>\n" +
            "    <nav class=\"fs-nav\">\n" +
            "      <a href=\"/api/openapi.json\" target=\"_blank\">OpenAPI JSON</a>\n" +
            "      <a href=\"/api/health\" target=\"_blank\">Health Check</a>\n" +
            "      <a href=\"http://localhost:5173\" class=\"btn-ui\">Go to Web UI &rarr;</a>\n" +
            "    </nav>\n" +
            "  </header>\n" +
            "\n" +
            "  <div id=\"swagger-ui\"></div>\n" +
            "\n" +
            "  <div id=\"fallback-ui\">\n" +
            "    <h2 style=\"margin-top:0; color:#0f172a;\">FinServe Interactive API Directory</h2>\n" +
            "    <p style=\"color:#64748b;\">The remote Swagger UI assets are loading or unavailable offline. You can test live endpoints below directly against port 8080:</p>\n" +
            "    <div id=\"fallback-endpoints\"></div>\n" +
            "  </div>\n" +
            "\n" +
            "  <script src=\"https://cdn.jsdelivr.net/npm/swagger-ui-dist@5/swagger-ui-bundle.js\" onerror=\"loadFallback()\"></script>\n" +
            "  <script src=\"https://cdn.jsdelivr.net/npm/swagger-ui-dist@5/swagger-ui-standalone-preset.js\"></script>\n" +
            "  <script>\n" +
            "    let uiLoaded = false;\n" +
            "    window.onload = function() {\n" +
            "      if (typeof SwaggerUIBundle !== 'undefined') {\n" +
            "        uiLoaded = true;\n" +
            "        window.ui = SwaggerUIBundle({\n" +
            "          url: '/api/openapi.json',\n" +
            "          dom_id: '#swagger-ui',\n" +
            "          deepLinking: true,\n" +
            "          presets: [\n" +
            "            SwaggerUIBundle.presets.apis,\n" +
            "            SwaggerUIStandalonePreset\n" +
            "          ],\n" +
            "          layout: 'BaseLayout',\n" +
            "          defaultModelsExpandDepth: -1\n" +
            "        });\n" +
            "      } else {\n" +
            "        loadFallback();\n" +
            "      }\n" +
            "    };\n" +
            "\n" +
            "    setTimeout(function() {\n" +
            "      if (!uiLoaded && typeof SwaggerUIBundle === 'undefined') {\n" +
            "        loadFallback();\n" +
            "      }\n" +
            "    }, 1500);\n" +
            "\n" +
            "    function loadFallback() {\n" +
            "      const fb = document.getElementById('fallback-ui');\n" +
            "      if (!fb) return;\n" +
            "      fb.style.display = 'block';\n" +
            "      fetch('/api/openapi.json')\n" +
            "        .then(r => r.json())\n" +
            "        .then(spec => {\n" +
            "          const container = document.getElementById('fallback-endpoints');\n" +
            "          container.innerHTML = '';\n" +
            "          for (const path in spec.paths) {\n" +
            "            const methods = spec.paths[path];\n" +
            "            for (const m in methods) {\n" +
            "              const op = methods[m];\n" +
            "              const card = document.createElement('div');\n" +
            "              card.className = 'ep-card';\n" +
            "              card.innerHTML = `\n" +
            "                <div class=\"ep-header\">\n" +
            "                  <div>\n" +
            "                    <span class=\"ep-badge\">${m.toUpperCase()}</span>\n" +
            "                    <strong>${path}</strong>\n" +
            "                  </div>\n" +
            "                  <button class=\"ep-btn\" onclick=\"testEndpoint('${path}', '${m.toUpperCase()}')\">Test Live</button>\n" +
            "                </div>\n" +
            "                <div class=\"ep-body\">\n" +
            "                  <div>${op.summary || ''}</div>\n" +
            "                  <div style=\"font-size:11px; color:#94a3b8; margin-top:4px;\">Tag: ${op.tags ? op.tags.join(', ') : 'General'}</div>\n" +
            "                  <div id=\"res-${path.replace(/[^a-zA-Z0-9]/g, '_')}\" style=\"margin-top:10px; display:none;\">\n" +
            "                    <pre style=\"background:#0f172a; color:#38bdf8; padding:10px; border-radius:6px; font-size:11px; max-height:200px; overflow:auto;\"></pre>\n" +
            "                  </div>\n" +
            "                </div>\n" +
            "              `;\n" +
            "              container.appendChild(card);\n" +
            "            }\n" +
            "          }\n" +
            "        });\n" +
            "    }\n" +
            "\n" +
            "    window.testEndpoint = function(path, method) {\n" +
            "      const cleanId = 'res-' + path.replace(/[^a-zA-Z0-9]/g, '_');\n" +
            "      const resDiv = document.getElementById(cleanId);\n" +
            "      if (!resDiv) return;\n" +
            "      resDiv.style.display = 'block';\n" +
            "      resDiv.querySelector('pre').textContent = 'Calling ' + path + '...';\n" +
            "      fetch(path, { method: method })\n" +
            "        .then(r => r.json())\n" +
            "        .then(data => {\n" +
            "          resDiv.querySelector('pre').textContent = JSON.stringify(data, null, 2);\n" +
            "        })\n" +
            "        .catch(err => {\n" +
            "          resDiv.querySelector('pre').textContent = 'Error: ' + err.message;\n" +
            "        });\n" +
            "    };\n" +
            "  </script>\n" +
            "</body>\n" +
            "</html>";
    }

    public static String getOpenApiJson() {
        return "{\n" +
            "  \"openapi\": \"3.0.3\",\n" +
            "  \"info\": {\n" +
            "    \"title\": \"FinServe - Institutional Banking Analytics & Settlement Platform\",\n" +
            "    \"description\": \"Comprehensive Data Structures and Algorithms (DSA) API engine executing real-time financial string matching, suffix archival indexing, dynamic programming optimizations, interbank network flow, regulatory NP-reduction validation, and randomised parallel computation.\",\n" +
            "    \"version\": \"1.0.0\",\n" +
            "    \"contact\": {\n" +
            "      \"name\": \"FinServe Algorithmic Engineering Team\",\n" +
            "      \"url\": \"http://localhost:5173\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"servers\": [\n" +
            "    { \"url\": \"http://localhost:8080\", \"description\": \"Direct Backend API Server (Java 17)\" },\n" +
            "    { \"url\": \"http://localhost:5173\", \"description\": \"Vite Frontend Dev Proxy\" }\n" +
            "  ],\n" +
            "  \"tags\": [\n" +
            "    { \"name\": \"System & Health\", \"description\": \"Server health status and in-memory sample dataset summaries\" },\n" +
            "    { \"name\": \"Module 1: String Algorithms\", \"description\": \"KMP, Z-Function, Rabin-Karp, and Aho-Corasick algorithms for transaction searching & AML screening\" },\n" +
            "    { \"name\": \"Module 2: Suffix Structures\", \"description\": \"Suffix Array, SA-IS, Kasai LCP Array, and Suffix Automaton for document & transaction archival\" },\n" +
            "    { \"name\": \"Module 3: Advanced Dynamic Programming\", \"description\": \"Levenshtein, Weighted Edit Distance, Bitmask DP, and Matrix-Chain Multiplication for error correction & portfolio optimization\" },\n" +
            "    { \"name\": \"Module 4: Network Flow & Matching\", \"description\": \"Ford-Fulkerson, Edmonds-Karp, Dinic's, Bipartite Matching, and Min-Cut for interbank liquidity routing\" },\n" +
            "    { \"name\": \"Module 5: NP-Completeness & Reductions\", \"description\": \"3-SAT Solver, 3-SAT to CLIQUE, Conflict Reductions, and Vertex Cover Approximations for regulatory compliance\" },\n" +
            "    { \"name\": \"Module 6: Randomised & Parallel Algorithms\", \"description\": \"Monte Carlo simulation, Las Vegas randomised algorithms, Parallel Prefix Sum, Parallel Reduce, and Work-Span Analysis for probabilistic risk estimation and parallel batch processing\" }\n" +
            "  ],\n" +
            "  \"paths\": {\n" +
            "    \"/api/health\": {\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"System & Health\"],\n" +
            "        \"summary\": \"System Health & Database Status\",\n" +
            "        \"description\": \"Returns server operational status, total modules (5), and in-memory counts of transactions, accounts, and audit documents.\",\n" +
            "        \"responses\": { \"200\": { \"description\": \"System is healthy and ready to process requests\" } }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/api/data/summary\": {\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"System & Health\"],\n" +
            "        \"summary\": \"In-Memory Financial Ledger Summary\",\n" +
            "        \"description\": \"Returns overview statistics and sample preview of populated accounts and ledger transactions.\",\n" +
            "        \"responses\": { \"200\": { \"description\": \"Sample ledger summary records\" } }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/api/module1/search-code\": {\n" +
            "      \"post\": {\n" +
            "        \"tags\": [\"Module 1: String Algorithms\"],\n" +
            "        \"summary\": \"Payment Reference Lookup (KMP Algorithm)\",\n" +
            "        \"description\": \"Scans transactions using Knuth-Morris-Pratt O(n + m) pattern matching with precomputed failure function.\",\n" +
            "        \"requestBody\": {\n" +
            "          \"content\": { \"application/json\": { \"schema\": { \"type\": \"object\", \"properties\": { \"query\": { \"type\": \"string\", \"default\": \"PAY-2024\" } } } } }\n" +
            "        },\n" +
            "        \"responses\": { \"200\": { \"description\": \"Matching transactions and occurrence indices\" } }\n" +
            "      },\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"Module 1: String Algorithms\"],\n" +
            "        \"summary\": \"Payment Reference Lookup (KMP Algorithm - GET)\",\n" +
            "        \"parameters\": [{ \"name\": \"query\", \"in\": \"query\", \"schema\": { \"type\": \"string\", \"default\": \"PAY-2024\" } }],\n" +
            "        \"responses\": { \"200\": { \"description\": \"Matching transactions and occurrence indices\" } }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/api/module1/analyze-patterns\": {\n" +
            "      \"post\": {\n" +
            "        \"tags\": [\"Module 1: String Algorithms\"],\n" +
            "        \"summary\": \"Recurring Payment Pattern Detector (Z-Function)\",\n" +
            "        \"description\": \"Computes Z-array values in O(n + m) to identify recurring periodic debits, standing instructions, and subscriptions.\",\n" +
            "        \"requestBody\": {\n" +
            "          \"content\": { \"application/json\": { \"schema\": { \"type\": \"object\", \"properties\": { \"query\": { \"type\": \"string\", \"default\": \"payment\" } } } } }\n" +
            "        },\n" +
            "        \"responses\": { \"200\": { \"description\": \"Discovered recurring pattern occurrences\" } }\n" +
            "      },\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"Module 1: String Algorithms\"],\n" +
            "        \"summary\": \"Recurring Payment Pattern Detector (Z-Function - GET)\",\n" +
            "        \"parameters\": [{ \"name\": \"query\", \"in\": \"query\", \"schema\": { \"type\": \"string\", \"default\": \"payment\" } }],\n" +
            "        \"responses\": { \"200\": { \"description\": \"Discovered recurring pattern occurrences\" } }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/api/module1/lookup-code\": {\n" +
            "      \"post\": {\n" +
            "        \"tags\": [\"Module 1: String Algorithms\"],\n" +
            "        \"summary\": \"Bank Routing & SWIFT Verifier (Rabin-Karp Rolling Hash)\",\n" +
            "        \"description\": \"Verifies SWIFT BIC, IFSC, and clearing network tokens using polynomial rolling hash with O(1) space.\",\n" +
            "        \"requestBody\": {\n" +
            "          \"content\": { \"application/json\": { \"schema\": { \"type\": \"object\", \"properties\": { \"query\": { \"type\": \"string\", \"default\": \"SWIFT\" } } } } }\n" +
            "        },\n" +
            "        \"responses\": { \"200\": { \"description\": \"Routing authentication results\" } }\n" +
            "      },\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"Module 1: String Algorithms\"],\n" +
            "        \"summary\": \"Bank Routing & SWIFT Verifier (Rabin-Karp - GET)\",\n" +
            "        \"parameters\": [{ \"name\": \"query\", \"in\": \"query\", \"schema\": { \"type\": \"string\", \"default\": \"SWIFT\" } }],\n" +
            "        \"responses\": { \"200\": { \"description\": \"Routing authentication results\" } }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/api/module1/detect-fraud\": {\n" +
            "      \"post\": {\n" +
            "        \"tags\": [\"Module 1: String Algorithms\"],\n" +
            "        \"summary\": \"Anti-Money Laundering Scanner (Aho-Corasick Automaton)\",\n" +
            "        \"description\": \"Simultaneously screens all transaction records against multiple AML indicators in a single O(n + m + z) pass.\",\n" +
            "        \"requestBody\": {\n" +
            "          \"content\": { \"application/json\": { \"schema\": { \"type\": \"object\", \"properties\": { \"patterns\": { \"type\": \"string\", \"default\": \"shell company, money laundering, structuring, offshore\" } } } } }\n" +
            "        },\n" +
            "        \"responses\": { \"200\": { \"description\": \"Flagged transactions with risk severity scores\" } }\n" +
            "      },\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"Module 1: String Algorithms\"],\n" +
            "        \"summary\": \"Anti-Money Laundering Scanner (Aho-Corasick - GET)\",\n" +
            "        \"parameters\": [{ \"name\": \"patterns\", \"in\": \"query\", \"schema\": { \"type\": \"string\", \"default\": \"shell company, money laundering, structuring, offshore\" } }],\n" +
            "        \"responses\": { \"200\": { \"description\": \"Flagged transactions with risk severity scores\" } }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/api/module2/index-transactions\": {\n" +
            "      \"post\": {\n" +
            "        \"tags\": [\"Module 2: Suffix Structures\"],\n" +
            "        \"summary\": \"Historical Transaction Substring Search (Suffix Array)\",\n" +
            "        \"description\": \"Builds a sorted suffix array over the transaction corpus for fast logarithmic-time substring discovery.\",\n" +
            "        \"requestBody\": {\n" +
            "          \"content\": { \"application/json\": { \"schema\": { \"type\": \"object\", \"properties\": { \"query\": { \"type\": \"string\", \"default\": \"transfer\" } } } } }\n" +
            "        },\n" +
            "        \"responses\": { \"200\": { \"description\": \"Discovered suffix matches and textual context\" } }\n" +
            "      },\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"Module 2: Suffix Structures\"],\n" +
            "        \"summary\": \"Historical Transaction Substring Search (GET)\",\n" +
            "        \"parameters\": [{ \"name\": \"query\", \"in\": \"query\", \"schema\": { \"type\": \"string\", \"default\": \"transfer\" } }],\n" +
            "        \"responses\": { \"200\": { \"description\": \"Discovered suffix matches and textual context\" } }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/api/module2/index-documents\": {\n" +
            "      \"post\": {\n" +
            "        \"tags\": [\"Module 2: Suffix Structures\"],\n" +
            "        \"summary\": \"Regulatory Document Full-Text Indexer (SA-IS Algorithm)\",\n" +
            "        \"description\": \"Constructs linear-time O(n) suffix arrays on compliance filings and loan covenants via Induced Sorting.\",\n" +
            "        \"requestBody\": {\n" +
            "          \"content\": { \"application/json\": { \"schema\": { \"type\": \"object\", \"properties\": { \"query\": { \"type\": \"string\", \"default\": \"compliance\" } } } } }\n" +
            "        },\n" +
            "        \"responses\": { \"200\": { \"description\": \"Document indexing benchmark & search results\" } }\n" +
            "      },\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"Module 2: Suffix Structures\"],\n" +
            "        \"summary\": \"Regulatory Document Full-Text Indexer (SA-IS - GET)\",\n" +
            "        \"parameters\": [{ \"name\": \"query\", \"in\": \"query\", \"schema\": { \"type\": \"string\", \"default\": \"compliance\" } }],\n" +
            "        \"responses\": { \"200\": { \"description\": \"Document indexing benchmark & search results\" } }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/api/module2/find-patterns\": {\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"Module 2: Suffix Structures\"],\n" +
            "        \"summary\": \"Contract Clause Repetition Finder (Kasai LCP Array)\",\n" +
            "        \"description\": \"Computes Longest Common Prefix array in O(n) time to surface repeated boilerplate clauses in legal disclosures.\",\n" +
            "        \"responses\": { \"200\": { \"description\": \"Top repeated legal clauses and occurrence offsets\" } }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/api/module2/substring-query\": {\n" +
            "      \"post\": {\n" +
            "        \"tags\": [\"Module 2: Suffix Structures\"],\n" +
            "        \"summary\": \"Audit Stream Content Inspector (Suffix Automaton SAM)\",\n" +
            "        \"description\": \"Online construction of minimal DFA/DAWG recognizing all substrings in linear time with exact O(m) presence checks.\",\n" +
            "        \"requestBody\": {\n" +
            "          \"content\": { \"application/json\": { \"schema\": { \"type\": \"object\", \"properties\": { \"query\": { \"type\": \"string\", \"default\": \"suspicious\" } } } } }\n" +
            "        },\n" +
            "        \"responses\": { \"200\": { \"description\": \"Verification status and distinct substring count\" } }\n" +
            "      },\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"Module 2: Suffix Structures\"],\n" +
            "        \"summary\": \"Audit Stream Content Inspector (SAM - GET)\",\n" +
            "        \"parameters\": [{ \"name\": \"query\", \"in\": \"query\", \"schema\": { \"type\": \"string\", \"default\": \"suspicious\" } }],\n" +
            "        \"responses\": { \"200\": { \"description\": \"Verification status and distinct substring count\" } }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/api/module3/correct-description\": {\n" +
            "      \"post\": {\n" +
            "        \"tags\": [\"Module 3: Advanced Dynamic Programming\"],\n" +
            "        \"summary\": \"Payment Memo Spell Corrector (Levenshtein / Damerau-Levenshtein)\",\n" +
            "        \"description\": \"Resolves typographical errors, transpositions, and abbreviations in customer payment descriptions with full traceback.\",\n" +
            "        \"requestBody\": {\n" +
            "          \"content\": { \"application/json\": { \"schema\": { \"type\": \"object\", \"properties\": { \"query\": { \"type\": \"string\", \"default\": \"Salry credit fom TechCrp\" } } } } }\n" +
            "        },\n" +
            "        \"responses\": { \"200\": { \"description\": \"Standardized description suggestions and edit distance\" } }\n" +
            "      },\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"Module 3: Advanced Dynamic Programming\"],\n" +
            "        \"summary\": \"Payment Memo Spell Corrector (GET)\",\n" +
            "        \"parameters\": [{ \"name\": \"query\", \"in\": \"query\", \"schema\": { \"type\": \"string\", \"default\": \"Salry credit fom TechCrp\" } }],\n" +
            "        \"responses\": { \"200\": { \"description\": \"Standardized description suggestions and edit distance\" } }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/api/module3/handle-error\": {\n" +
            "      \"post\": {\n" +
            "        \"tags\": [\"Module 3: Advanced Dynamic Programming\"],\n" +
            "        \"summary\": \"Account Number Rectifier (Weighted Edit Distance)\",\n" +
            "        \"description\": \"Calculates error reconciliation using asymmetric penalties heavily weighting digit alterations over alphabetical typos.\",\n" +
            "        \"requestBody\": {\n" +
            "          \"content\": { \"application/json\": { \"schema\": { \"type\": \"object\", \"properties\": { \"errorData\": { \"type\": \"string\", \"default\": \"ACC1234567890\" }, \"correctData\": { \"type\": \"string\", \"default\": \"ACC1234557890\" } } } } }\n" +
            "        },\n" +
            "        \"responses\": { \"200\": { \"description\": \"Weighted reconciliation cost and error severity breakdown\" } }\n" +
            "      },\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"Module 3: Advanced Dynamic Programming\"],\n" +
            "        \"summary\": \"Account Number Rectifier (GET)\",\n" +
            "        \"parameters\": [\n" +
            "          { \"name\": \"errorData\", \"in\": \"query\", \"schema\": { \"type\": \"string\", \"default\": \"ACC1234567890\" } },\n" +
            "          { \"name\": \"correctData\", \"in\": \"query\", \"schema\": { \"type\": \"string\", \"default\": \"ACC1234557890\" } }\n" +
            "        ],\n" +
            "        \"responses\": { \"200\": { \"description\": \"Weighted reconciliation cost and error severity breakdown\" } }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/api/module3/combine-operations\": {\n" +
            "      \"post\": {\n" +
            "        \"tags\": [\"Module 3: Advanced Dynamic Programming\"],\n" +
            "        \"summary\": \"Asset Portfolio Allocation Optimizer (Bitmask DP)\",\n" +
            "        \"description\": \"Evaluates all 2^n subsets of financial holdings to find the optimal combination maximizing yield within capital limits.\",\n" +
            "        \"requestBody\": {\n" +
            "          \"content\": { \"application/json\": { \"schema\": { \"type\": \"object\", \"properties\": { \"budget\": { \"type\": \"number\", \"default\": 500000 } } } } }\n" +
            "        },\n" +
            "        \"responses\": { \"200\": { \"description\": \"Optimal selected portfolio operations and ROI\" } }\n" +
            "      },\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"Module 3: Advanced Dynamic Programming\"],\n" +
            "        \"summary\": \"Asset Portfolio Allocation Optimizer (GET)\",\n" +
            "        \"parameters\": [{ \"name\": \"budget\", \"in\": \"query\", \"schema\": { \"type\": \"number\", \"default\": 500000 } }],\n" +
            "        \"responses\": { \"200\": { \"description\": \"Optimal selected portfolio operations and ROI\" } }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/api/module3/optimize-analysis\": {\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"Module 3: Advanced Dynamic Programming\"],\n" +
            "        \"summary\": \"Clearing Pipeline Multi-Stage Scheduler (Matrix-Chain DP)\",\n" +
            "        \"description\": \"Calculates optimal parenthesization order for chained financial transformations to minimize computational cost.\",\n" +
            "        \"responses\": { \"200\": { \"description\": \"Optimal execution order and computational savings percentage\" } }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/api/module4/transaction-flow\": {\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"Module 4: Network Flow & Matching\"],\n" +
            "        \"summary\": \"Interbank Liquidity Flow Analyzer (Ford-Fulkerson Method)\",\n" +
            "        \"description\": \"Computes maximum transaction throughput between banking nodes and identifies saturated corridor bottlenecks.\",\n" +
            "        \"responses\": { \"200\": { \"description\": \"Max flow volume and edge utilization matrix\" } }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/api/module4/network-capacity\": {\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"Module 4: Network Flow & Matching\"],\n" +
            "        \"summary\": \"Shortest Settlement Corridor Router (Edmonds-Karp Algorithm)\",\n" +
            "        \"description\": \"Computes guaranteed polynomial max-flow using BFS shortest augmenting paths for clearing reserve pools.\",\n" +
            "        \"responses\": { \"200\": { \"description\": \"Max settlement capacity and augmenting paths found\" } }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/api/module4/large-network\": {\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"Module 4: Network Flow & Matching\"],\n" +
            "        \"summary\": \"Bulk Clearing Engine (Dinic's Algorithm)\",\n" +
            "        \"description\": \"Simulates large-scale corporate batch transfers using level graphs and blocking flows for superior throughput.\",\n" +
            "        \"responses\": { \"200\": { \"description\": \"Dinic's algorithm performance benchmark and flow distribution\" } }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/api/module4/assign-analysts\": {\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"Module 4: Network Flow & Matching\"],\n" +
            "        \"summary\": \"Investigation Case Workload Assigner (Bipartite Matching)\",\n" +
            "        \"description\": \"Optimally matches fraud cases with specialist investigators based on capabilities using augmenting paths.\",\n" +
            "        \"responses\": { \"200\": { \"description\": \"Casework assignment matrix and allocation efficiency\" } }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/api/module4/critical-channels\": {\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"Module 4: Network Flow & Matching\"],\n" +
            "        \"summary\": \"Settlement Bottleneck Monitor (Max-Flow Min-Cut Theorem)\",\n" +
            "        \"description\": \"Identifies critical transit channels whose disruption throttles liquidity, isolating single points of failure.\",\n" +
            "        \"responses\": { \"200\": { \"description\": \"Critical channels and verified min-cut capacity\" } }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/api/module5/validate-rules\": {\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"Module 5: NP-Completeness & Reductions\"],\n" +
            "        \"summary\": \"Regulatory Policy Constraint Validator (3-SAT Solver)\",\n" +
            "        \"description\": \"Validates whether complex combinations of KYC, transfer limits, and risk policies have satisfying truth assignments.\",\n" +
            "        \"responses\": { \"200\": { \"description\": \"Satisfiability status and compliant configuration assignments\" } }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/api/module5/analyze-constraints\": {\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"Module 5: NP-Completeness & Reductions\"],\n" +
            "        \"summary\": \"Syndicate Collusion Ring Detector (3-SAT to CLIQUE Reduction)\",\n" +
            "        \"description\": \"Reduces financial boolean constraints to a graph to prove rule satisfiability through k-clique detection.\",\n" +
            "        \"responses\": { \"200\": { \"description\": \"Graph reduction metrics and discovered clique interpretation\" } }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/api/module5/analyze-conflicts\": {\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"Module 5: NP-Completeness & Reductions\"],\n" +
            "        \"summary\": \"Counterparty Risk Segregation (Clique -> Independent Set -> Vertex Cover)\",\n" +
            "        \"description\": \"Uncovers collusion syndicates (clique), safe non-conflicting portfolios (independent set), and minimum monitoring nodes.\",\n" +
            "        \"responses\": { \"200\": { \"description\": \"Risk sub-portfolios, safe accounts, and surveillance checkpoints\" } }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/api/module5/solve-dependencies\": {\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"Module 5: NP-Completeness & Reductions\"],\n" +
            "        \"summary\": \"Audit Checkpoint Optimizer (Vertex Cover 2-Approximation)\",\n" +
            "        \"description\": \"Calculates minimal set of system inspection checkpoints to cover 100% of inter-system dependencies with 2x guarantee.\",\n" +
            "        \"responses\": { \"200\": { \"description\": \"Monitored systems, 2-approx comparison, and coverage verification\" } }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/api/module6/monte-carlo-risk\": {\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"Module 6: Randomised & Parallel Algorithms\"],\n" +
            "        \"summary\": \"Portfolio Value-at-Risk Estimator (Monte Carlo Simulation)\",\n" +
            "        \"description\": \"Runs thousands of randomised simulations to estimate VaR and CVaR at configurable confidence levels. Monte Carlo: approximate result, bounded time.\",\n" +
            "        \"responses\": { \"200\": { \"description\": \"VaR estimates, convergence analysis, and simulation benchmarks\" } }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/api/module6/las-vegas-fraud\": {\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"Module 6: Randomised & Parallel Algorithms\"],\n" +
            "        \"summary\": \"Statistical Anomaly Fraud Detector (Las Vegas Quickselect)\",\n" +
            "        \"description\": \"Uses randomised quickselect (expected O(n)) to compute exact median and IQR thresholds for anomaly detection. Las Vegas: always correct, randomised runtime.\",\n" +
            "        \"responses\": { \"200\": { \"description\": \"Flagged anomalous transactions and statistical thresholds\" } }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/api/module6/parallel-analytics\": {\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"Module 6: Randomised & Parallel Algorithms\"],\n" +
            "        \"summary\": \"Parallel Prefix Sum & Reduce Engine (Blelloch Scan + Tree Reduction)\",\n" +
            "        \"description\": \"Computes running balances via parallel prefix sum (O(n) work, O(log n) span) and aggregate statistics via tree-based parallel reduce.\",\n" +
            "        \"responses\": { \"200\": { \"description\": \"Running balances, aggregate stats, and work-span metrics\" } }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/api/module6/work-span-analysis\": {\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"Module 6: Randomised & Parallel Algorithms\"],\n" +
            "        \"summary\": \"Work-Span Pipeline Parallelism Assessor (Brent's Theorem)\",\n" +
            "        \"description\": \"Evaluates work W(n), span S(n), parallelism P = W/S for banking batch pipelines. Projects execution on p processors via T_p ≤ W/p + S.\",\n" +
            "        \"responses\": { \"200\": { \"description\": \"Per-operation work-span breakdown and end-to-end projections\" } }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}\n";
    }
}
