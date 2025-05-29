package com.example.invoicescanner.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IbanCheckService {

    private static final Logger logger = LoggerFactory.getLogger(IbanCheckService.class);

    private static final List<String> BLACKLISTED_IBANS = List.of(
            "DE89370400440532013000",
            "FR7630006000011234567890189",
            "GB33BUKB20201555555555"
    );

    private static final Pattern IBAN_PATTERN = Pattern.compile("[A-Z]{2}[0-9]{2}[A-Z0-9]{11,30}");

    public Optional<String> findBlacklistedIban(String text) {
        logger.info("Starte IBAN-Prüfung...");

        Matcher matcher = IBAN_PATTERN.matcher(text);
        while (matcher.find()) {
            String iban = matcher.group();
            logger.debug("Gefundene IBAN: {}", iban);

            if (BLACKLISTED_IBANS.contains(iban)) {
                logger.warn("Schwarze Liste: Gefundene gesperrte IBAN: {}", iban);
                return Optional.of(iban);
            }
        }

        logger.info("Keine gesperrte IBAN gefunden.");
        return Optional.empty();
    }
}
