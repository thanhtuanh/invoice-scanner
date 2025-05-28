# 📄 Invoice Scanner Service

Ein einfacher Java-Service mit Spring Boot, der PDF-Rechnungen nach gesperrten IBANs durchsucht.

## ✅ Funktionen

* 📥 Akzeptiert eine PDF-URL via REST-API
* 🔍 Extrahiert Text aus PDF-Dateien (nur "selectable text")
* 🚫 Prüft, ob gesperrte IBANs im PDF enthalten sind
* 🧱 Erweiterbar für zusätzliche Prüfungen oder Persistenz

## 🏗️ Technologien

* Java 17+
* Spring Boot 3+
* Apache PDFBox
* Regex für IBAN-Erkennung

## 🚀 Lokale Ausführung

### Voraussetzungen

* Java 17
* Maven

### Starten

```bash
git clone https://github.com/thanhtuanh/invoice-scanner.git
cd invoice-scanner
./mvnw spring-boot:run
```

### Beispielaufruf

```bash
curl -X POST http://localhost:8080/scan \
  -H "Content-Type: application/json" \
  -d '{"pdfUrl": "https://github.com/thanhtuanh/invoice-scanner/raw/main/invoice.pdf"}'
```

### Beispielantwort (OK)

```json
{
  "status": "OK",
  "message": "No blacklisted IBANs found."
}
```

### Beispielantwort (FAILED)

```json
{
  "status": "FAILED",
  "message": "Blacklisted IBAN found: DE89370400440532013000"
}
```

## 🧪 Testdatei

🧾 Beispielrechnung zur Prüfung findest du hier:
👉 [`invoice.pdf`](https://github.com/thanhtuanh/invoice-scanner/raw/main/invoice.pdf)

## 🛠 Erweiterungsideen

* ✅ Integration einer persistierenden Datenbank (MongoDB/PostgreSQL)
* 🧠 Verwendung eines OCR-Moduls (z. B. Tesseract) für nicht-selektierbare Texte
* 🧾 Scan-Ergebnisse speichern (Audit Trail)
* 🧪 Unit- und Integrationstests (JUnit + MockMvc)
* 📑 Unterstützung von Dateiuploads (nicht nur URL)
* 🔐 Authentifizierung & Rate-Limiting
* 📤 Frontend-UI mit React oder Angular

## 📄 Beispielhafte Blacklist (fest kodiert)

```java
List.of(
  "DE89370400440532013000",
  "FR7630006000011234567890189",
  "GB33BUKB20201555555555"
);
```

## 📚 Hinweise

* IBANs werden via Regex erkannt: `[A-Z]{2}[0-9]{2}[A-Z0-9]{11,30}`
* PDF-Inhalte müssen "selectable" sein (kein gescanntes Bild)

## 📬 Kontakt

Erstellt von [Duc Thanh Nguyen](https://github.com/thanhtuanh)

