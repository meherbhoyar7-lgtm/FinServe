package com.finserve.data;

import com.finserve.model.*;
import java.util.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DatasetManager — Central registry for managing active datasets.
 * Holds the current transactions, accounts, and documents and allows
 * switching between presets or loading custom user-uploaded data.
 *
 * Thread-safe: all mutators are synchronized so concurrent API calls
 * see a consistent snapshot.
 */
public class DatasetManager {

    // ----- Singleton -----
    private static DatasetManager instance;

    public static synchronized DatasetManager getInstance() {
        if (instance == null) {
            instance = new DatasetManager();
        }
        return instance;
    }

    // ----- Active dataset state -----
    private List<Transaction> transactions;
    private List<Account> accounts;
    private List<FinancialDocument> documents;
    private String activeDatasetId;
    private String activeDatasetName;
    private String activeDatasetDescription;
    private long loadedAt;

    private DatasetManager() {
        // Boot with the default generated dataset
        loadPreset("default");
    }

    // ----- Preset loading -----
    public synchronized void loadPreset(String presetId) {
        switch (presetId) {
            case "aml-suspicious":
                this.transactions = DatasetPresets.generateAMLTransactions(250);
                this.accounts = DatasetPresets.generateAMLAccounts(40);
                this.documents = DatasetPresets.generateAMLDocuments();
                this.activeDatasetId = "aml-suspicious";
                this.activeDatasetName = "AML Suspicious Activity Dataset";
                this.activeDatasetDescription = "250 transactions with high fraud/AML signals — shell companies, structuring, smurfing, offshore layering. Ideal for Module 1 (fraud detection) and Module 5 (compliance rules).";
                break;

            case "swift-forex":
                this.transactions = DatasetPresets.generateSWIFTForexTransactions(200);
                this.accounts = DatasetPresets.generateSWIFTAccounts(30);
                this.documents = DatasetPresets.generateSWIFTDocuments();
                this.activeDatasetId = "swift-forex";
                this.activeDatasetName = "Global SWIFT & Forex Dataset";
                this.activeDatasetDescription = "200 cross-border SWIFT/RTGS transactions across 12 currencies with correspondent banking chains. Ideal for Module 4 (settlement flow) and Module 2 (document archival).";
                break;

            case "retail-highvol":
                this.transactions = DatasetPresets.generateRetailHighVolTransactions(500);
                this.accounts = DatasetPresets.generateRetailAccounts(60);
                this.documents = DatasetPresets.generateRetailDocuments();
                this.activeDatasetId = "retail-highvol";
                this.activeDatasetName = "Retail High-Volume Dataset";
                this.activeDatasetDescription = "500 high-frequency retail transactions — UPI, card payments, EMIs, e-commerce. Ideal for Module 6 (parallel analytics) and Module 3 (data cleansing).";
                break;

            case "synthetic-100":
                this.transactions = SampleDataGenerator.generateTransactions(100);
                this.accounts = SampleDataGenerator.generateAccounts(20);
                this.documents = SampleDataGenerator.generateDocuments();
                this.activeDatasetId = "synthetic-100";
                this.activeDatasetName = "Synthetic Indian Banking Dataset (100 txns)";
                this.activeDatasetDescription = "100 mixed-type banking transactions with 20 accounts across Indian branches. Balanced representation of all transaction types.";
                break;

            case "bank-transactions-2":
            case "default":
            default:
                // Primary Project Default: Load real bank_transactions_data_2.csv
                boolean loaded = loadFromBankTransactionsData2Csv();
                if (!loaded) {
                    this.transactions = SampleDataGenerator.generateTransactions(100);
                    this.accounts = SampleDataGenerator.generateAccounts(20);
                    this.documents = SampleDataGenerator.generateDocuments();
                    this.activeDatasetId = "default";
                    this.activeDatasetName = "Default FinServe Dataset";
                    this.activeDatasetDescription = "100 mixed-type banking transactions with 20 accounts across Indian branches.";
                }
                break;
        }
        this.loadedAt = System.currentTimeMillis();
    }

