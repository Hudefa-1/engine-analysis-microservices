# Engine Quality Analysis – Microservice Architecture

Semesterprojekt aus dem Modul **Service-basierte Komponenten-Architekturen**.

Dieses Projekt ist ein Proof of Concept für eine Microservice-basierte Anwendung zur Konfiguration und Analyse eines Dieselmotors. Eine Motorkonfiguration wird über eine Weboberfläche erstellt und anschließend von mehreren eigenständigen Analyse-Services geprüft.

## Technologien

- Java 17
- Spring Boot
- Spring Data JPA
- REST
- H2 Database
- Resilience4j
- Maven
- Docker
- Docker Compose
- HTML, CSS und JavaScript

## Architektur

Die Anwendung besteht aus fünf Spring-Boot-Services:

- **Configuration Service (Port 8080)**  
  Zentrale Koordination der Analysen, Speicherung der Konfigurationen und Ergebnisse, H2-Datenbank, Weboberfläche und Circuit-Breaker-Logik.

- **Fluid Analysis (Port 8081)**  
  Analyse von Oil System, Fuel System und Cooling System.

- **Mechanical Analysis (Port 8082)**  
  Analyse von Exhaust System, Mounting System, Power Transmission und Gearbox.

- **Electrical Analysis (Port 8083)**  
  Analyse von Starting System, Auxiliary PTO und Monitoring & Control System.

- **Engine Management (Port 8084)**  
  Wird nach Abschluss der drei Teilanalysen gestartet und erzeugt das Gesamtergebnis.

### Ablauf

1. Der Benutzer erstellt eine Motorkonfiguration über die Weboberfläche.
2. Der Configuration Service speichert die Konfiguration.
3. Fluid, Mechanical und Electrical Analysis werden parallel gestartet.
4. Die drei Services führen ihre Prüfungen unabhängig voneinander durch.
5. Die Ergebnisse werden per REST-Callback an den Configuration Service zurückgesendet.
6. Sobald alle drei Teilanalysen abgeschlossen sind, wird Engine Management gestartet.
7. Engine Management verarbeitet die Teilergebnisse und erzeugt das Gesamtergebnis.
8. Die Weboberfläche zeigt Status und Resultate an.

Vereinfacht:

```text
Benutzer
   |
   v
Weboberfläche
   |
   v
Configuration Service
   |
   +----> Fluid Analysis
   |
   +----> Mechanical Analysis
   |
   +----> Electrical Analysis
              |
              v
     Ergebnisse zurück
              |
              v
     Configuration Service
              |
              v
      Engine Management
              |
              v
        Gesamtergebnis
```

## Fachliche und technische Fehler

Das Projekt unterscheidet bewusst zwischen **fachlichen** und **technischen** Fehlern.

### Fachlicher Fehler

Ein fachlicher Fehler bedeutet, dass ein Analyse-Service technisch korrekt funktioniert, aber eine ungültige Konfiguration erkennt.

Beispiel aus der Fluid Analysis:

```text
Fuel System: high-pressure
Cooling System: water
```

Mögliches Ergebnis:

```text
Status: ready
Result: oil=ok; fuel=ok; cooling=failed
```

`ready` bedeutet, dass der Service technisch erfolgreich gearbeitet hat. `cooling=failed` bedeutet dagegen, dass die fachliche Prüfung eine ungültige Kombination erkannt hat.

Weitere fachliche Fehlerfälle:

```text
Mechanical Analysis:
Power Transmission: hybrid
Gearbox: manual
→ gearbox=failed
```

```text
Electrical Analysis:
Auxiliary PTO: heavy-duty
Monitoring & Control: standard
→ monitoring=failed
```

Wenn mindestens eine Teilanalyse ein fachliches `failed` enthält, wird auch das Gesamtergebnis durch Engine Management als `failed` bewertet.

### Technischer Fehler

Ein technischer Fehler liegt vor, wenn ein Service seine Analyse nicht durchführen kann, zum Beispiel weil:

- der Container gestoppt wurde,
- der Service nicht erreichbar ist,
- ein REST-Aufruf fehlschlägt.

In diesem Fall wird der technische Status des betroffenen Services auf `failed` gesetzt.

Beispiel:

```text
Mechanical Analysis nicht erreichbar
→ Status: failed
```

## Circuit Breaker

Für technische Fehler verwendet der Configuration Service **Resilience4j Circuit Breaker**.

Der Circuit Breaker schützt die Anwendung davor, einen bereits ausgefallenen Service immer wieder anzusprechen.

Die drei zentralen Zustände sind:

- **CLOSED** – Aufrufe werden normal weitergeleitet.
- **OPEN** – Weitere Aufrufe werden blockiert und direkt über den Fallback behandelt.
- **HALF-OPEN** – Nach einer Wartezeit wird ein Testaufruf zugelassen.

