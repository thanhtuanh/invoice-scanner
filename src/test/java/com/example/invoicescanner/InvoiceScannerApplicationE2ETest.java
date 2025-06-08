package com.example.invoicescanner; // Wichtig: Im Root-Package der Anwendung für @SpringBootTest

import com.example.invoicescanner.model.ScanResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat; // Beliebte Assertion-Bibliothek, ggf. adden
// Wenn AssertJ nicht verfügbar ist, können Sie auch org.junit.jupiter.api.Assertions verwenden, z.B. assertEquals

/**
 * End-to-End Tests für die gesamte Invoice Scanner Anwendung.
 * Startet den vollständigen Spring Boot Kontext und sendet echte HTTP-Anfragen.
 * Dies testet die Integration aller Schichten (Controller, Services, externe URL-Downloads).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InvoiceScannerApplicationE2ETest {

    @LocalServerPort
    private int port; // Der zufällig zugewiesene Port des eingebetteten Webservers

    @Autowired
    private TestRestTemplate restTemplate; // Spring's Helferklasse zum Senden von HTTP-Anfragen

    // Region: Testfälle für den /scan Endpunkt
    // ---

    @Test
    void testScanInvoice_e2e_ok() {
        // GIVEN: Eine PDF-URL, die eine sichere IBAN enthält
        // Diese URL muss von der Anwendung auch tatsächlich erreichbar sein
        String pdfUrl = "https://github.com/thanhtuanh/invoice-scanner/raw/main/invoice-ok.pdf";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("pdfUrl", pdfUrl);

        // WHEN: Eine POST-Anfrage an den /scan Endpunkt gesendet wird
        // Der TestRestTemplate sendet die Anfrage an den auf einem zufälligen Port laufenden Server
        ResponseEntity<ScanResult> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/scan", // Komplette URL zum Endpunkt
                requestBody, // Der JSON-Request-Body
                ScanResult.class // Die erwartete Antwortklasse
        );

        // THEN: Die Antwort sollte HTTP 200 OK sein und den Status "OK" enthalten
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("OK");
        assertThat(response.getBody().getMessage()).contains("No blacklisted IBANs found.");
    }

    @Test
    void testScanInvoice_e2e_blacklisted() {
        // GIVEN: Eine PDF-URL, die eine auf der Blacklist stehende IBAN enthält
        String pdfUrl = "https://github.com/thanhtuanh/invoice-scanner/raw/main/invoice.pdf";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("pdfUrl", pdfUrl);

        // WHEN: Eine POST-Anfrage an den /scan Endpunkt gesendet wird
        ResponseEntity<ScanResult> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/scan",
                requestBody,
                ScanResult.class
        );

        // THEN: Die Antwort sollte HTTP 400 Bad Request sein und den Status "FAILED" enthalten
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("FAILED");
        assertThat(response.getBody().getMessage()).contains("Blacklisted IBAN found: DE89370400440532013000");
    }

    @Test
    void testScanInvoice_e2e_nonExistentPdfUrl() {
        // GIVEN: Eine URL, die zu keiner existierenden PDF führt
        String pdfUrl = "https://example.com/does-not-exist-this-pdf.pdf"; // Diese URL ist absichtlich nicht erreichbar
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("pdfUrl", pdfUrl);

        // WHEN: Eine POST-Anfrage gesendet wird
        ResponseEntity<ScanResult> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/scan",
                requestBody,
                ScanResult.class
        );

        // THEN: Die Antwort sollte HTTP 500 Internal Server Error sein (wegen IOException)
        // und den Status "ERROR" enthalten
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("ERROR");
        // Die genaue Fehlermeldung kann je nach zugrunde liegendem Fehler (z.B. Host not found, 404 von example.com) variieren,
        // daher prüfen wir nur auf einen Teil der erwarteten Nachricht.
        assertThat(response.getBody().getMessage()).contains("Failed to process PDF:");
    }

    @Test
    void testScanInvoice_e2e_emptyUrl() {
        // GIVEN: Ein leerer Request-Body für die URL
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("pdfUrl", ""); // Leere URL

        // WHEN: Eine POST-Anfrage gesendet wird
        ResponseEntity<ScanResult> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/scan",
                requestBody,
                ScanResult.class
        );

        // THEN: Die Antwort sollte HTTP 400 Bad Request sein und den Status "ERROR" enthalten
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("ERROR");
        assertThat(response.getBody().getMessage()).contains("No URL provided");
    }
}