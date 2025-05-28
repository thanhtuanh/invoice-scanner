package com.example.invoicescanner.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Service
public class PdfScannerService {

    public String extractTextFromPdf(String url) throws IOException {
        try (InputStream in = new URL(url).openStream();
             PDDocument document = PDDocument.load(in)) {

            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
} 
