package com.finserve.data;

import com.finserve.model.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * DatasetPresets — Generates three specialized banking datasets
 * modeled after real Kaggle financial fraud datasets.
 *
 * Schema inspired by:
 *   - Kaggle "Bank Transaction Dataset for Fraud Detection"
 *     (TransactionID, AccountID, TransactionAmount, TransactionType,
 *      Location, Channel, MerchantID, etc.)
 *
 * Each preset emphasizes a different analytical domain:
 *   1. AML Suspicious Activity  — high fraud signals for M1/M5
 *   2. Global SWIFT & Forex     — cross-border flows for M2/M4
 *   3. Retail High-Volume       — mass retail for M3/M6
 */
public class DatasetPresets {

    // ========================================================================
    //  PRESET 1: AML SUSPICIOUS ACTIVITY
    // ========================================================================

    private static final String[] AML_DESCRIPTIONS = {
        "Wire transfer to shell company Oceanic Holdings Ltd offshore account",
        "Multiple rapid cash deposits below INR 10 lakh reporting threshold",
        "Structured deposits across 5 branches within 2 hours window",
        "International wire to sanctioned jurisdiction entity Cayman Islands",
        "Round-tripping funds via Dubai hawala intermediary channel",
        "Layering transaction through 3 shell entities registered in BVI",
        "Cash withdrawal exceeding daily limit with emergency override code",
        "Suspicious bulk transfer to newly opened dormant account ACC0089",
        "Trade-based money laundering via over-invoiced import goods",
        "Smurfing pattern detected - 12 deposits under threshold in 24hrs",
        "Funds routed through cryptocurrency exchange P2P wallet",
        "Wire transfer to PEP-linked account requiring enhanced due diligence",
        "Cross-border payment to high-risk jurisdiction flagged by FATF",
        "Circular transaction pattern between related party accounts detected",
        "Unusual spike in transaction volume - 400% increase from baseline",
        "Dormant account reactivated with large inbound international wire",
        "Cash-intensive business deposit pattern inconsistent with declared revenue",
        "Third-party transfer to unrelated beneficiary without documentation",
        "Rapid movement of funds through multiple intermediary accounts",
        "Suspicious refund pattern - goods returned but funds redirected",
        "Salary credit from TechCorp Solutions Pvt Ltd monthly payroll",
        "Regular EMI payment for home loan account HDFC HFL approved",
        "Insurance premium debit for LIC Jeevan Anand policy renewal",
        "Mutual fund SIP investment in Axis Bluechip Fund via NACH mandate",
        "Utility bill payment to BESCOM electricity board autopay",
        "Credit card bill settlement for ICICI Platinum card cycle",
        "Vendor payment to Amazon India marketplace seller settlement",
        "Government subsidy credit under PM-KISAN scheme direct benefit",
    };

    private static final String[] AML_PAYMENT_CODES = {
        "SWIFT-BOFAUS3N", "SWIFT-HDFCINBB", "SWIFT-SBININBB",
        "HAW-DXB-001", "HAW-DXB-002", "SHELL-BVI-991",
        "SHELL-CYM-442", "SHELL-PNM-331", "NEFT-REF-88901",
        "RTGS-REF-77234", "UPI-REF-SUSPI01", "CRYPTO-P2P-XYZ",
        "TXN-WIRE-FLAGGED", "TXN-ACH-STRUCT", "PAY-2024-AML01",
        "PAY-2024-AML02", "IFSC-HDFC0001234", "IFSC-SBIN0005432",
        "RTN-021000021", "PAY-SAL-OCT24", "PAY-INV-2024-Q3",
    };

    private static final String[] AML_LOCATIONS = {
        "Mumbai Nariman Point", "Delhi Connaught Place", "Dubai DIFC",
        "London Canary Wharf", "Singapore Marina Bay", "Cayman Islands George Town",
        "BVI Road Town", "Bangalore Koramangala", "Chennai T-Nagar",
        "Kolkata Park Street", "Panama City", "Zurich Bahnhofstrasse",
        "Hong Kong Central", "Hyderabad HITEC City", "Pune Hinjewadi",
    };

