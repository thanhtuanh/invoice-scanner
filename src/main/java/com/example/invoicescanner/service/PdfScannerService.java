package com.example.invoicescanner.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Service // Diese Klasse ist ein Spring-Service und kann automatisch injiziert werden
public class PdfScannerService {

    /**
     * Lädt eine PDF-Datei von der angegebenen URL und extrahiert den Textinhalt.
     *
     * @param url PDF-Datei-URL (muss direkt herunterladbar sein)
     * @return extrahierter Text als String
     * @throws IOException bei Problemen mit Download oder Verarbeitung
     */
    public String extractTextFromPdf(String url) throws IOException {
        try (
            InputStream in = new URL(url).openStream(); // öffnet HTTP-Stream zur URL
            PDDocument document = PDDocument.load(in)  // lädt das PDF-Dokument
        ) {
            PDFTextStripper stripper = new PDFTextStripper(); // Werkzeug zum Textextrahieren
            return stripper.getText(document); // gibt extrahierten Text zurück
        }
    }
}
