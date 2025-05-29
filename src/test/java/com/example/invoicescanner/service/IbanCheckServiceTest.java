package com.example.invoicescanner.service;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class IbanCheckServiceTest {

    private final IbanCheckService ibanCheckService = new IbanCheckService();

    @Test
    void testFindBlacklistedIban_found() {
        String text = "Rechnung mit IBAN: DE89370400440532013000";
        Optional<String> result = ibanCheckService.findBlacklistedIban(text);
        assertTrue(result.isPresent());
        assertEquals("DE89370400440532013000", result.get());
    }

    @Test
    void testFindBlacklistedIban_notFound() {
        String text = "IBAN: DE12345678901234567890";
        Optional<String> result = ibanCheckService.findBlacklistedIban(text);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindBlacklistedIban_invalidText() {
        String text = "Keine IBAN enthalten.";
        Optional<String> result = ibanCheckService.findBlacklistedIban(text);
        assertTrue(result.isEmpty());
    }
}