Vereinfacht:

```text
CLOSED
  |
  | wiederholte Fehler
  v
OPEN
  |
  | Wartezeit
  v
HALF-OPEN
  |
  +-- erfolgreicher Test --> CLOSED
  |
  +-- Fehler -------------> OPEN
```

Konfiguration im Projekt:

```text
Sliding Window Size: 2
Minimum Number of Calls: 2
Failure Rate Threshold: 100 %
Wait Duration in Open State: 30 Sekunden
Permitted Calls in Half-Open State: 1
```

Bei einem technischen Fehler wird ein Fallback ausgeführt. Dadurch bleibt die Anwendung erreichbar und kann den Fehler kontrolliert an die Weboberfläche weitergeben.

## REST-Kommunikation und Parallelität

Der Configuration Service startet die Analyse-Services per REST.

Nach Abschluss senden die Services ihre Ergebnisse über einen Callback zurück:

```text
Configuration Service
        |
        | POST
        v
  Analysis Service
        |
        | Analyse
        |
        | POST Callback
        v
Configuration Service
```

Fluid, Mechanical und Electrical Analysis laufen parallel. Engine Management startet erst, wenn alle drei Teilanalysen abgeschlossen sind.

## Persistenz

Die zentrale Persistenz befindet sich im Configuration Service.

Verwendet werden:

- Spring Data JPA
- H2 Database

Gespeichert werden unter anderem:

- Motorkonfigurationen
- Analyse-Datensätze
- Status der Teilanalysen
- Ergebnisse der Teilanalysen
- Engine-Management-Ergebnis
- Gesamtergebnis

Die einzelnen Analyse-Services besitzen keine eigene Datenbank.

## Docker

Jeder Microservice läuft in einem eigenen Docker-Container:

```text
configuration
fluid-analysis
mechanical-analysis
electrical-analysis
engine-management
```

Docker Compose startet und verbindet alle fünf Services.

Innerhalb des Docker-Netzwerks kommunizieren die Container über ihre Service-Namen:

```text
http://fluid-analysis:8081
http://mechanical-analysis:8082
http://electrical-analysis:8083
http://engine-management:8084
```

## Multi-Stage Build

Die Dockerfiles verwenden einen Multi-Stage Build.

In der ersten Phase wird der jeweilige Service mit Maven gebaut:

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests
```

In der zweiten Phase wird nur die fertige JAR-Datei in ein Java-Runtime-Image übernommen:

```dockerfile
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
```

Dadurch müssen vor dem Start keine JAR-Dateien manuell erstellt werden. Docker baut die Anwendung direkt aus dem Sourcecode.

## Anwendung starten

### Voraussetzungen

- Docker Desktop
- Docker Compose

Projekt starten:

```bash
docker compose up --build
```

Alternativ im Hintergrund:

```bash
docker compose up --build -d
```

Status der Container prüfen:

```bash
docker compose ps
```

Die Weboberfläche ist anschließend erreichbar unter:

```text
http://localhost:8080
```

Anwendung beenden:

```bash
docker compose down
```

## Circuit Breaker testen

Zum Testen eines technischen Fehlers kann beispielsweise der Mechanical Analysis Service gestoppt werden:

```bash
docker compose stop mechanical-analysis
```

Danach kann über die Weboberfläche eine neue Analyse gestartet werden. Der Configuration Service erkennt den technischen Fehler und führt den Fallback aus.

Service wieder starten:

```bash
docker compose start mechanical-analysis
```

## Projektstruktur

```text
.
├── configuration/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── fluid-analysis/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── mechanical-analysis/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── electrical-analysis/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── engine-management/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── Dokumentation/
├── docker-compose.yaml
├── .gitignore
└── README.md
```

## Zentrale Architekturentscheidungen

- Aufteilung in eigenständige Microservices
- zentrale Orchestrierung durch den Configuration Service
- parallele Ausführung der drei Teilanalysen
- REST-basierte Kommunikation
- Callback-basierte Rückmeldung der Ergebnisse
- zentrale Persistenz
- klare Trennung zwischen fachlichen und technischen Fehlern
- Circuit Breaker und Fallback-Mechanismen
- Containerisierung aller Services
- Multi-Stage-Builds für reproduzierbare Docker-Builds

## Ziel des Projekts

Das Projekt wurde als **Proof of Concept** entwickelt, um zentrale Konzepte service-basierter Softwarearchitekturen praktisch umzusetzen.

Im Mittelpunkt stehen insbesondere Microservices, REST-Kommunikation, parallele Verarbeitung, zentrale Orchestrierung, Persistenz, Resilience Pattern, Circuit Breaker, Fallback-Mechanismen und Docker.
