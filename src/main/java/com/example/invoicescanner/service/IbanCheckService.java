package com.example.invoicescanner.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IbanCheckService {

    private static final List<String> BLACKLISTED_IBANS = List.of(
            "DE89370400440532013000",
            "FR7630006000011234567890189",
            "GB33BUKB20201555555555"
    );

    private static final Pattern IBAN_PATTERN = Pattern.compile("[A-Z]{2}[0-9]{2}[A-Z0-9]{11,30}");

    public Optional<String> findBlacklistedIban(String text) {
        Matcher matcher = IBAN_PATTERN.matcher(text);
        while (matcher.find()) {
            String iban = matcher.group();
            if (BLACKLISTED_IBANS.contains(iban)) {
                return Optional.of(iban);
            }
        }
        return Optional.empty();
    }
}
