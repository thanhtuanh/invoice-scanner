package com.example.invoicescanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Markiert die Hauptklasse als Spring Boot Anwendung (inkl. @Configuration, @EnableAutoConfiguration, @ComponentScan)
@SpringBootApplication
public class InvoiceScannerApplication {

    public static void main(String[] args) {
        // Startet die Spring Boot Anwendung mit eingebettetem Tomcat-Server
        SpringApplication.run(InvoiceScannerApplication.class, args);
    }
}
