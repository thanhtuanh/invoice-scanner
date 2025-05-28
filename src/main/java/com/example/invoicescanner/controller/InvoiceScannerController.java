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

@RestController
@RequestMapping("/scan")
public class InvoiceScannerController {

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
            return ResponseEntity.badRequest().body(new ScanResult("ERROR", "No URL provided"));
        }
        try {
            String text = pdfScannerService.extractTextFromPdf(url);
            Optional<String> blacklistedIban = ibanCheckService.findBlacklistedIban(text);

            if (blacklistedIban.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ScanResult("FAILED", "Blacklisted IBAN found: " + blacklistedIban.get()));
            }
            return ResponseEntity.ok(new ScanResult("OK", "No blacklisted IBANs found."));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ScanResult("ERROR", "Failed to process PDF: " + e.getMessage()));
        }
    }
}