    private boolean loadFromBankTransactionsData2Csv() {
        File[] candidates = {
            new File("bank_transactions_data_2.csv"),
            new File("sample_datasets/bank_transactions_data_2.csv"),
            new File("../bank_transactions_data_2.csv"),
            new File(System.getProperty("user.dir"), "bank_transactions_data_2.csv"),
            new File(System.getProperty("user.dir"), "sample_datasets/bank_transactions_data_2.csv")
        };

        File file = null;
        for (File f : candidates) {
            if (f.exists() && f.isFile()) {
                file = f;
                break;
            }
        }

        if (file == null) {
            return false;
        }

        try {
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            parseAndSetBankTransactionsData2(content);
            return true;
        } catch (Exception e) {
            System.err.println("Notice: Could not read bank_transactions_data_2.csv (" + e.getMessage() + "), falling back to default generator.");
            return false;
        }
    }

    private void parseAndSetBankTransactionsData2(String content) {
        String[] lines = content.split("\\r?\\n");
        if (lines.length < 2) return;

        List<Transaction> txns = new ArrayList<>();
        Map<String, Account> accountMap = new LinkedHashMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;
            String[] cols = splitCSVLine(line);
            if (cols.length < 5) continue;

            String txnId = cols.length > 0 ? cols[0].trim() : "TX" + i;
            String accountId = cols.length > 1 ? cols[1].trim() : "AC00000";
            double amount = 0;
            try { amount = Double.parseDouble(cols[2].trim().replaceAll("[^0-9.]", "")); } catch (Exception ignored) {}

            LocalDateTime date = LocalDateTime.now();
            if (cols.length > 3 && !cols[3].trim().isEmpty()) {
                try { date = LocalDateTime.parse(cols[3].trim(), fmt); } catch (Exception ignored) {}
            }

            String typeStr = cols.length > 4 ? cols[4].trim() : "Debit";
            String location = cols.length > 5 ? cols[5].trim() : "New York";
            String deviceId = cols.length > 6 ? cols[6].trim() : "D000000";
            String ip = cols.length > 7 ? cols[7].trim() : "127.0.0.1";
            String merchantId = cols.length > 8 ? cols[8].trim() : "M000";
            String channel = cols.length > 9 ? cols[9].trim() : "Online";
            String age = cols.length > 10 ? cols[10].trim() : "35";
            String occupation = cols.length > 11 ? cols[11].trim() : "Professional";
            int loginAttempts = 1;
            if (cols.length > 13) {
                try { loginAttempts = Integer.parseInt(cols[13].trim()); } catch (Exception ignored) {}
            }
            double balance = 5000.0;
            if (cols.length > 14) {
                try { balance = Double.parseDouble(cols[14].trim().replaceAll("[^0-9.]", "")); } catch (Exception ignored) {}
            }

            // Map channel & type to institutional transaction category
            String mappedType = "WIRE";
            if ("ATM".equalsIgnoreCase(channel)) {
                mappedType = "INTERNAL";
            } else if ("Online".equalsIgnoreCase(channel)) {
                mappedType = amount > 500 ? "WIRE" : "UPI";
            } else if ("Branch".equalsIgnoreCase(channel)) {
                mappedType = amount > 1000 ? "RTGS" : "NEFT";
            }
            if ("Debit".equalsIgnoreCase(typeStr)) {
                mappedType = "CARD";
            }

            // Behavioral fraud classification based on login velocity & anomalies
            String status = "COMPLETED";
            if (loginAttempts >= 3) {
                status = "FLAGGED"; // Anomaly: Repeated credential attempts
            } else if (loginAttempts == 2) {
                status = "PENDING"; // Step-up verification required
            } else if (amount >= 850) {
                status = "FLAGGED"; // High amount outlier
            }

            String desc = occupation + " " + typeStr + " via " + channel + " in " + location + " (Age " + age + ", IP " + ip + ")";
            String paymentCode = deviceId + "-" + merchantId;

            txns.add(new Transaction(txnId, accountId, merchantId, amount, desc, paymentCode, mappedType, status, date, "USD"));

            // Maintain Customer Account with real balance from dataset
            if (!accountMap.containsKey(accountId)) {
                String holder = occupation + " (" + accountId + ", Age " + age + ")";
                accountMap.put(accountId, new Account(accountId, holder, "CHECKING", balance, location + " Branch", "FINS" + accountId.replace("AC", "00")));
            }

            // Maintain Merchant Account
            if (!accountMap.containsKey(merchantId)) {
                String mHolder = "Merchant Entity " + merchantId + " (" + location + ")";
                accountMap.put(merchantId, new Account(merchantId, mHolder, "BUSINESS", 250000.0, location + " Commercial", "FINS" + merchantId.replace("M", "99")));
            }
        }