    public static List<Transaction> generateAMLTransactions(int count) {
        Random rng = new Random(101);
        List<Transaction> txns = new ArrayList<>();

        String[] types = {"WIRE", "SWIFT", "RTGS", "NEFT", "INTERNAL", "ACH", "CARD", "UPI"};
        String[] statuses = {"COMPLETED", "FLAGGED", "FLAGGED", "FLAGGED", "PENDING", "REJECTED", "COMPLETED", "COMPLETED"};
        String[] currencies = {"INR", "USD", "EUR", "GBP", "AED", "SGD", "CHF"};
        String[] channels = {"Online", "Branch", "ATM", "Mobile", "Wire Room"};

        for (int i = 0; i < count; i++) {
            String txnId = String.format("AML-TXN%06d", i + 1);
            String sender = String.format("ACC%04d", rng.nextInt(80) + 1);
            String receiver = String.format("ACC%04d", rng.nextInt(80) + 1);
            while (receiver.equals(sender)) receiver = String.format("ACC%04d", rng.nextInt(80) + 1);

            // Higher amounts for suspicious transactions
            double amount;
            if (i % 3 == 0) {
                // Structured deposits just below threshold
                amount = Math.round((800000 + rng.nextDouble() * 190000) * 100.0) / 100.0;
            } else if (i % 5 == 0) {
                // Very large suspicious transfers
                amount = Math.round((5000000 + rng.nextDouble() * 45000000) * 100.0) / 100.0;
            } else {
                amount = Math.round((1000 + rng.nextDouble() * 500000) * 100.0) / 100.0;
            }

            String desc = AML_DESCRIPTIONS[rng.nextInt(AML_DESCRIPTIONS.length)];
            String code = AML_PAYMENT_CODES[rng.nextInt(AML_PAYMENT_CODES.length)];
            String type = types[rng.nextInt(types.length)];
            String status = statuses[rng.nextInt(statuses.length)];
            String currency = currencies[rng.nextInt(currencies.length)];
            LocalDateTime ts = LocalDateTime.of(2024, 1 + rng.nextInt(12),
                1 + rng.nextInt(28), rng.nextInt(24), rng.nextInt(60));

            txns.add(new Transaction(txnId, sender, receiver, amount, desc, code, type, status, ts, currency));
        }
        return txns;
    }

