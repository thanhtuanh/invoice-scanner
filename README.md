# 📄 Invoice Scanner Service

Ein Java-Service mit Spring Boot, der PDF-Rechnungen auf gesperrte IBANs prüft – zur Vorbeugung von Geldwäsche durch automatische Dokumentenanalyse.

🌐 **Live-Demo Deployment**:  
Das Projekt ist online verfügbar unter:

🔗 **https://invoice-scanner-service.onrender.com**

👉 Du kannst dort direkt die API testen – z. B. per `curl`:

```bash
curl -X POST https://invoice-scanner-service.onrender.com/scan \
  -H "Content-Type: application/json" \
  -d '{"pdfUrl": "https://github.com/thanhtuanh/invoice-scanner/raw/main/invoice.pdf"}'


---

## ✅ Funktionen

- 📥 Akzeptiert eine PDF-URL via REST-API
- 🔍 Extrahiert Text aus PDF-Dateien (sofern „selectable“)
- 🚫 Prüft auf gesperrte IBANs (Blacklist)
- 🔁 Liefert API-Status: OK, FAILED, ERROR
- 🧱 Erweiterbar für zusätzliche Prüfregeln und Persistenz
- 🧪 Getestet mit Unit- und Integrationstests (JUnit + MockMvc +  Spring Boot Test)
- 📝 Logging via SLF4J/Logback (inkl. Stacktrace bei Fehlern)

---

## 🏗️ Technologien

- Java 17
- Spring Boot 3.2
- Apache PDFBox
- SLF4J + Logback
- JUnit 5, MockMvc
- Regex für IBAN-Erkennung
- Maven Buildsystem

---

## 🚀 Lokale Ausführung

### Voraussetzungen

- Java 17
- Maven 3.8+

### Starten

```bash
git clone https://github.com/thanhtuanh/invoice-scanner.git
cd invoice-scanner
./mvnw spring-boot:run
```

### Alternativ: Docker-Container bauen

```bash
./mvnw clean package
docker build -t invoice-scanner .
docker run -p 8080:8080 invoice-scanner
```

---

## 📮 API-Aufrufe (z. B. über Postman oder `.http`)

```http
### 1. PDF mit blacklisted IBAN scannen
POST http://localhost:8080/scan
Content-Type: application/json
{
  "pdfUrl": "https://github.com/thanhtuanh/invoice-scanner/raw/main/invoice.pdf"
}

### 2. PDF mit sicherer IBAN scannen (OK)
POST http://localhost:8080/scan
Content-Type: application/json
{
  "pdfUrl": "https://github.com/thanhtuanh/invoice-scanner/raw/main/invoice-ok.pdf"
}

### 3. Fehlerfall: nicht existierende URL
POST http://localhost:8080/scan
Content-Type: application/json
{
  "pdfUrl": "https://example.com/does-not-exist.pdf"
}

### 4. Fehlerfall: leere URL
POST http://localhost:8080/scan
Content-Type: application/json
{
  "pdfUrl": ""
}
```

---

## 🧪 Tests

```bash
mvn test
```

- `IbanCheckServiceTest`: prüft IBAN-Erkennung
- `InvoiceScannerControllerTest`: prüft API-Logik mit MockMvc
- Tests laufen über JUnit 5 + Spring Testkontext

---

## 📄 Beispielhafte Blacklist (fest kodiert)

```java
List.of(
  "DE89370400440532013000",
  "FR7630006000011234567890189",
  "GB33BUKB20201555555555"
);
```

---

## 🛠 Erweiterungsideen

- 💾 Anbindung an persistente Datenbank (z. B. PostgreSQL)
- 🧠 OCR-Modul für gescannte PDFs (Tesseract)
- 🧾 Audit-Trail & Exportfunktion
- 🔐 Basic-Auth oder JWT-Token für Absicherung der API
- 📤 Frontend-UI (React oder Angular)
- 🌐 Deployment auf Render.com oder Fly.io

---

## 📚 Hinweise

- IBAN-Erkennung per Regex: `[A-Z]{2}[0-9]{2}[A-Z0-9]{11,30}`
- Nur „selectable text“ in PDFs wird erkannt – keine OCR-Verarbeitung

---

## 👤 Autor

Erstellt von [Duc Thanh Nguyen](https://github.com/thanhtuanh)  
→ Teil des Bewerbungsportfolios: [🔗 GitHub-Portfolio](https://github.com/thanhtuanh/bewerbung)