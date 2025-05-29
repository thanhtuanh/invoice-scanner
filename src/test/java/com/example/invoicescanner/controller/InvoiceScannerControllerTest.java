package com.example.invoicescanner.controller;

import com.example.invoicescanner.service.IbanCheckService;
import com.example.invoicescanner.service.PdfScannerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

@WebMvcTest(InvoiceScannerController.class)
class InvoiceScannerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PdfScannerService pdfScannerService;

    @MockBean
    private IbanCheckService ibanCheckService;

    @Test
    void testScanInvoice_ok() throws Exception {
        String fakeText = "IBAN: DE12345678901234567890";
        given(pdfScannerService.extractTextFromPdf(anyString())).willReturn(fakeText);
        given(ibanCheckService.findBlacklistedIban(fakeText)).willReturn(Optional.empty());

        mockMvc.perform(post("/scan")
                .contentType("application/json")
                .content("{\"pdfUrl\": \"https://example.com/invoice.pdf\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));
    }

    @Test
    void testScanInvoice_blacklisted() throws Exception {
        String fakeText = "IBAN: DE89370400440532013000";
        given(pdfScannerService.extractTextFromPdf(anyString())).willReturn(fakeText);
        given(ibanCheckService.findBlacklistedIban(fakeText)).willReturn(Optional.of("DE89370400440532013000"));

        mockMvc.perform(post("/scan")
                .contentType("application/json")
                .content("{\"pdfUrl\": \"https://example.com/invoice.pdf\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("FAILED"));
    }

    @Test
    void testScanInvoice_missingUrl() throws Exception {
        mockMvc.perform(post("/scan")
                .contentType("application/json")
                .content("{\"pdfUrl\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }
}
