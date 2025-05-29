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

@RestController
@RequestMapping("/scan")
public class InvoiceScannerController {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceScannerController.class);

    private final PdfScannerService pdfScannerService;
    private final IbanCheckService ibanCheckService;

    public InvoiceScannerController(PdfScannerService pdfScannerService, IbanCheckService ibanCheckService) {
        this.pdfScannerService = pdfScannerService;
        this.ibanCheckService = ibanCheckService;
    }

    @PostMapping
    public ResponseEntity<ScanResult> scanInvoice(@RequestBody Map<String, String> request) {
        String url = request.get("pdfUrl");
        if (url == null || url.isEmpty()) {
            logger.error("Fehlende URL im Request");
            return ResponseEntity.badRequest().body(new ScanResult("ERROR", "No URL provided"));
        }

        try {
            logger.info("Starte PDF-Scan für URL: {}", url);
            String text = pdfScannerService.extractTextFromPdf(url);
            Optional<String> blacklistedIban = ibanCheckService.findBlacklistedIban(text);

            if (blacklistedIban.isPresent()) {
                String message = "Blacklisted IBAN found: " + blacklistedIban.get();
                logger.warn(message);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ScanResult("FAILED", message));
            }

            logger.info("PDF geprüft – keine Blacklist-IBAN gefunden.");
            return ResponseEntity.ok(new ScanResult("OK", "No blacklisted IBANs found."));

        } catch (IOException e) {
            logger.error("Fehler beim Verarbeiten der PDF", e);  // Stacktrace im Log!
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ScanResult("ERROR", "Failed to process PDF: " + e.getMessage()));
        }
    }
}