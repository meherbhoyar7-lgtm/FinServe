package com.finserve.model;

/**
 * Represents a financial document in the FinServe system.
 * Used for document indexing and text-search operations.
 */
public class FinancialDocument {
    private String documentId;
    private String title;
    private String content;
    private String documentType; // REPORT, STATEMENT, AUDIT, COMPLIANCE, POLICY
    private String date;
    private String author;

    public FinancialDocument() {}

    public FinancialDocument(String documentId, String title, String content,
                             String documentType, String date, String author) {
        this.documentId = documentId;
        this.title = title;
        this.content = content;
        this.documentType = documentType;
        this.date = date;
        this.author = author;
    }

    // Getters
    public String getDocumentId() { return documentId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getDocumentType() { return documentType; }
    public String getDate() { return date; }
    public String getAuthor() { return author; }

    // Setters
    public void setDocumentId(String documentId) { this.documentId = documentId; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public void setDate(String date) { this.date = date; }
    public void setAuthor(String author) { this.author = author; }

    public String toJson() {
        return "{" +
            "\"documentId\":\"" + esc(documentId) + "\"," +
            "\"title\":\"" + esc(title) + "\"," +
            "\"content\":\"" + esc(content) + "\"," +
            "\"documentType\":\"" + esc(documentType) + "\"," +
            "\"date\":\"" + esc(date) + "\"," +
            "\"author\":\"" + esc(author) + "\"" +
        "}";
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }
}