        // Generate matching compliance & audit dossiers for the 2,512 transactions
        List<FinancialDocument> docs = new ArrayList<>();
        int flaggedCount = 0;
        double totalVolume = 0;
        for (Transaction t : txns) {
            totalVolume += t.getAmount();
            if ("FLAGGED".equals(t.getStatus())) flaggedCount++;
        }

        docs.add(new FinancialDocument("DOC-SURVEIL-01", "Surveillance Audit: Bank Transactions Data 2",
            "Comprehensive behavioral monitoring for 2,512 transactions across " + accountMap.size() +
            " distinct accounts and merchants. Total processed ledger volume: $" + String.format("%.2f", totalVolume) +
            ". Surveillance heuristics identified " + flaggedCount + " transactions exhibiting abnormal login telemetry (>=3 failed attempts) " +
            "or high ticket outliers requiring Enhanced Due Diligence (EDD).",
            "AUDIT_REPORT", "2024-11-04 10:00:00", "Chief Risk Officer"));

        docs.add(new FinancialDocument("DOC-AML-02", "Credential Stuffing & Login Velocity Report",
            "Automated cyber-fraud telemetry analysis. Detected multi-attempt login bursts (up to 5 attempts) from distributed IP ranges " +
            "preceding ATM and Online debit settlements. Mandatory Suspicious Activity Reports (SAR) queued for regulatory dispatch.",
            "COMPLIANCE", "2024-11-04 11:30:00", "Cyber Fraud Unit"));

        docs.add(new FinancialDocument("DOC-SETTLE-03", "Merchant Clearing & Settlement Statement",
            "Inter-institution merchant net settlement analysis for 100 registered merchant endpoints. " +
            "ATM cash logistics, Online card clearing, and branch counter transactions reconciled against federal clearing reserves.",
            "REGULATORY", "2024-11-04 14:00:00", "Clearing Operations Committee"));

        docs.add(new FinancialDocument("DOC-GEO-04", "Geographical Exposure & Counterparty Review",
            "Metropolitan transaction distribution across major urban centers (San Diego, Houston, Mesa, Raleigh, Atlanta, Oklahoma City, Seattle). " +
            "All regional branches maintained liquidity within target operating buffers.",
            "REPORT", "2024-11-04 16:45:00", "Treasury Risk Team"));