    public static List<Account> generateAMLAccounts(int count) {
        Random rng = new Random(101);
        String[] names = {
            "Rajesh Khanna", "Priya Offshore Holdings", "Shell Corp BVI-01",
            "Oceanic Trading LLC", "Vikram Malhotra", "Sunita Agarwal",
            "Global Exports FZE Dubai", "Midnight Ventures Cayman",
            "Arjun Patel", "Meera Investments Pvt Ltd", "Karthik Rao",
            "Phantom Consulting Panama", "Neha Sharma", "Dhruv Capital LP",
            "Riya Enterprises", "Secure Finance Hong Kong Ltd",
            "Amit Kumar", "Deepa Nair", "Sanjay Gupta", "Kavya Menon",
            "Rohit Verma", "Ananya Reddy", "Varun Choudhary", "Shreya Bhat",
            "Aditya Singh", "Ishita Kapoor", "Siddharth Das", "Pooja Mehta",
            "Nisha Jain", "Rakesh Yadav", "Summit Holdings Ltd", "Apex Global Trading",
            "Delta Financial Services", "Emerald Offshore Corp", "Phoenix Realty Trust",
            "Zenith Investments LLC", "Atlas Capital Partners", "Horizon Wealth Management",
            "Sterling Banking Associates", "Pinnacle Finance Group",
        };
        String[] types = {"SAVINGS", "CHECKING", "BUSINESS", "INVESTMENT", "OFFSHORE", "SHELL"};
        String[] branches = {
            "Mumbai Nariman Point", "Delhi CP", "Dubai DIFC", "London Canary Wharf",
            "Cayman Islands", "BVI Road Town", "Bangalore Koramangala", "Singapore",
        };

        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String accId = String.format("ACC%04d", i + 1);
            String name = names[i % names.length];
            String type = types[rng.nextInt(types.length)];
            double balance = Math.round((10000 + rng.nextDouble() * 50000000) * 100.0) / 100.0;
            String branch = branches[rng.nextInt(branches.length)];
            String ifsc = String.format("FNSV%07d", rng.nextInt(10000000));
            accounts.add(new Account(accId, name, type, balance, branch, ifsc));
        }
        return accounts;
    }

    public static List<FinancialDocument> generateAMLDocuments() {
        List<FinancialDocument> docs = new ArrayList<>();

        docs.add(new FinancialDocument("AML-DOC001",
            "Suspicious Activity Report (SAR) - Q4 2024",
            "Filed pursuant to PMLA 2002 Section 12. Investigation identified 23 accounts " +
            "exhibiting structured deposit patterns below INR 10 lakh threshold. Primary subjects: " +
            "ACC0012 (Oceanic Trading LLC), ACC0034 (Shell Corp BVI-01), ACC0045 (Phantom Consulting Panama). " +
            "Transaction velocity analysis reveals 156 transfers within 48-hour window across " +
            "5 branches. Funds traced through hawala channels in Dubai DIFC to ultimate beneficiary " +
            "in Cayman Islands. Total suspicious volume: INR 45.7 crore. Recommended freeze of " +
            "ACC0012 ACC0034 ACC0045 pending ED investigation. Cross-referenced with FATF high-risk " +
            "jurisdiction list - 12 transactions to sanctioned entities identified.",
            "SAR", "2024-12-20", "AML Compliance Unit"));

        docs.add(new FinancialDocument("AML-DOC002",
            "Enhanced Due Diligence Report - PEP Accounts",
            "Quarterly review of 8 Politically Exposed Person (PEP) linked accounts. " +
            "3 accounts flagged for unusual activity: ACC0023 received INR 12 crore from " +
            "offshore entity with no clear business rationale. ACC0041 shows round-tripping " +
            "pattern - funds sent to Dubai, returned via Singapore within 72 hours. " +
            "ACC0056 dormant for 18 months, reactivated with 3 large international wires " +
            "totaling USD 2.1 million. KYC documents for ACC0041 show discrepancies in " +
            "declared income vs transaction volume. Escalated to Financial Intelligence Unit (FIU-IND).",
            "EDD", "2024-11-15", "KYC & Due Diligence Team"));

        docs.add(new FinancialDocument("AML-DOC003",
            "Trade-Based Money Laundering Investigation Summary",
            "Investigation into 4 import-export firms linked to over-invoicing scheme. " +
            "Customs data cross-referenced with banking records reveals systematic inflation " +
            "of import values by 300-500%. Firms involved: Global Exports FZE (Dubai), " +
            "Emerald Offshore Corp (BVI), Atlas Capital Partners (Panama). Total laundered " +
            "amount estimated at INR 234 crore over 18-month period. Wire transfers routed " +
            "through correspondent banking chain: HDFC -> SWIFT intermediary -> Barclays London -> " +
            "First Caribbean Bank -> Ultimate beneficiary Cayman trust account.",
            "INVESTIGATION", "2024-10-30", "Financial Crime Investigation Cell"));

        docs.add(new FinancialDocument("AML-DOC004",
            "Annual AML Risk Assessment - 2024",
            "Comprehensive risk evaluation across all banking channels. Key findings: " +
            "Online channel fraud attempts increased 67% YoY. Mobile banking shows 23 cases of " +
            "account takeover via SIM swap. Branch cash transactions flagged 45 structuring attempts. " +
            "Geographic risk: 78% of suspicious cross-border flows involve UAE, Singapore, and " +
            "offshore Caribbean jurisdictions. Customer segment risk: shell company accounts represent " +
            "2% of total accounts but 34% of flagged transactions. Recommended enhancements: " +
            "real-time transaction monitoring, enhanced velocity checks, blockchain analytics integration.",
            "RISK_ASSESSMENT", "2024-12-31", "Chief Risk Officer"));

        docs.add(new FinancialDocument("AML-DOC005",
            "Regulatory Examination Response - RBI Inspection 2024",
            "Response to Reserve Bank of India inspection observations dated 2024-09-15. " +
            "Observation 1: Inadequate monitoring of correspondent banking relationships - RESOLVED " +
            "with implementation of Wolfsberg questionnaire for all nostro/vostro accounts. " +
            "Observation 2: Delayed STR filing for 7 cases exceeding 30-day deadline - ROOT CAUSE " +
            "identified as manual review bottleneck, automated alert triage system deployed. " +
            "Observation 3: Missing beneficial ownership documentation for 12 corporate accounts - " +
            "KYC refresh completed for all identified accounts. Penalty of INR 2.5 crore levied " +
            "and paid. Corrective action plan submitted to RBI compliance department.",
            "REGULATORY", "2024-11-01", "Chief Compliance Officer"));

        return docs;
    }

    // ========================================================================
    //  PRESET 2: GLOBAL SWIFT & FOREX
    // ========================================================================

    private static final String[] SWIFT_DESCRIPTIONS = {
        "SWIFT MT103 single customer credit transfer to Deutsche Bank Frankfurt",
        "Correspondent banking nostro settlement via Citibank New York",
        "Foreign exchange conversion USD/INR at spot rate 83.42 treasury desk",
        "Letter of Credit payment for bulk commodity import from Japan",
        "Trade finance documentary collection shipment Bangkok to Mumbai",
        "Cross-currency swap settlement EUR/GBP with Barclays London",
        "RTGS high-value interbank transfer to SBI clearing account",
        "Remittance inward from NRI account Wells Fargo San Francisco",
        "Export proceeds realization against shipping bill SB-2024-78901",
        "SWIFT MT202 bank-to-bank transfer to Standard Chartered Singapore",
        "Foreign currency demand draft issuance GBP for university fees UK",
        "Vostro account credit from Sumitomo Mitsui Banking Tokyo",
        "SWIFT MT760 standby LC issuance for infrastructure project guarantee",
        "Pre-shipment credit disbursement in JPY for auto parts exporter",
        "Post-shipment credit against accepted bill of exchange 90 days",
        "FCNR deposit maturity proceeds conversion JPY to INR at forward rate",
        "Buyer's credit disbursement for capital goods import from Germany",
        "EEFC account credit USD export realization Tata Consultancy Services",
        "Forward contract settlement EUR/INR 6-month tenor treasury operations",
        "SWIFT MT940 statement request from correspondent bank BNP Paribas Paris",
    };

    private static final String[] SWIFT_CODES = {
        "SWIFT-DEUTDEFF", "SWIFT-CITIUS33", "SWIFT-BARCGB2L",
        "SWIFT-SCBLSGSG", "SWIFT-SMBCJPJT", "SWIFT-BNPAFRPP",
        "SWIFT-CHASUS33", "SWIFT-HSBCHKHH", "SWIFT-ANZBAU3M",
        "SWIFT-UBSWCHZH", "SWIFT-HDFCINBB", "SWIFT-SBININBB",
        "SWIFT-ABORAEAA", "SWIFT-WFBIUS6S", "RTGS-REF-INT01",
        "RTGS-REF-INT02", "NOSTRO-SETTLE-01", "VOSTRO-CR-01",
        "LC-PAY-2024-001", "LC-PAY-2024-002", "FCNR-MAT-001",
    };

    public static List<Transaction> generateSWIFTForexTransactions(int count) {
        Random rng = new Random(202);
        List<Transaction> txns = new ArrayList<>();

        String[] types = {"SWIFT", "SWIFT", "RTGS", "RTGS", "WIRE", "NEFT", "NOSTRO", "LC"};
        String[] statuses = {"COMPLETED", "COMPLETED", "COMPLETED", "PENDING", "PROCESSING", "COMPLETED"};
        String[] currencies = {"USD", "EUR", "GBP", "JPY", "SGD", "AUD", "CHF", "AED", "HKD", "CAD", "INR", "CNY"};

        for (int i = 0; i < count; i++) {
            String txnId = String.format("FX-TXN%06d", i + 1);
            String sender = String.format("NOSTRO-%03d", rng.nextInt(30) + 1);
            String receiver = String.format("VOSTRO-%03d", rng.nextInt(30) + 1);

            // International transactions tend to be larger
            double amount = Math.round((50000 + rng.nextDouble() * 50000000) * 100.0) / 100.0;

            String desc = SWIFT_DESCRIPTIONS[rng.nextInt(SWIFT_DESCRIPTIONS.length)];
            String code = SWIFT_CODES[rng.nextInt(SWIFT_CODES.length)];
            String type = types[rng.nextInt(types.length)];
            String status = statuses[rng.nextInt(statuses.length)];
            String currency = currencies[rng.nextInt(currencies.length)];
            LocalDateTime ts = LocalDateTime.of(2024, 1 + rng.nextInt(12),
                1 + rng.nextInt(28), rng.nextInt(24), rng.nextInt(60));

            txns.add(new Transaction(txnId, sender, receiver, amount, desc, code, type, status, ts, currency));
        }
        return txns;
    }

    public static List<Account> generateSWIFTAccounts(int count) {
        Random rng = new Random(202);
        String[] names = {
            "HDFC Bank Nostro USD", "SBI Nostro EUR", "ICICI Nostro GBP",
            "Axis Bank Nostro JPY", "Kotak Nostro SGD", "Yes Bank Nostro AUD",
            "Deutsche Bank Vostro INR", "Citibank Vostro INR", "Barclays Vostro INR",
            "Standard Chartered Vostro INR", "HSBC Vostro INR", "BNP Paribas Vostro INR",
            "JP Morgan Correspondent", "Wells Fargo Correspondent", "ANZ Correspondent",
            "UBS Treasury Desk", "Sumitomo Mitsui Trade Finance",
            "Tata Exports EEFC Account", "Reliance Industries EEFC Account",
            "Infosys Ltd EEFC Account", "Wipro Technologies EEFC Account",
            "Mahindra International Trade", "Bajaj Auto Export Division",
            "ONGC Videsh Ltd", "Indian Oil Corporation Treasury",
            "State Bank of India London Branch", "Bank of Baroda New York",
            "Punjab National Bank Hong Kong", "Canara Bank Dubai",
            "Union Bank Frankfurt Representative",
        };
        String[] types = {"NOSTRO", "VOSTRO", "EEFC", "FCNR", "CORRESPONDENT", "TREASURY"};
        String[] branches = {
            "Mumbai Treasury", "New York Branch", "London Branch",
            "Singapore Branch", "Tokyo Representative", "Frankfurt Branch",
            "Dubai DIFC", "Hong Kong Branch",
        };

        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String accId = i < 15 ? String.format("NOSTRO-%03d", i + 1) : String.format("VOSTRO-%03d", i - 14);
            String name = names[i % names.length];
            String type = types[rng.nextInt(types.length)];
            double balance = Math.round((1000000 + rng.nextDouble() * 500000000) * 100.0) / 100.0;
            String branch = branches[rng.nextInt(branches.length)];
            String ifsc = String.format("INTL%07d", rng.nextInt(10000000));
            accounts.add(new Account(accId, name, type, balance, branch, ifsc));
        }
        return accounts;
    }

    public static List<FinancialDocument> generateSWIFTDocuments() {
        List<FinancialDocument> docs = new ArrayList<>();

        docs.add(new FinancialDocument("FX-DOC001",
            "Daily Treasury Position Report - Multi-Currency",
            "End-of-day position report across 12 currency pairs. Net USD position: long $45.2M " +
            "against authorized limit of $60M. EUR/INR forward book: 23 outstanding contracts " +
            "with notional value EUR 89M, MTM gain INR 12.3 crore. JPY exposure reduced by 40% " +
            "following BOJ rate decision. GBP/INR swap spread widened 15bps - treasury desk " +
            "recommends hedging 60% of Q1 2025 expected GBP receivables. Nostro account reconciliation " +
            "completed for 8 correspondent banks. 3 unmatched items in Citibank NY nostro pending " +
            "SWIFT MT950 investigation. Total interbank settlement volume: INR 2,345 crore.",
            "TREASURY", "2024-12-15", "Treasury Operations Desk"));

        docs.add(new FinancialDocument("FX-DOC002",
            "SWIFT Message Analytics Report - Q4 2024",
            "Total SWIFT messages processed: 12,456 (MT103: 8,234; MT202: 2,890; MT760: 456; " +
            "MT940: 876). Average processing time: 2.3 seconds for straight-through-processing (STP). " +
            "STP rate improved to 94.2% from 89.7% in Q3. Repair queue: 723 messages required " +
            "manual intervention primarily due to beneficiary name mismatch (45%) and missing " +
            "IBAN validation (32%). Top 5 correspondent banks by volume: Citibank NY (2,345), " +
            "Deutsche Bank Frankfurt (1,890), Barclays London (1,567), Standard Chartered SG (1,234), " +
            "HSBC Hong Kong (987). Cross-border payment tracking (gpi) adoption: 78% of MT103.",
            "OPERATIONS", "2024-12-31", "SWIFT Operations Center"));

        docs.add(new FinancialDocument("FX-DOC003",
            "Trade Finance Portfolio Summary - Annual Review",
            "Outstanding Letter of Credit portfolio: INR 3,456 crore across 234 active LCs. " +
            "Sector distribution: Auto components (34%), Pharma API imports (28%), Electronics (21%), " +
            "Commodity trading (17%). Average LC tenor: 120 days. Pre-shipment credit outstanding: " +
            "INR 890 crore. Post-shipment credit: INR 567 crore. Buyer's credit from overseas: " +
            "USD 234M at average SOFR+150bps. Documentary collection: 456 bills handled with " +
            "98.7% acceptance rate. Default rate: 0.3% well within risk appetite of 1.5%.",
            "TRADE_FINANCE", "2024-12-28", "Trade Finance Department"));

        docs.add(new FinancialDocument("FX-DOC004",
            "Foreign Exchange Risk Management Policy Update 2025",
            "Updated FX risk limits effective January 2025. Aggregate open position limit: " +
            "USD 100M equivalent (previously USD 75M). Individual currency pair limits: " +
            "USD/INR: $50M, EUR/INR: EUR 30M, GBP/INR: GBP 20M. Overnight gap limit reduced " +
            "to $25M from $30M following increased volatility. VaR limit: INR 15 crore at 99% " +
            "confidence 1-day horizon. Stop-loss trigger: INR 8 crore cumulative monthly. " +
            "New hedge accounting requirements under Ind-AS 109 implemented for all qualifying " +
            "hedges. Board-approved hedging strategy: minimum 60% of projected 12-month exposures.",
            "POLICY", "2024-12-20", "Chief Risk Officer"));

        docs.add(new FinancialDocument("FX-DOC005",
            "Correspondent Banking Due Diligence Annual Review",
            "Annual review of 45 correspondent banking relationships completed. KYC/AML due " +
            "diligence refreshed for all banks. Wolfsberg questionnaire responses received from " +
            "42 banks (93.3%). 3 pending: Bank of China, Mashreq Bank UAE, First Caribbean. " +
            "Risk ratings: Low (28), Medium (14), High (3). High-risk correspondents: " +
            "Bank in Belarus (sanctioned jurisdiction), Nigerian bank (FATF grey list), " +
            "Mauritius offshore bank (beneficial ownership concerns). Recommended actions: " +
            "terminate Belarus relationship, enhanced monitoring for Nigeria, request additional " +
            "documentation from Mauritius bank within 30 days.",
            "COMPLIANCE", "2024-11-30", "Correspondent Banking Unit"));

        return docs;
    }

    // ========================================================================
    //  PRESET 3: RETAIL HIGH-VOLUME
    // ========================================================================

    private static final String[] RETAIL_DESCRIPTIONS = {
        "UPI payment to Swiggy food delivery order #SW789012",
        "Amazon India online purchase electronics wireless earbuds",
        "Flipkart sale purchase Redmi smartphone EMI no-cost",
        "Zomato Gold restaurant dining bill payment QR scan",
        "PhonePe UPI transfer to friend birthday gift payment",
        "Google Pay merchant payment Big Bazaar grocery store",
        "Paytm wallet recharge mobile prepaid Jio Rs.599 plan",
        "Netflix India subscription monthly auto-debit card payment",
        "Spotify Premium India annual subscription renewal",
        "Ola ride payment Bangalore airport to Koramangala",
        "Uber ride payment Delhi airport to Connaught Place",
        "IRCTC railway ticket booking Rajdhani Express PNR confirmed",
        "MakeMyTrip flight booking Mumbai to Delhi GoIndiGo 6E-2345",
        "Zerodha demat account brokerage charge equity delivery",
        "Groww mutual fund SIP HDFC Mid-Cap Opportunities monthly",
        "CRED credit card bill payment cashback reward earned",
        "Bajaj Finserv EMI payment consumer durable loan installment",
        "HDFC home loan EMI monthly installment auto-debit NACH",
        "SBI car loan EMI monthly installment standing instruction",
        "LIC premium payment Jeevan Anand policy annual renewal",
        "BESCOM electricity bill payment Bangalore online portal",
        "BWSSB water bill payment quarterly Bangalore corporation",
        "Airtel postpaid mobile bill monthly auto-debit mandate",
        "JioFiber broadband monthly subscription auto-renewal",
        "Property tax payment BBMP Bangalore annual assessment",
        "School fees payment DPS Bangalore Q3 installment",
        "Petrol pump fuel payment Indian Oil HDFC credit card",
        "DMart grocery store weekly household shopping bill",
        "BigBasket online grocery delivery weekly subscription",
        "Reliance Trends clothing purchase festival season sale",
        "Myntra fashion purchase End of Reason Sale EORS",
        "Decathlon sports equipment purchase running shoes",
        "Apollo Pharmacy medicine purchase monthly prescription refill",
        "Practo doctor consultation fee online telemedicine visit",
        "Razorpay payment gateway subscription SaaS product annual",
    };

    private static final String[] RETAIL_CODES = {
        "UPI-REF-456789", "UPI-REF-789012", "UPI-REF-012345",
        "UPI-REF-345678", "UPI-REF-678901", "UPI-REF-901234",
        "IMPS-REF-112233", "IMPS-REF-445566", "IMPS-REF-778899",
        "NEFT-REF-111222", "NACH-REF-333444", "NACH-REF-555666",
        "PAY-EMI-2024-01", "PAY-EMI-2024-02", "PAY-EMI-2024-03",
        "CARD-TXN-001", "CARD-TXN-002", "CARD-TXN-003",
        "CARD-TXN-004", "CARD-TXN-005", "CARD-TXN-006",
    };

    public static List<Transaction> generateRetailHighVolTransactions(int count) {
        Random rng = new Random(303);
        List<Transaction> txns = new ArrayList<>();

        String[] types = {"UPI", "UPI", "UPI", "CARD", "CARD", "NEFT", "IMPS", "NACH", "DEBIT_CARD"};
        String[] statuses = {"COMPLETED", "COMPLETED", "COMPLETED", "COMPLETED", "COMPLETED",
                             "PENDING", "DECLINED", "COMPLETED", "COMPLETED", "REFUNDED"};
        String[] currencies = {"INR"};  // Domestic retail is all INR

        for (int i = 0; i < count; i++) {
            String txnId = String.format("RTL-TXN%06d", i + 1);
            String sender = String.format("SAV%05d", rng.nextInt(200) + 1);
            String receiver = String.format("MER%04d", rng.nextInt(100) + 1);

            // Retail amounts are typically small
            double amount;
            if (i % 20 == 0) {
                // Occasional EMI / large purchase
                amount = Math.round((10000 + rng.nextDouble() * 90000) * 100.0) / 100.0;
            } else if (i % 7 == 0) {
                // Medium purchases
                amount = Math.round((1000 + rng.nextDouble() * 9000) * 100.0) / 100.0;
            } else {
                // Small daily transactions
                amount = Math.round((50 + rng.nextDouble() * 2000) * 100.0) / 100.0;
            }

            String desc = RETAIL_DESCRIPTIONS[rng.nextInt(RETAIL_DESCRIPTIONS.length)];
            String code = RETAIL_CODES[rng.nextInt(RETAIL_CODES.length)];
            String type = types[rng.nextInt(types.length)];
            String status = statuses[rng.nextInt(statuses.length)];
            String currency = currencies[0];

            // Higher transaction density - multiple per day
            int month = 1 + rng.nextInt(12);
            int day = 1 + rng.nextInt(28);
            int hour = 7 + rng.nextInt(16); // 7am to 11pm
            int minute = rng.nextInt(60);
            LocalDateTime ts = LocalDateTime.of(2024, month, day, hour, minute);

            txns.add(new Transaction(txnId, sender, receiver, amount, desc, code, type, status, ts, currency));
        }
        return txns;
    }

    public static List<Account> generateRetailAccounts(int count) {
        Random rng = new Random(303);
        String[] names = {
            "Aarav Patel", "Priya Sharma", "Rahul Verma", "Sneha Gupta", "Vikram Singh",
            "Ananya Reddy", "Arjun Nair", "Meera Iyer", "Rohan Joshi", "Kavya Menon",
            "Aditya Kumar", "Ishita Kapoor", "Siddharth Das", "Pooja Mehta", "Karthik Rao",
            "Neha Agarwal", "Dhruv Malhotra", "Riya Banerjee", "Varun Choudhary", "Shreya Bhat",
            "Tushar Pandey", "Divya Saxena", "Manish Tiwari", "Swati Mishra", "Nikhil Bhatt",
            "Kriti Sanon", "Harsh Vardhan", "Simran Kaur", "Rajat Chauhan", "Tanvi Deshmukh",
            "Vivek Srinivasan", "Nandini Pillai", "Gaurav Thakur", "Anushka Sen", "Kartik Arora",
            "Bhavya Patel", "Rishabh Jain", "Megha Kulkarni", "Sahil Malhotra", "Tanya Oberoi",
            "Yash Agrawal", "Pallavi Hegde", "Akash Dubey", "Shruti Rajan", "Mohit Sehgal",
            "Ritika Khanna", "Pranav Goswami", "Aditi Choudhary", "Suresh Iyer", "Geeta Nambiar",
            "Chirag Patel", "Lakshmi Subramanian", "Nitin Gupta", "Deepika Nair", "Vishal Rawat",
            "Anjali Kapoor", "Raghav Menon", "Prachi Kulkarni", "Arun Krishnan", "Saumya Tripathi",
        };
        String[] types = {"SAVINGS", "SAVINGS", "SAVINGS", "CHECKING", "SALARY"};
        String[] branches = {
            "Mumbai Andheri", "Mumbai Bandra", "Delhi Saket", "Delhi Rohini",
            "Bangalore HSR Layout", "Bangalore Whitefield", "Pune Kothrud",
            "Hyderabad Gachibowli", "Chennai Adyar", "Kolkata Salt Lake",
            "Ahmedabad Navrangpura", "Jaipur Malviya Nagar",
        };

        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String accId = String.format("SAV%05d", i + 1);
            String name = names[i % names.length];
            String type = types[rng.nextInt(types.length)];
            double balance = Math.round((5000 + rng.nextDouble() * 500000) * 100.0) / 100.0;
            String branch = branches[rng.nextInt(branches.length)];
            String ifsc = String.format("HDFC%07d", rng.nextInt(10000000));
            accounts.add(new Account(accId, name, type, balance, branch, ifsc));
        }
        return accounts;
    }

    public static List<FinancialDocument> generateRetailDocuments() {
        List<FinancialDocument> docs = new ArrayList<>();

        docs.add(new FinancialDocument("RTL-DOC001",
            "UPI Transaction Analytics Report - Monthly Summary",
            "Total UPI transactions processed: 45,678 with combined value INR 23.4 crore. " +
            "Average transaction value: INR 512. Peak hour: 1PM-2PM with 3,456 transactions. " +
            "Top merchants by volume: Swiggy (12%), Amazon (10%), Flipkart (8%), Zomato (7%), " +
            "Big Bazaar (5%). Failed transaction rate: 2.3% primarily due to bank server timeout (67%) " +
            "and insufficient balance (28%). P2P transfers constitute 35% of total UPI volume. " +
            "QR code scan payments growing at 45% MoM. UPI Autopay mandates: 12,345 active.",
            "ANALYTICS", "2024-12-31", "Digital Banking Division"));

        docs.add(new FinancialDocument("RTL-DOC002",
            "Credit Card Portfolio Health Dashboard - Q4 2024",
            "Active credit cards: 34,567. Total outstanding: INR 234 crore. Average utilization: 45%. " +
            "EMI conversions: 8,901 transactions converted to EMI (avg tenor 9 months). " +
            "Delinquency bucket analysis: Current (89%), 1-30 DPD (6%), 31-60 DPD (3%), " +
            "61-90 DPD (1.5%), 90+ DPD (0.5%). Top spending categories: Online shopping (34%), " +
            "Food & dining (18%), Travel (12%), Fuel (10%), Groceries (8%). Reward points " +
            "redeemed: 45M points worth INR 1.2 crore. Fraud chargebacks: 234 cases (0.07% rate).",
            "DASHBOARD", "2024-12-28", "Retail Cards Division"));

        docs.add(new FinancialDocument("RTL-DOC003",
            "Customer Spending Behavior Analysis - Festival Season",
            "Diwali season (Oct 15 - Nov 15) transaction analysis. Total volume: INR 456 crore " +
            "representing 3.2x increase over normal month. Category breakdown: Electronics (28%), " +
            "Fashion & apparel (22%), Home decor (15%), Jewellery (12%), Gifts (10%). " +
            "EMI adoption rate: 45% of purchases above INR 5,000 converted to no-cost EMI. " +
            "New customer acquisition: 2,345 first-time digital payment users. Average basket " +
            "size increased from INR 1,200 to INR 3,400. Cashback and rewards drove 23% of " +
            "incremental spending. Regional analysis: Tier-2 cities showed 67% higher growth rate.",
            "INSIGHTS", "2024-11-30", "Consumer Insights Team"));

        docs.add(new FinancialDocument("RTL-DOC004",
            "Digital Payment Disputes & Chargeback Summary",
            "Total disputes filed: 1,234 in Q4 2024. Resolution rate: 92% within SLA of 7 days. " +
            "Category: Unauthorized transaction (34%), Merchant not providing service (28%), " +
            "Duplicate charge (18%), Amount mismatch (12%), Others (8%). Refund amount: INR 2.3 crore. " +
            "Chargeback ratio: 0.04% well within card network threshold of 0.1%. Auto-resolution " +
            "by AI system: 45% of cases resolved without manual intervention. Average resolution " +
            "time reduced from 5.2 days to 3.1 days after ML model deployment.",
            "OPERATIONS", "2024-12-20", "Customer Service Operations"));

        return docs;
    }
}
