package com.finserve.data;

import com.finserve.model.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Generates realistic sample financial data for demonstrating all DSA modules.
 */
public class SampleDataGenerator {

    private static final Random random = new Random(42);

    private static final String[] NAMES = {
        "Aarav Patel", "Priya Sharma", "Rahul Verma", "Sneha Gupta", "Vikram Singh",
        "Ananya Reddy", "Arjun Nair", "Meera Iyer", "Rohan Joshi", "Kavya Menon",
        "Aditya Kumar", "Ishita Kapoor", "Siddharth Das", "Pooja Mehta", "Karthik Rao",
        "Neha Agarwal", "Dhruv Malhotra", "Riya Banerjee", "Varun Choudhary", "Shreya Bhat"
    };

    private static final String[] BRANCHES = {
        "Mumbai Central", "Delhi Connaught Place", "Bangalore Koramangala",
        "Chennai T-Nagar", "Pune Hinjewadi", "Hyderabad HITEC City",
        "Kolkata Park Street", "Ahmedabad SG Highway"
    };

    private static final String[] TX_TYPES = {"WIRE", "ACH", "SWIFT", "INTERNAL", "CARD", "UPI", "NEFT", "RTGS"};
    private static final String[] STATUSES = {"COMPLETED", "COMPLETED", "COMPLETED", "PENDING", "FLAGGED", "REJECTED"};
    private static final String[] CURRENCIES = {"INR", "USD", "EUR", "GBP"};

    private static final String[] DESCRIPTIONS = {
        "Salary credit from TechCorp Solutions Pvt Ltd",
        "Wire transfer to Global Exports LLC for invoice payment",
        "Monthly EMI payment for home loan account",
        "Online purchase from Amazon India marketplace",
        "Utility bill payment to BESCOM electricity board",
        "Insurance premium payment to LIC policy renewal",
        "Mutual fund SIP investment in HDFC Equity Fund",
        "Credit card bill payment for ICICI Platinum card",
        "International wire transfer to offshore subsidiary account",
        "Refund processed from cancelled flight booking GoAir",
        "Vendor payment to Reliance Digital for bulk hardware order",
        "Cash deposit at ATM branch Mumbai Central",
        "Standing instruction payment for recurring deposit",
        "Corporate payroll processing batch transfer Q3",
        "Tax payment to Income Tax Department advance tax",
        "Dividend credit from Infosys Technologies shares",
        "Loan disbursement approved for vehicle finance",
        "Inter-branch transfer to savings account backup",
        "Charity donation to PM Relief Fund verified",
        "Rent payment to landlord monthly tenancy agreement",
        "Stock brokerage settlement Zerodha demat account",
        "Foreign exchange conversion USD to INR treasury",
        "Service charge deduction quarterly maintenance fee",
        "Government subsidy credit DBT scheme payment",
        "Suspicious high-value transfer to unknown shell company offshore",
        "Multiple rapid small deposits structuring pattern detected",
        "Cash withdrawal exceeding daily limit emergency override",
        "Cross-border transaction flagged for compliance review AML"
    };

    private static final String[] PAYMENT_CODES = {
        "PAY-2024-001", "PAY-2024-002", "SWIFT-BOFAUS3N", "SWIFT-HDFCINBB",
        "NEFT-REF-78234", "RTGS-REF-99012", "UPI-REF-456789", "IMPS-REF-321654",
        "TXN-WIRE-5501", "TXN-ACH-8834", "PAY-INV-2024-Q3", "PAY-SAL-OCT24",
        "RTN-021000021", "RTN-026009593", "IFSC-HDFC0001234", "IFSC-SBIN0005432"
    };