        this.transactions = txns;
        this.accounts = new ArrayList<>(accountMap.values());
        this.documents = docs;
        this.activeDatasetId = "default";
        this.activeDatasetName = "Bank Transactions Data 2 (Default)";
        this.activeDatasetDescription = "Primary default dataset with 2,512 real-world transactions across " +
            this.accounts.size() + " accounts/merchants, featuring failed login telemetry, merchant routing, and device IDs.";
    }

    // ----- Custom dataset upload -----
    public synchronized void loadCustomDataset(String name, List<Transaction> txns,
                                                List<Account> accts, List<FinancialDocument> docs) {
        this.transactions = txns != null ? txns : new ArrayList<>();
        // If accounts not provided, synthesize from transaction senders and receivers
        if (accts == null || accts.isEmpty()) {
            this.accounts = synthesizeAccounts(this.transactions);
        } else {
            this.accounts = accts;
        }
        // If documents not provided, synthesize compliance & audit documents referencing this data
        if (docs == null || docs.isEmpty()) {
            this.documents = synthesizeDocuments(name, this.transactions);
        } else {
            this.documents = docs;
        }
        this.activeDatasetId = "custom-" + System.currentTimeMillis();
        this.activeDatasetName = name != null && !name.isEmpty() ? name : "Custom Uploaded Dataset";
        this.activeDatasetDescription = "Uploaded dataset containing " + this.transactions.size() +
            " transactions across " + this.accounts.size() + " accounts, with " + this.documents.size() + " synthesized audit dossiers.";
        this.loadedAt = System.currentTimeMillis();
    }

    private static List<Account> synthesizeAccounts(List<Transaction> txns) {
        Set<String> uniqueIds = new LinkedHashSet<>();
        for (Transaction t : txns) {
            if (t.getSenderAccount() != null && !t.getSenderAccount().isEmpty()) uniqueIds.add(t.getSenderAccount());
            if (t.getReceiverAccount() != null && !t.getReceiverAccount().isEmpty()) uniqueIds.add(t.getReceiverAccount());
        }
        List<Account> list = new ArrayList<>();
        String[] branches = {"Mumbai Corporate Hub", "Bengaluru Tech Core", "New Delhi Financial District", "Hyderabad Cyber Hub", "Chennai Port Branch"};
        String[] types = {"CHECKING", "SAVINGS", "BUSINESS", "INVESTMENT"};
        int i = 0;
        for (String id : uniqueIds) {
            String holder = "Account Holder " + id;
            String type = types[i % types.length];
            String branch = branches[i % branches.length];
            double balance = Math.round((25000 + (i * 18731.45) % 1500000) * 100.0) / 100.0;
            String ifsc = "FINS000" + String.format("%04d", (i % 9000) + 1000);
            list.add(new Account(id, holder, type, balance, branch, ifsc));
            i++;
        }
        return list;
    }

    private static List<FinancialDocument> synthesizeDocuments(String datasetName, List<Transaction> txns) {
        List<FinancialDocument> docs = new ArrayList<>();
        int flaggedCount = 0;
        double totalVolume = 0;
        for (Transaction t : txns) {
            totalVolume += t.getAmount();
            if ("FLAGGED".equalsIgnoreCase(t.getStatus()) || "REJECTED".equalsIgnoreCase(t.getStatus())) flaggedCount++;
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String nowStr = LocalDateTime.now().format(dtf);
        String pastStr = LocalDateTime.now().minusDays(2).format(dtf);

        docs.add(new FinancialDocument("DOC-AUDIT-01", "Audit Dossier: " + datasetName,
            "Internal regulatory examination for " + datasetName + ". Ledger contains " + txns.size() +
            " transactions totaling ₹" + String.format("%.2f", totalVolume) + ". Identified " + flaggedCount +
            " anomalous or flagged transactions requiring anti-money laundering (AML) enhanced due diligence (EDD). " +
            "Compliance verification confirms SWIFT, RTGS, and UPI settlement corridors were reviewed for suspicious structuring.",
            "AUDIT_REPORT", pastStr, "Chief Compliance Officer"));

        docs.add(new FinancialDocument("DOC-AML-02", "AML & Fraud Surveillance Dossier",
            "Automated transaction monitoring alert summary. High-velocity fund movements, structuring below reporting thresholds, " +
            "and shell company accounts were analyzed. High risk indicators flagged " + flaggedCount + " transactions. " +
            "Mandatory Suspicious Transaction Reports (STR) prepared for submission to Financial Intelligence Unit (FIU-IND).",
            "COMPLIANCE", pastStr, "Financial Intelligence Unit"));

        docs.add(new FinancialDocument("DOC-SETTLE-03", "Interbank Liquidity and Clearing Report",
            "Cross-institution net settlement analysis. Clearing capacity across RTGS, NEFT, ACH, and SWIFT channels tested. " +
            "All clearing accounts maintained necessary reserves under RBI prudential liquidity guidelines.",
            "REGULATORY", nowStr, "Treasury Risk Committee"));

        return docs;
    }

    // ----- Accessors (return defensive copies for thread safety) -----
    public synchronized List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public synchronized List<Account> getAccounts() {
        return new ArrayList<>(accounts);
    }

    public synchronized List<FinancialDocument> getDocuments() {
        return new ArrayList<>(documents);
    }

    public synchronized String getActiveDatasetId() { return activeDatasetId; }
    public synchronized String getActiveDatasetName() { return activeDatasetName; }
    public synchronized String getActiveDatasetDescription() { return activeDatasetDescription; }
    public synchronized long getLoadedAt() { return loadedAt; }

    // ----- Summary for API -----
    public synchronized Map<String, Object> getSummary() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("datasetId", activeDatasetId);
        info.put("datasetName", activeDatasetName);
        info.put("description", activeDatasetDescription);
        info.put("totalTransactions", transactions.size());
        info.put("totalAccounts", accounts.size());
        info.put("totalDocuments", documents.size());
        info.put("loadedAt", loadedAt);

        // Compute basic stats
        if (!transactions.isEmpty()) {
            double total = 0, min = Double.MAX_VALUE, max = Double.MIN_VALUE;
            Map<String, Integer> typeCounts = new LinkedHashMap<>();
            Map<String, Integer> statusCounts = new LinkedHashMap<>();
            Map<String, Integer> currencyCounts = new LinkedHashMap<>();

            for (Transaction t : transactions) {
                total += t.getAmount();
                if (t.getAmount() < min) min = t.getAmount();
                if (t.getAmount() > max) max = t.getAmount();
                typeCounts.merge(t.getTransactionType(), 1, Integer::sum);
                statusCounts.merge(t.getStatus(), 1, Integer::sum);
                currencyCounts.merge(t.getCurrency(), 1, Integer::sum);
            }

            Map<String, Object> stats = new LinkedHashMap<>();
            stats.put("totalVolume", Math.round(total * 100.0) / 100.0);
            stats.put("averageAmount", Math.round((total / transactions.size()) * 100.0) / 100.0);
            stats.put("minAmount", Math.round(min * 100.0) / 100.0);
            stats.put("maxAmount", Math.round(max * 100.0) / 100.0);
            stats.put("byType", typeCounts);
            stats.put("byStatus", statusCounts);
            stats.put("byCurrency", currencyCounts);
            info.put("statistics", stats);
        }

        // Sample transactions (first 10)
        List<Map<String, Object>> sample = new ArrayList<>();
        for (int i = 0; i < Math.min(10, transactions.size()); i++) {
            Transaction t = transactions.get(i);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", t.getTransactionId());
            row.put("sender", t.getSenderAccount());
            row.put("receiver", t.getReceiverAccount());
            row.put("amount", t.getAmount());
            row.put("type", t.getTransactionType());
            row.put("status", t.getStatus());
            row.put("currency", t.getCurrency());
            row.put("description", t.getDescription());
            sample.add(row);
        }
        info.put("sampleTransactions", sample);

        return info;
    }

    // ----- Available presets listing -----
    public static List<Map<String, Object>> getAvailablePresets() {
        List<Map<String, Object>> presets = new ArrayList<>();

        Map<String, Object> p1 = new LinkedHashMap<>();
        p1.put("id", "default");
        p1.put("name", "Bank Transactions Data 2 (Default)");
        p1.put("description", "2,512 real-world banking transactions across 595 accounts & merchants with device IDs, login attempts, and debit/credit channels.");
        p1.put("transactions", 2512);
        p1.put("accounts", 595);
        p1.put("documents", 4);
        p1.put("tags", new String[]{"default", "kaggle", "real-data", "2512-txns"});
        presets.add(p1);

        Map<String, Object> p2 = new LinkedHashMap<>();
        p2.put("id", "aml-suspicious");
        p2.put("name", "AML Suspicious Activity Dataset");
        p2.put("description", "250 transactions with high fraud/AML signals — shell companies, structuring, smurfing, offshore layering. Ideal for fraud detection and compliance.");
        p2.put("transactions", 250);
        p2.put("accounts", 40);
        p2.put("documents", 5);
        p2.put("tags", new String[]{"fraud", "AML", "compliance", "suspicious"});
        presets.add(p2);

        Map<String, Object> p3 = new LinkedHashMap<>();
        p3.put("id", "swift-forex");
        p3.put("name", "Global SWIFT & Forex Dataset");
        p3.put("description", "200 cross-border SWIFT/RTGS transactions across 12 currencies with correspondent banking chains. Ideal for settlement flow analysis.");
        p3.put("transactions", 200);
        p3.put("accounts", 30);
        p3.put("documents", 5);
        p3.put("tags", new String[]{"international", "SWIFT", "forex", "cross-border"});
        presets.add(p3);

        Map<String, Object> p4 = new LinkedHashMap<>();
        p4.put("id", "retail-highvol");
        p4.put("name", "Retail High-Volume Dataset");
        p4.put("description", "500 high-frequency retail transactions — UPI, card payments, EMIs, e-commerce. Ideal for parallel analytics and data cleansing.");
        p4.put("transactions", 500);
        p4.put("accounts", 60);
        p4.put("documents", 4);
        p4.put("tags", new String[]{"retail", "UPI", "high-volume", "card"});
        presets.add(p4);

        Map<String, Object> p5 = new LinkedHashMap<>();
        p5.put("id", "synthetic-100");
        p5.put("name", "Synthetic Indian Banking Dataset");
        p5.put("description", "100 mixed-type banking transactions with 20 accounts across Indian branches. Balanced representation of all transaction types.");
        p5.put("transactions", 100);
        p5.put("accounts", 20);
        p5.put("documents", 4);
        p5.put("tags", new String[]{"synthetic", "indian-banking", "sample"});
        presets.add(p5);

        return presets;
    }

    // ----- Normalize column header key for fuzzy matching -----
    private static String norm(String s) {
        if (s == null) return "";
        return s.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    // ----- Parse uploaded CSV into transactions (Kaggle & Banking compatible) -----
    public static List<Transaction> parseCSV(String csv) {
        List<Transaction> result = new ArrayList<>();
        if (csv == null || csv.trim().isEmpty()) return result;

        String[] lines = csv.split("\\r?\\n");
        if (lines.length < 2) return result;

        // First line is header
        String[] headers = splitCSVLine(lines[0]);
        Map<String, Integer> headerMap = new LinkedHashMap<>();
        for (int i = 0; i < headers.length; i++) {
            headerMap.put(norm(headers[i]), i);
        }

        DateTimeFormatter[] fmts = {
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
        };

        for (int row = 1; row < lines.length; row++) {
            String line = lines[row].trim();
            if (line.isEmpty()) continue;

            String[] cols = splitCSVLine(line);
            try {
                // Transaction ID
                String txnId = getCol(cols, headerMap, "transactionid", "id", "txnid", "transactioncode", "transid", "refno");
                if (txnId == null || txnId.isEmpty()) txnId = "TXN-UP-" + String.format("%05d", row);

                // Sender / Customer / Account
                String sender = getCol(cols, headerMap, "senderaccount", "sender", "fromaccount", "customerid", "accountid", "sourceaccount", "custid", "source");
                if (sender == null || sender.isEmpty()) sender = "ACC-EXT-" + ((row * 13) % 97 + 10);

                // Receiver / Beneficiary / Merchant
                String receiver = getCol(cols, headerMap, "receiveraccount", "receiver", "toaccount", "recipientid", "merchantid", "destaccount", "targetaccount", "merchant", "destination");
                if (receiver == null || receiver.isEmpty()) receiver = "ACC-RCV-" + ((row * 29) % 89 + 10);

                // Amount (handles commas and currency symbols)
                double amount = 0;
                String amtStr = getCol(cols, headerMap, "amount", "transactionamount", "txnamount", "value", "amt", "transferamount");
                if (amtStr != null && !amtStr.isEmpty()) {
                    try {
                        amtStr = amtStr.replaceAll("[^0-9.]", "");
                        amount = Double.parseDouble(amtStr);
                    } catch (Exception e) {}
                }
                if (amount == 0) amount = 1000.0 + (row * 379.25) % 250000.0;

                // Description / Narration / Merchant / Location
                String desc = getCol(cols, headerMap, "description", "desc", "memo", "narration", "transactiondetails", "notes", "location", "merchantname");
                if (desc == null || desc.isEmpty()) {
                    String loc = getCol(cols, headerMap, "location", "city", "country");
                    String chan = getCol(cols, headerMap, "channel", "channeltype");
                    desc = "Transaction via " + (chan != null ? chan : "online banking") + (loc != null ? " at " + loc : "");
                }

                // Reference / Code
                String code = getCol(cols, headerMap, "paymentcode", "payment_code", "reference", "ref", "codereference", "authcode");
                if (code == null || code.isEmpty()) code = "REF-2024-" + String.format("%06d", row);

                // Type / Channel
                String type = getCol(cols, headerMap, "transactiontype", "type", "txntype", "channel", "paymentmethod", "method");
                if (type == null || type.isEmpty()) type = "WIRE";
                type = mapTransactionType(type);

                // Status & Fraud detection flag
                String status = getCol(cols, headerMap, "status", "txnstatus");
                String isFraud = getCol(cols, headerMap, "isfraud", "fraud", "fraudulent", "flagged");
                if (isFraud != null && (isFraud.equals("1") || isFraud.equalsIgnoreCase("true") || isFraud.equalsIgnoreCase("yes"))) {
                    status = "FLAGGED";
                } else if (status == null || status.isEmpty()) {
                    status = "COMPLETED";
                }

                // Currency
                String currency = getCol(cols, headerMap, "currency", "ccy");
                if (currency == null || currency.isEmpty()) currency = "INR";

                // Timestamp
                LocalDateTime ts = LocalDateTime.now().minusDays(row % 30).minusMinutes(row * 17);
                String tsStr = getCol(cols, headerMap, "timestamp", "date", "datetime", "txndate", "time");
                if (tsStr != null && !tsStr.isEmpty()) {
                    for (DateTimeFormatter fmt : fmts) {
                        try {
                            if (tsStr.length() == 10) {
                                ts = java.time.LocalDate.parse(tsStr, fmt).atStartOfDay();
                            } else {
                                ts = LocalDateTime.parse(tsStr, fmt);
                            }
                            break;
                        } catch (Exception e) {}
                    }
                }

                result.add(new Transaction(txnId, sender, receiver, amount, desc, code,
                    type.toUpperCase(), status.toUpperCase(), ts, currency.toUpperCase()));
            } catch (Exception e) {
                // Skip malformed rows
            }
        }
        return result;
    }

    private static String mapTransactionType(String raw) {
        String u = raw.toUpperCase();
        if (u.contains("UPI")) return "UPI";
        if (u.contains("SWIFT") || u.contains("CROSS")) return "SWIFT";
        if (u.contains("CARD") || u.contains("POS") || u.contains("DEBIT") || u.contains("CREDIT")) return "CARD";
        if (u.contains("NEFT")) return "NEFT";
        if (u.contains("RTGS")) return "RTGS";
        if (u.contains("ACH") || u.contains("DIRECT DEBIT")) return "ACH";
        if (u.contains("ATM") || u.contains("CASH")) return "INTERNAL";
        if (u.contains("TRANSFER") || u.contains("WIRE") || u.contains("ONLINE")) return "WIRE";
        return "WIRE";
    }

    // ----- Parse uploaded JSON array of transactions -----
    public static List<Transaction> parseJsonTransactions(String json) {
        List<Transaction> result = new ArrayList<>();
        if (json == null || json.trim().isEmpty()) return result;
        json = json.trim();

        if (!json.startsWith("[")) return result;
        json = json.substring(1);
        if (json.endsWith("]")) json = json.substring(0, json.length() - 1);

        int depth = 0;
        int start = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start >= 0) {
                    String obj = json.substring(start, i + 1);
                    Transaction t = parseOneTransaction(obj);
                    if (t != null) result.add(t);
                    start = -1;
                }
            }
        }
        return result;
    }

    private static Transaction parseOneTransaction(String json) {
        try {
            Map<String, String> fields = com.finserve.util.JsonUtil.parseJsonObject(json);

            // Normalize keys in map
            Map<String, String> normMap = new LinkedHashMap<>();
            for (Map.Entry<String, String> e : fields.entrySet()) {
                normMap.put(norm(e.getKey()), e.getValue());
            }

            String id = firstNonNull(normMap, "transactionid", "id", "txnid", "transactioncode");
            if (id == null) id = "TXN-UP-" + System.currentTimeMillis();

            String sender = firstNonNull(normMap, "senderaccount", "sender", "fromaccount", "customerid", "accountid");
            if (sender == null) sender = "ACC-UNKNOWN";

            String receiver = firstNonNull(normMap, "receiveraccount", "receiver", "toaccount", "merchantid", "destaccount");
            if (receiver == null) receiver = "ACC-UNKNOWN";

            double amount = 0;
            String amtStr = firstNonNull(normMap, "amount", "transactionamount", "value", "amt");
            if (amtStr != null) try { amount = Double.parseDouble(amtStr.replaceAll("[^0-9.]", "")); } catch (Exception e) {}

            String desc = firstNonNull(normMap, "description", "desc", "memo", "narration");
            if (desc == null) desc = "Uploaded transaction";

            String code = firstNonNull(normMap, "paymentcode", "payment_code", "reference", "ref");
            if (code == null) code = "REF-" + System.currentTimeMillis() % 10000;

            String type = firstNonNull(normMap, "transactiontype", "type", "txntype", "channel");
            if (type == null) type = "WIRE";
            type = mapTransactionType(type);

            String status = firstNonNull(normMap, "status", "txnstatus");
            String isFraud = firstNonNull(normMap, "isfraud", "fraud");
            if (isFraud != null && (isFraud.equals("1") || isFraud.equalsIgnoreCase("true"))) {
                status = "FLAGGED";
            } else if (status == null) {
                status = "COMPLETED";
            }

            String currency = firstNonNull(normMap, "currency", "ccy");
            if (currency == null) currency = "INR";

            LocalDateTime ts = LocalDateTime.now();
            String tsStr = firstNonNull(normMap, "timestamp", "date", "datetime");
            if (tsStr != null) {
                try { ts = LocalDateTime.parse(tsStr); } catch (Exception e) {}
            }

            return new Transaction(id, sender, receiver, amount, desc, code,
                type.toUpperCase(), status.toUpperCase(), ts, currency.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    // ----- Helpers -----
    private static String firstNonNull(Map<String, String> map, String... normalizedKeys) {
        for (String k : normalizedKeys) {
            String v = map.get(norm(k));
            if (v != null && !v.trim().isEmpty()) return v.trim();
        }
        return null;
    }

    private static String getCol(String[] cols, Map<String, Integer> headerMap, String... keys) {
        for (String k : keys) {
            Integer idx = headerMap.get(norm(k));
            if (idx != null && idx < cols.length) {
                String val = cols[idx].trim().replace("\"", "");
                if (!val.isEmpty()) return val;
            }
        }
        return null;
    }

    private static String[] splitCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString().trim());
        return fields.toArray(new String[0]);
    }
}
