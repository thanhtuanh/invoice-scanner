package com.example.invoicescanner.controller;

import com.example.invoicescanner.service.IbanCheckService;
import com.example.invoicescanner.service.PdfScannerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.*; // für given/when/then Syntax
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

@WebMvcTest(InvoiceScannerController.class) // Startet nur Web Layer, nicht den kompletten Spring Context
class InvoiceScannerControllerTest {

    @Autowired
    private MockMvc mockMvc; // Ermöglicht das Simulieren von HTTP-Requests an den Controller

    @MockBean
    private PdfScannerService pdfScannerService; // Diese Bean wird "gemockt", also ersetzt

    @MockBean
    private IbanCheckService ibanCheckService;

    @Test
    void testScanInvoice_ok() throws Exception {
        // Vorbereitete Rückgabe: Kein Treffer auf Blacklist
        String fakeText = "IBAN: DE12345678901234567890";
        given(pdfScannerService.extractTextFromPdf(anyString())).willReturn(fakeText);
        given(ibanCheckService.findBlacklistedIban(fakeText)).willReturn(Optional.empty());

        // Simulation eines HTTP POST Requests
        mockMvc.perform(post("/scan")
                .contentType("application/json")
                .content("{\"pdfUrl\": \"https://example.com/invoice.pdf\"}"))
                .andExpect(status().isOk()) // Erwartung: HTTP 200
                .andExpect(jsonPath("$.status").value("OK")); // Erwartung: JSON { status: "OK" }
    }

    @Test
    void testScanInvoice_blacklisted() throws Exception {
        // Fake-Text enthält eine gesperrte IBAN
        String fakeText = "IBAN: DE89370400440532013000";
        given(pdfScannerService.extractTextFromPdf(anyString())).willReturn(fakeText);
        given(ibanCheckService.findBlacklistedIban(fakeText)).willReturn(Optional.of("DE89370400440532013000"));

        mockMvc.perform(post("/scan")
                .contentType("application/json")
                .content("{\"pdfUrl\": \"https://example.com/invoice.pdf\"}"))
                .andExpect(status().isBadRequest()) // Erwartung: HTTP 400
                .andExpect(jsonPath("$.status").value("FAILED")); // Erwartung: JSON { status: "FAILED" }
    }

    @Test
    void testScanInvoice_missingUrl() throws Exception {
        // Request enthält leere URL -> Validierungsfehler
        mockMvc.perform(post("/scan")
                .contentType("application/json")
                .content("{\"pdfUrl\": \"\"}"))
                .andExpect(status().isBadRequest()) // Erwartung: HTTP 400
                .andExpect(jsonPath("$.status").value("ERROR")); // Erwartung: JSON { status: "ERROR" }
    }
}
