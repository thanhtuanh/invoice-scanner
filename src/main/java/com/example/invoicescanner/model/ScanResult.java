package com.example.invoicescanner.model;

// Einfaches Datenmodell für die API-Antwort
public class ScanResult {

    private String status;  // z. B. "OK", "FAILED", "ERROR"
    private String message; // Detailnachricht zur Prüfung

    // Leerer Standardkonstruktor (wird für Deserialisierung benötigt)
    public ScanResult() {}

    // Konstruktor mit Status und Nachricht
    public ScanResult(String status, String message) {
        this.status = status;
        this.message = message;
    }

    // Getter und Setter
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
