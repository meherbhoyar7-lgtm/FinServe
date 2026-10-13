package com.finserve.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a financial transaction in the FinServe banking system.
 * Used across all modules for transaction-based analytics.
 */
public class Transaction {
    private String transactionId;
    private String senderAccount;
    private String receiverAccount;
    private double amount;
    private String description;
    private String paymentCode;
    private String transactionType; // WIRE, ACH, SWIFT, INTERNAL, CARD
    private String status;          // COMPLETED, PENDING, FLAGGED, REJECTED
    private LocalDateTime timestamp;
    private String currency;

    public Transaction() {}

    public Transaction(String transactionId, String senderAccount, String receiverAccount,
                       double amount, String description, String paymentCode,
                       String transactionType, String status, LocalDateTime timestamp, String currency) {
        this.transactionId = transactionId;
        this.senderAccount = senderAccount;
        this.receiverAccount = receiverAccount;
        this.amount = amount;
        this.description = description;
        this.paymentCode = paymentCode;
        this.transactionType = transactionType;
        this.status = status;
        this.timestamp = timestamp;
        this.currency = currency;
    }

    // Getters
    public String getTransactionId() { return transactionId; }
    public String getSenderAccount() { return senderAccount; }
    public String getReceiverAccount() { return receiverAccount; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getPaymentCode() { return paymentCode; }
    public String getTransactionType() { return transactionType; }
    public String getStatus() { return status; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getCurrency() { return currency; }

    // Setters
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public void setSenderAccount(String senderAccount) { this.senderAccount = senderAccount; }
    public void setReceiverAccount(String receiverAccount) { this.receiverAccount = receiverAccount; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setDescription(String description) { this.description = description; }
    public void setPaymentCode(String paymentCode) { this.paymentCode = paymentCode; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public void setStatus(String status) { this.status = status; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String toJson() {
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return "{" +
            "\"transactionId\":\"" + esc(transactionId) + "\"," +
            "\"senderAccount\":\"" + esc(senderAccount) + "\"," +
            "\"receiverAccount\":\"" + esc(receiverAccount) + "\"," +
            "\"amount\":" + amount + "," +
            "\"description\":\"" + esc(description) + "\"," +
            "\"paymentCode\":\"" + esc(paymentCode) + "\"," +
            "\"transactionType\":\"" + esc(transactionType) + "\"," +
            "\"status\":\"" + esc(status) + "\"," +
            "\"timestamp\":\"" + (timestamp != null ? timestamp.format(fmt) : "") + "\"," +
            "\"currency\":\"" + esc(currency) + "\"" +
        "}";
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    @Override
    public String toString() {
        return transactionId + " | " + description + " | $" + String.format("%.2f", amount);
    }
}
