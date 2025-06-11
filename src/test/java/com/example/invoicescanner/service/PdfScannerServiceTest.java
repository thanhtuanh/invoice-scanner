package com.example.invoicescanner.service;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PdfScannerServiceTest {

    private final PdfScannerService pdfScannerService = new PdfScannerService();

    @Test
    void testExtractTextFromValidPdf() throws IOException {
        // GIVEN: Eine gültige PDF-URL mit Text
        String url = "https://github.com/thanhtuanh/invoice-scanner/raw/main/invoice-ok.pdf";

        // WHEN: PDF wird geladen und Text extrahiert
        String text = pdfScannerService.extractTextFromPdf(url);

        // THEN: Text sollte nicht leer sein
        assertNotNull(text);
        assertFalse(text == null || text.trim().isEmpty());
        assertTrue(text.contains("Invoice")); // Optional: Inhalt prüfen
    }

    @Test
    void testExtractTextFromInvalidUrl() {
        String url = "https://example.com/does-not-exist.pdf";

        assertThrows(IOException.class, () -> {
            pdfScannerService.extractTextFromPdf(url);
        });
    }

    @Test
    void testExtractTextFromMalformedUrl() {
        String url = "ht!tp://malformed-url";

        assertThrows(IOException.class, () -> {
            pdfScannerService.extractTextFromPdf(url);
        });
    }
}