    // ----- Transaction Generator -----
    public static List<Transaction> generateTransactions(int count) {
        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String txnId = String.format("TXN%06d", i + 1);
            String sender = String.format("ACC%04d", random.nextInt(50) + 1);
            String receiver = String.format("ACC%04d", random.nextInt(50) + 1);
            while (receiver.equals(sender)) receiver = String.format("ACC%04d", random.nextInt(50) + 1);

            double amount = Math.round((100 + random.nextDouble() * 999900) * 100.0) / 100.0;
            String desc = DESCRIPTIONS[random.nextInt(DESCRIPTIONS.length)];
            String code = PAYMENT_CODES[random.nextInt(PAYMENT_CODES.length)];
            String type = TX_TYPES[random.nextInt(TX_TYPES.length)];
            String status = STATUSES[random.nextInt(STATUSES.length)];
            String currency = CURRENCIES[random.nextInt(CURRENCIES.length)];
            LocalDateTime ts = LocalDateTime.of(2024, 1 + random.nextInt(12),
                    1 + random.nextInt(28), random.nextInt(24), random.nextInt(60));

            transactions.add(new Transaction(txnId, sender, receiver, amount, desc, code, type, status, ts, currency));
        }
        return transactions;
    }

    // ----- Account Generator -----
    public static List<Account> generateAccounts(int count) {
        List<Account> accounts = new ArrayList<>();
        String[] types = {"SAVINGS", "CHECKING", "BUSINESS", "INVESTMENT"};
        for (int i = 0; i < count; i++) {
            String accId = String.format("ACC%04d", i + 1);
            String name = NAMES[i % NAMES.length];
            String type = types[random.nextInt(types.length)];
            double balance = Math.round((1000 + random.nextDouble() * 9999000) * 100.0) / 100.0;
            String branch = BRANCHES[random.nextInt(BRANCHES.length)];
            String ifsc = String.format("FNSV%07d", random.nextInt(10000000));
            accounts.add(new Account(accId, name, type, balance, branch, ifsc));
        }
        return accounts;
    }

    // ----- Financial Document Generator -----
    public static List<FinancialDocument> generateDocuments() {
        List<FinancialDocument> docs = new ArrayList<>();
        docs.add(new FinancialDocument("DOC001", "Q3 2024 Financial Audit Report",
            "The quarterly audit reveals consistent transaction patterns across all major branches. " +
            "Wire transfers to international accounts have increased by 15% compared to Q2. " +
            "Suspicious activity was flagged in 3 accounts involving rapid small deposits " +
            "below the reporting threshold. The compliance team recommends enhanced monitoring " +
            "for accounts ACC0012 ACC0034 and ACC0045. Total transaction volume reached INR 450 crore " +
            "with average transaction size of INR 2.3 lakh. Cross-border payments constituted 8% of volume.",
            "AUDIT", "2024-10-01", "Internal Audit Team"));

        docs.add(new FinancialDocument("DOC002", "Anti-Money Laundering Policy 2024",
            "This policy outlines procedures for detecting and preventing money laundering activities. " +
            "Key indicators include structuring deposits below INR 10 lakh threshold, " +
            "layering through multiple shell company accounts, round-tripping of funds via " +
            "offshore entities, and smurfing using multiple low-value transactions. " +
            "All employees must complete annual AML training and report suspicious activity " +
            "within 24 hours to the compliance officer. Enhanced due diligence required for " +
            "politically exposed persons and high-risk jurisdictions.",
            "COMPLIANCE", "2024-01-15", "Compliance Department"));

        docs.add(new FinancialDocument("DOC003", "Annual Account Statement Summary",
            "Total deposits processed: 12,456 transactions worth INR 890 crore. " +
            "Total withdrawals: 8,234 transactions worth INR 567 crore. " +
            "Net position: positive INR 323 crore. Top performing branch: Mumbai Central " +
            "with 2,345 transactions. Highest individual transaction: INR 45 crore wire transfer " +
            "from corporate account ACC0001 to vendor payment account. Digital transactions " +
            "constitute 78% of total volume. UPI payments grew 45% year over year.",
            "STATEMENT", "2024-12-31", "Finance Department"));

        docs.add(new FinancialDocument("DOC004", "Risk Assessment Report - High Value Transactions",
            "Analysis of transactions exceeding INR 50 lakh during Q4 2024. " +
            "Total high-value transactions: 156 across 45 unique accounts. " +
            "78% were legitimate corporate payments with proper documentation. " +
            "12% required additional verification due to first-time beneficiary. " +
            "10% were flagged for enhanced review - 3 confirmed as suspicious " +
            "involving potential trade-based money laundering through over-invoicing. " +
            "Recommended action: freeze accounts ACC0023 and ACC0041 pending investigation.",
            "REPORT", "2024-12-15", "Risk Management"));

        return docs;
    }

    // ----- Fraud Pattern Keywords -----
    public static String[] getFraudPatterns() {
        return new String[]{
            "shell company", "money laundering", "structuring", "smurfing",
            "layering", "offshore", "suspicious", "flagged", "round-tripping",
            "over-invoicing", "freeze", "compliance", "threshold", "unauthorized"
        };
    }

    // ----- Concatenated transaction descriptions for text search -----
    public static String buildTransactionCorpus(List<Transaction> transactions) {
        StringBuilder sb = new StringBuilder();
        for (Transaction t : transactions) {
            sb.append(t.getDescription()).append(" | ");
        }
        return sb.toString();
    }
}
