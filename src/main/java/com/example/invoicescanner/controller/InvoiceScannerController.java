package com.example.invoicescanner.controller;

import com.example.invoicescanner.model.ScanResult;
import com.example.invoicescanner.service.IbanCheckService;
import com.example.invoicescanner.service.PdfScannerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController // Diese Klasse ist ein REST-Controller und verarbeitet HTTP-Anfragen
@RequestMapping("/scan") // Basis-URL für alle Endpunkte in diesem Controller
public class InvoiceScannerController {

    // Logger für konsolenbasierte Ausgabe (z. B. Fehler oder Debug-Informationen)
    private static final Logger logger = LoggerFactory.getLogger(InvoiceScannerController.class);

    // Services für PDF-Verarbeitung und IBAN-Prüfung werden via Konstruktor injiziert
    private final PdfScannerService pdfScannerService;
    private final IbanCheckService ibanCheckService;

    // Konstruktor-Injektion der beiden Service-Klassen
    public InvoiceScannerController(PdfScannerService pdfScannerService, IbanCheckService ibanCheckService) {
        this.pdfScannerService = pdfScannerService;
        this.ibanCheckService = ibanCheckService;
    }

    // POST-Endpunkt: Erwartet eine JSON-Anfrage mit "pdfUrl" als Eingabeparameter
    @PostMapping
    public ResponseEntity<ScanResult> scanInvoice(@RequestBody Map<String, String> request) {
        // Extrahiere die PDF-URL aus dem Request-Body
        String url = request.get("pdfUrl");

        // Validierung: URL darf nicht leer oder null sein
        if (url == null || url.isEmpty()) {
            logger.error("Fehlende URL im Request");
            return ResponseEntity.badRequest().body(new ScanResult("ERROR", "No URL provided"));
        }

        try {
            logger.info("Starte PDF-Scan für URL: {}", url);

            // Lade und extrahiere den Textinhalt aus dem PDF-Dokument
            String text = pdfScannerService.extractTextFromPdf(url);

            // Suche im Text nach einer blacklisted IBAN
            Optional<String> blacklistedIban = ibanCheckService.findBlacklistedIban(text);

            // Falls eine blacklisted IBAN gefunden wurde, gib einen Fehler zurück
            if (blacklistedIban.isPresent()) {
                String message = "Blacklisted IBAN found: " + blacklistedIban.get();
                logger.warn(message);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ScanResult("FAILED", message));
            }

            // Kein Treffer → Rückmeldung: alles in Ordnung
            logger.info("PDF geprüft – keine Blacklist-IBAN gefunden.");
            return ResponseEntity.ok(new ScanResult("OK", "No blacklisted IBANs found."));

        } catch (IOException e) {
            // Fehler beim Lesen/Verarbeiten der PDF → interne Server-Fehlermeldung
            logger.error("Fehler beim Verarbeiten der PDF", e);  // Stacktrace wird geloggt
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ScanResult("ERROR", "Failed to process PDF: " + e.getMessage()));
        }
    }
}
