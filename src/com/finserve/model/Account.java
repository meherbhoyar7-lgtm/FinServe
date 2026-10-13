package com.finserve.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a bank account in the FinServe system.
 */
public class Account {
    private String accountId;
    private String holderName;
    private String accountType; // SAVINGS, CHECKING, BUSINESS, INVESTMENT
    private double balance;
    private String branch;
    private String ifscCode;
    private List<String> transactionIds;

    public Account() {
        this.transactionIds = new ArrayList<>();
    }

    public Account(String accountId, String holderName, String accountType,
                   double balance, String branch, String ifscCode) {
        this.accountId = accountId;
        this.holderName = holderName;
        this.accountType = accountType;
        this.balance = balance;
        this.branch = branch;
        this.ifscCode = ifscCode;
        this.transactionIds = new ArrayList<>();
    }

    // Getters
    public String getAccountId() { return accountId; }
    public String getHolderName() { return holderName; }
    public String getAccountType() { return accountType; }
    public double getBalance() { return balance; }
    public String getBranch() { return branch; }
    public String getIfscCode() { return ifscCode; }
    public List<String> getTransactionIds() { return transactionIds; }

    // Setters
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public void setHolderName(String holderName) { this.holderName = holderName; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public void setBalance(double balance) { this.balance = balance; }
    public void setBranch(String branch) { this.branch = branch; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }

    public void addTransactionId(String txnId) { this.transactionIds.add(txnId); }

    public String toJson() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"accountId\":\"").append(esc(accountId)).append("\",");
        sb.append("\"holderName\":\"").append(esc(holderName)).append("\",");
        sb.append("\"accountType\":\"").append(esc(accountType)).append("\",");
        sb.append("\"balance\":").append(balance).append(",");
        sb.append("\"branch\":\"").append(esc(branch)).append("\",");
        sb.append("\"ifscCode\":\"").append(esc(ifscCode)).append("\"");
        sb.append("}");
        return sb.toString();
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
