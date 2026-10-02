# \# Engine Quality Analysis – Microservice Architecture

# 

# Semesterprojekt aus dem Modul \*\*Service-basierte Komponenten-Architekturen\*\*.

# 

# Das Projekt ist ein Proof of Concept für eine Microservice-basierte Anwendung zur Konfiguration und Analyse eines Dieselmotors. Eine Motorkonfiguration wird über eine Weboberfläche erstellt und anschließend von mehreren eigenständigen Analyse-Services geprüft.

# 

# Die Anwendung demonstriert unter anderem:

# 

# \- Microservice-Architektur

# \- REST-basierte Kommunikation

# \- parallele Verarbeitung

# \- zentrale Orchestrierung

# \- Spring Data JPA

# \- H2-Datenbank

# \- Circuit Breaker mit Resilience4j

# \- Fallback-Mechanismen

# \- Docker

# \- Docker Compose

# \- Multi-Stage-Docker-Builds

# \- HTML, CSS und JavaScript als Weboberfläche

# 

# \---

# 

# \## Technologien

# 

# Das Projekt verwendet folgende Technologien:

# 

# \- Java 17

# \- Spring Boot

# \- Spring Data JPA

# \- REST

# \- H2 Database

# \- Resilience4j

# \- Maven

# \- Docker

# \- Docker Compose

# \- HTML

# \- CSS

# \- JavaScript

# 

# \---

# 

# \## Architektur

# 

# Die Anwendung besteht aus fünf eigenständigen Spring-Boot-Services.

# 

# \### Configuration Service – Port 8080

# 

# Der Configuration Service ist die zentrale Komponente der Anwendung.

# 

# Aufgaben:

# 

# \- Bereitstellung der Weboberfläche

# \- Speicherung der Motorkonfigurationen

# \- Erstellung neuer Analysen

# \- Start der einzelnen Analyse-Services

# \- Speicherung der Analyseergebnisse

# \- Verwaltung der Analyse-Status

# \- Start des Engine Management Service

# \- Speicherung des Gesamtergebnisses

# \- Fehlerbehandlung mit Resilience4j

# 

# Der Configuration Service verwendet eine eingebettete H2-Datenbank.

# 

# \---

# 

# \### Fluid Analysis – Port 8081

# 

# Der Fluid Analysis Service analysiert unter anderem:

# 

# \- Oil System

# \- Fuel System

# \- Cooling System

# 

# Der Service arbeitet unabhängig vom Configuration Service und sendet sein Ergebnis nach Abschluss der Analyse per REST an diesen zurück.

# 

# \---

# 

# \### Mechanical Analysis – Port 8082

# 

# Der Mechanical Analysis Service analysiert unter anderem:

# 

# \- Exhaust System

# \- Mounting System

# \- Power Transmission

# \- Gearbox

# 

# Auch dieser Service übermittelt sein Ergebnis nach Abschluss an den Configuration Service.

# 

# \---

# 

# \### Electrical Analysis – Port 8083

# 

# Der Electrical Analysis Service analysiert unter anderem:

# 

# \- Starting System

# \- Auxiliary PTO

# \- Monitoring \& Control System

# 

# Das Ergebnis wird ebenfalls über einen REST-Callback an den Configuration Service zurückgegeben.

# 

# \---

# 

# \### Engine Management – Port 8084

# 

# Der Engine Management Service wird erst gestartet, wenn die drei vorherigen Teilanalysen abgeschlossen wurden.

# 

# Er erhält:

# 

# \- Ergebnis der Fluid Analysis

# \- Ergebnis der Mechanical Analysis

# \- Ergebnis der Electrical Analysis

# \- Konfiguration des Engine Management Systems

# 

# Anschließend wird daraus ein Gesamtergebnis erzeugt.

# 

# \---

# 

# \## Vereinfachte Architektur

# 

# ```text

# &#x20;                        Benutzer

# &#x20;                           |

# &#x20;                           v

# &#x20;                   +----------------+

# &#x20;                   |   Weboberfläche |

# &#x20;                   | HTML / CSS / JS |

# &#x20;                   +-------+--------+

# &#x20;                           |

# &#x20;                           v

# &#x20;                +----------------------+

# &#x20;                | Configuration Service|

# &#x20;                |       :8080          |

# &#x20;                |                      |

# &#x20;                |  H2 Database         |

# &#x20;                |  Orchestrierung      |

# &#x20;                |  Circuit Breaker     |

# &#x20;                +----------+-----------+

# &#x20;                           |

# &#x20;            +--------------+--------------+

# &#x20;            |              |              |

# &#x20;            v              v              v

# &#x20;     +-------------+ +-------------+ +-------------+

# &#x20;     |    Fluid    | | Mechanical  | | Electrical  |

# &#x20;     |    :8081    | |    :8082    | |    :8083    |

# &#x20;     +------+------+ +------+------+ +------+------+

# &#x20;            |              |              |

# &#x20;            +--------------+--------------+

# &#x20;                           |

# &#x20;                   Ergebnisse zurück

# &#x20;                           |

# &#x20;                           v

# &#x20;                +----------------------+

# &#x20;                | Configuration Service|

# &#x20;                +----------+-----------+

# &#x20;                           |

# &#x20;                           v

# &#x20;                +----------------------+

# &#x20;                |  Engine Management   |

# &#x20;                |        :8084         |

# &#x20;                +----------+-----------+

# &#x20;                           |

# &#x20;                           v

# &#x20;                    Gesamtergebnis

# ```

# 

# \---

# 

# \## Ablauf einer Analyse

# 

# Der Benutzer erstellt zunächst über die Weboberfläche eine Motorkonfiguration.

# 

# Danach läuft die Analyse folgendermaßen ab:

# 

# 1\. Die Konfiguration wird im Configuration Service gespeichert.

# 2\. Der Benutzer startet eine neue Analyse.

# 3\. Der Configuration Service erstellt einen neuen Analyse-Datensatz.

# 4\. Fluid Analysis, Mechanical Analysis und Electrical Analysis werden parallel gestartet.

# 5\. Jeder Analyse-Service führt seine eigene Prüfung durch.

# 6\. Nach Abschluss sendet jeder Service seinen Status und sein Ergebnis an den Configuration Service zurück.

# 7\. Der Configuration Service speichert die eingehenden Ergebnisse.

# 8\. Sobald alle drei Teilanalysen abgeschlossen sind, wird Engine Management gestartet.

# 9\. Engine Management verarbeitet die drei Teilergebnisse.

# 10\. Das Gesamtergebnis wird gespeichert.

# 11\. Die Weboberfläche zeigt die Statuswerte und Ergebnisse an.

# 

# Die drei ersten Analyse-Services werden parallel ausgeführt, damit nicht auf das Ende eines Services gewartet werden muss, bevor die nächste Analyse beginnt.

# 

# \---

# 

# \## Weboberfläche

# 

# Die Weboberfläche wurde mit:

# 

# \- HTML

# \- CSS

# \- JavaScript

# 

# umgesetzt.

# 

# Der Benutzer kann dort:

# 

# \- eine Motorkonfiguration erstellen

# \- die Konfiguration speichern

# \- eine Analyse starten

# \- den Status der einzelnen Services beobachten

# \- die Analyseergebnisse anzeigen

# \- das Gesamtergebnis anzeigen

# 

# Während eine Analyse läuft, werden Ladeindikatoren angezeigt.

# 

# Die Weboberfläche fragt den aktuellen Analysezustand regelmäßig beim Configuration Service ab.

# 

# \---

# 

# \# Fachliche und technische Fehler

# 

# Ein wichtiger Bestandteil des Projekts ist die Unterscheidung zwischen \*\*fachlichen Fehlern\*\* und \*\*technischen Fehlern\*\*.

# 

# Diese beiden Fehlerarten werden bewusst unterschiedlich behandelt.

# 

# \---

# 

# \## Fachlicher Fehler

# 

# Ein fachlicher Fehler bedeutet:

# 

# > Der Analyse-Service funktioniert technisch korrekt, erkennt aber eine fachlich ungültige Motorkonfiguration.

# 

# Der Service ist also erreichbar und konnte die Analyse vollständig durchführen.

# 

# Deshalb bleibt der technische Status:

# 

# ```text

# ready

# ```

# 

# Das Ergebnis der fachlichen Prüfung kann trotzdem `failed` enthalten.

# 

# \---

# 

# \### Beispiel Fluid Analysis

# 

# Folgende Kombination wird als fachlich ungültig bewertet:

# 

# ```text

# Fuel System: high-pressure

# Cooling System: water

# ```

# 

# Das Ergebnis sieht beispielsweise so aus:

# 

# ```text

# Status: ready

# Result: oil=ok; fuel=ok; cooling=failed

# ```

# 

# `ready` bedeutet:

# 

# > Die Analyse wurde technisch erfolgreich durchgeführt.

# 

# `cooling=failed` bedeutet:

# 

# > Die fachliche Prüfung hat eine ungültige Kombination erkannt.

# 

# \---

# 

# \### Beispiel Mechanical Analysis

# 

# Folgende Kombination erzeugt einen fachlichen Fehler:

# 

# ```text

# Power Transmission: hybrid

# Gearbox: manual

# ```

# 

# Der Service bleibt technisch erfolgreich:

# 

# ```text

# Status: ready

# ```

# 

# Im Ergebnis wird jedoch der entsprechende Teil als fehlerhaft bewertet:

# 

# ```text

# gearbox=failed

# ```

# 

# \---

# 

# \### Beispiel Electrical Analysis

# 

# Folgende Kombination erzeugt einen fachlichen Fehler:

# 

# ```text

# Auxiliary PTO: heavy-duty

# Monitoring \& Control: standard

# ```

# 

# Auch hier gilt:

# 

# ```text

# Status: ready

# ```

# 

# Das fachliche Ergebnis enthält jedoch:

# 

# ```text

# monitoring=failed

# ```

# 

# \---

# 

# \## Gesamtergebnis bei einem fachlichen Fehler

# 

# Engine Management erhält die Ergebnisse der drei Analyse-Services.

# 

# Wenn mindestens eines der Ergebnisse `failed` enthält, wird das Gesamtergebnis ebenfalls:

# 

# ```text

# failed

# ```

# 

# Der technische Status von Engine Management kann dabei trotzdem:

# 

# ```text

# ready

# ```

# 

# sein.

# 

# Das bedeutet:

# 

# > Engine Management wurde erfolgreich ausgeführt, aber das fachliche Gesamtergebnis ist negativ.

# 

# \---

# 

# \# Technischer Fehler

# 

# Ein technischer Fehler liegt vor, wenn ein Service seine Analyse gar nicht durchführen kann.

# 

# Beispiele:

# 

# \- Service wurde gestoppt

# \- Container ist nicht erreichbar

# \- REST-Verbindung schlägt fehl

# \- Service antwortet nicht korrekt

# 

# In diesem Fall ist die Analyse nicht fachlich fehlgeschlagen, sondern technisch.

# 

# Beispiel:

# 

# ```text

# Mechanical Analysis Service ist nicht erreichbar

# ```

# 

# Der technische Status wird dann:

# 

# ```text

# failed

# ```

# 

# Das reguläre Analyseergebnis kann nicht erzeugt werden.

# 

# Die Weboberfläche zeigt beispielsweise:

# 

# ```text

# Technischer Fehler – Analyse konnte nicht durchgeführt werden

# ```

# 

# \---

# 

# \## Unterschied zwischen fachlichem und technischem Fehler

# 

# ```text

# Fachlicher Fehler:

# 

# Service erreichbar

# &#x20;       |

# &#x20;       v

# Analyse wird durchgeführt

# &#x20;       |

# &#x20;       v

# Status = ready

# &#x20;       |

# &#x20;       v

# Result enthält failed

# ```

# 

# ```text

# Technischer Fehler:

# 

# Service nicht erreichbar

# &#x20;       |

# &#x20;       v

# REST-Aufruf schlägt fehl

# &#x20;       |

# &#x20;       v

# Fallback wird ausgeführt

# &#x20;       |

# &#x20;       v

# Status = failed

# ```

# 

# Der Circuit Breaker reagiert auf \*\*technische Fehler\*\*, nicht auf fachlich ungültige Ergebnisse.

# 

# \---

# 

# \# Circuit Breaker

# 

# Für die REST-Kommunikation zwischen dem Configuration Service und den Analyse-Services wird das \*\*Circuit Breaker Pattern\*\* verwendet.

# 

# Die Implementierung erfolgt mit:

# 

# ```text

# Resilience4j

# ```

# 

# Der Circuit Breaker soll verhindern, dass ein nicht erreichbarer Service immer wieder aufgerufen wird.

# 

# Ohne Circuit Breaker könnte der Configuration Service kontinuierlich versuchen, einen ausgefallenen Service zu erreichen.

# 

# Das würde:

# 

# \- unnötige Netzwerkaufrufe erzeugen

# \- Ressourcen verbrauchen

# \- Wartezeiten verursachen

# \- weitere Teile des Systems beeinträchtigen

# 

# Der Circuit Breaker erkennt wiederholte technische Fehler und unterbricht anschließend vorübergehend die Kommunikation mit dem betroffenen Service.

# 

# \---

# 

# \## Zustände des Circuit Breakers

# 

# Der Circuit Breaker besitzt drei zentrale Zustände:

# 

# ```text

# CLOSED

# OPEN

# HALF-OPEN

# ```

# 

# \---

# 

# \### CLOSED

# 

# `CLOSED` ist der normale Zustand.

# 

# REST-Aufrufe werden an den Service weitergeleitet.

# 

# ```text

# Configuration Service

# &#x20;       |

# &#x20;       v

# Circuit Breaker: CLOSED

# &#x20;       |

# &#x20;       v

# Mechanical Analysis

# ```

# 

# Erfolgreiche und fehlgeschlagene Aufrufe werden registriert.

# 

# Wenn die konfigurierte Fehlerschwelle erreicht wird, wechselt der Circuit Breaker zu:

# 

# ```text

# OPEN

# ```

# 

# \---

# 

# \### OPEN

# 

# Im Zustand `OPEN` werden weitere Aufrufe nicht mehr an den ausgefallenen Service geschickt.

# 

# ```text

# Configuration Service

# &#x20;       |

# &#x20;       v

# Circuit Breaker: OPEN

# &#x20;       X

# &#x20;       |

# Mechanical Analysis

# ```

# 

# Stattdessen wird direkt der konfigurierte Fallback ausgeführt.

# 

# Dadurch wird ein bereits als fehlerhaft erkannter Service nicht permanent erneut kontaktiert.

# 

# \---

# 

# \### HALF-OPEN

# 

# Nach einer definierten Wartezeit wechselt der Circuit Breaker von:

# 

# ```text

# OPEN

# ```

# 

# zu:

# 

# ```text

# HALF-OPEN

# ```

# 

# Jetzt wird eine begrenzte Anzahl von Testaufrufen zugelassen.

# 

# Wenn der Service wieder funktioniert:

# 

# ```text

# HALF-OPEN

# &#x20;   |

# &#x20;   | erfolgreicher Aufruf

# &#x20;   v

# CLOSED

# ```

# 

# Wenn der Service weiterhin nicht erreichbar ist:

# 

# ```text

# HALF-OPEN

# &#x20;   |

# &#x20;   | Fehler

# &#x20;   v

# OPEN

# ```

# 

# Dadurch kann sich das System automatisch von einem temporären Service-Ausfall erholen.

# 

# \---

# 

# \## Circuit-Breaker-Konfiguration

# 

# Für die Analyse-Services werden getrennte Circuit Breaker verwendet.

# 

# Unter anderem sind folgende Werte konfiguriert:

# 

# ```text

# Sliding Window Size: 2

# Minimum Number of Calls: 2

# Failure Rate Threshold: 100 %

# Wait Duration in Open State: 30 Sekunden

# Permitted Calls in Half-Open State: 1

# ```

# 

# Das bedeutet beispielsweise:

# 

# Nach zwei fehlgeschlagenen Aufrufen und einer Fehlerrate von 100 % kann der Circuit Breaker geöffnet werden.

# 

# Nach 30 Sekunden darf ein Testaufruf durchgeführt werden.

# 

# \---

# 

# \## Fallback

# 

# Wenn ein technischer REST-Aufruf fehlschlägt, wird ein Fallback ausgeführt.

# 

# Dieser sorgt dafür, dass die gesamte Anwendung nicht abstürzt.

# 

# Stattdessen wird der betroffene Analyse-Service beispielsweise mit folgendem Status gespeichert:

# 

# ```text

# failed

# ```

# 

# Die Anwendung kann den Fehler anschließend kontrolliert an die Weboberfläche weitergeben.

# 

# \---

# 

# \# Persistenz

# 

# Die zentrale Persistenz befindet sich im Configuration Service.

# 

# Verwendet werden:

# 

# ```text

# Spring Data JPA

# H2 Database

# ```

# 

# Gespeichert werden unter anderem:

# 

# \- Motorkonfigurationen

# \- Analyse-Datensätze

# \- Status der einzelnen Teilanalysen

# \- Ergebnisse der Teilanalysen

# \- Engine-Management-Ergebnis

# \- Gesamtergebnis

# 

# Die einzelnen Analyse-Services besitzen keine eigene Datenbank.

# 

# \---

# 

# \# REST-Kommunikation

# 

# Die Services kommunizieren über REST.

# 

# Der Configuration Service startet die Analysen durch REST-Aufrufe an die jeweiligen Analyse-Services.

# 

# Nach Abschluss einer Analyse senden die Services ihre Ergebnisse über einen Callback wieder an den Configuration Service zurück.

# 

# Vereinfacht:

# 

# ```text

# Configuration

# &#x20;     |

# &#x20;     | POST

# &#x20;     v

# Fluid Analysis

# &#x20;     |

# &#x20;     | Analyse durchführen

# &#x20;     |

# &#x20;     | POST Callback

# &#x20;     v

# Configuration

# ```

# 

# Dadurch kennt der Configuration Service jederzeit den aktuellen Zustand der gesamten Analyse.

# 

# \---

# 

# \# Parallele Verarbeitung

# 

# Fluid Analysis, Mechanical Analysis und Electrical Analysis sind voneinander unabhängig.

# 

# Deshalb werden sie parallel gestartet.

# 

# Vereinfacht:

# 

# ```text

# &#x20;                      +--> Fluid

# &#x20;                      |

# Configuration ---------+--> Mechanical

# &#x20;                      |

# &#x20;                      +--> Electrical

# ```

# 

# Erst wenn alle drei Teilanalysen abgeschlossen wurden:

# 

# ```text

# Fluid       = fertig

# Mechanical  = fertig

# Electrical  = fertig

# ```

# 

# wird:

# 

# ```text

# Engine Management

# ```

# 

# gestartet.

# 

# \---

# 

# \# Docker

# 

# Jeder Microservice wird in einem eigenen Docker-Container ausgeführt.

# 

# Die fünf Container sind:

# 

# ```text

# configuration

# fluid-analysis

# mechanical-analysis

# electrical-analysis

# engine-management

# ```

# 

# Damit wird die Trennung der Microservices nicht nur im Sourcecode, sondern auch zur Laufzeit umgesetzt.

# 

# Ein einzelner Service kann beispielsweise unabhängig gestoppt und wieder gestartet werden.

# 

# Das ist unter anderem hilfreich, um technische Fehler und den Circuit Breaker zu testen.

# 

# \---

# 

# \## Kommunikation innerhalb von Docker

# 

# Innerhalb eines Docker-Compose-Netzwerks kommunizieren die Container über ihre Service-Namen.

# 

# Beispiele:

# 

# ```text

# http://fluid-analysis:8081

# http://mechanical-analysis:8082

# http://electrical-analysis:8083

# http://engine-management:8084

# ```

# 

# Innerhalb eines Containers würde `localhost` immer auf den Container selbst zeigen.

# 

# Deshalb werden für die Kommunikation zwischen verschiedenen Containern die jeweiligen Service-Namen verwendet.

# 

# \---

# 

# \# Docker Multi-Stage Build

# 

# Die Dockerfiles verwenden einen \*\*Multi-Stage Build\*\*.

# 

# Dadurch muss vor dem Start keine JAR-Datei manuell erzeugt werden.

# 

# Docker baut die Anwendung selbst direkt aus dem Sourcecode.

# 

# \---

# 

# \## Build-Phase

# 

# In der ersten Phase wird Maven verwendet:

# 

# ```dockerfile

# FROM maven:3.9-eclipse-temurin-17 AS build

# 

# WORKDIR /app

# 

# COPY pom.xml .

# COPY src ./src

# 

# RUN mvn clean package -DskipTests

# ```

# 

# Dabei wird:

# 

# ```text

# Sourcecode

# &#x20;   |

# &#x20;   v

# Maven

# &#x20;   |

# &#x20;   v

# JAR-Datei

# ```

# 

# erzeugt.

# 

# \---

# 

# \## Runtime-Phase

# 

# Anschließend wird ein separates Java-Runtime-Image verwendet:

# 

# ```dockerfile

# FROM eclipse-temurin:17-jre

# 

# WORKDIR /app

# 

# COPY --from=build /app/target/\*.jar app.jar

# 

# ENTRYPOINT \["java", "-jar", "app.jar"]

# ```

# 

# Nur die fertige Anwendung wird in das endgültige Image übernommen.

# 

# Dadurch muss ein Benutzer des Repositorys Maven nicht separat installieren und auch keine JAR-Dateien vorher erzeugen.

# 

# \---

# 

# \# Docker Compose

# 

# Docker Compose startet und verbindet alle fünf Services.

# 

# Die zentrale Datei dafür ist:

# 

# ```text

# docker-compose.yaml

# ```

# 

# Sie definiert unter anderem:

# 

# \- die fünf Services

# \- die Dockerfiles

# \- die verwendeten Ports

# \- die Service-Adressen

# \- die Umgebungsvariablen

# 

# \---

# 

# \# Anwendung starten

# 

# \## Voraussetzungen

# 

# Benötigt werden:

# 

# \- Docker Desktop

# \- Docker Compose

# 

# Nach dem Klonen des Repositorys kann das Projekt direkt aus dem Hauptverzeichnis gestartet werden.

# 

# ```bash

# docker compose up --build

# ```

# 

# Docker:

# 

# 1\. baut alle fünf Services

# 2\. erstellt die Images

# 3\. startet die Container

# 4\. verbindet die Services über das Docker-Netzwerk

# 

# \---

# 

# \## Anwendung im Hintergrund starten

# 

# Alternativ:

# 

# ```bash

# docker compose up --build -d

# ```

# 

# Mit `-d` werden die Container im Hintergrund gestartet.

# 

# \---

# 

# \## Containerstatus prüfen

# 

# ```bash

# docker compose ps

# ```

# 

# \---

# 

# \## Weboberfläche öffnen

# 

# Nach erfolgreichem Start ist die Anwendung erreichbar unter:

# 

# ```text

# http://localhost:8080

# ```

# 

# \---

# 

# \# Anwendung beenden

# 

# ```bash

# docker compose down

# ```

# 

# Dadurch werden die Container und das von Docker Compose erzeugte Netzwerk beendet.

# 

# \---

# 

# \# Circuit Breaker testen

# 

# Ein einzelner Analyse-Service kann gezielt gestoppt werden.

# 

# Beispiel:

# 

# ```bash

# docker compose stop mechanical-analysis

# ```

# 

# Danach kann über die Weboberfläche eine neue Analyse gestartet werden.

# 

# Der Mechanical Analysis Service ist nun nicht erreichbar.

# 

# Der Configuration Service erkennt den technischen Fehler und führt den entsprechenden Fallback aus.

# 

# Der Service kann anschließend wieder gestartet werden:

# 

# ```bash

# docker compose start mechanical-analysis

# ```

# 

# \---

# 

# \# Fachliche Fehlerfälle testen

# 

# \## Fluid Analysis

# 

# Folgende Kombination erzeugt einen fachlichen Fehler:

# 

# ```text

# Fuel System: high-pressure

# Cooling System: water

# ```

# 

# Erwartung:

# 

# ```text

# Status: ready

# Result: oil=ok; fuel=ok; cooling=failed

# ```

# 

# \---

# 

# \## Mechanical Analysis

# 

# Folgende Kombination erzeugt einen fachlichen Fehler:

# 

# ```text

# Power Transmission: hybrid

# Gearbox: manual

# ```

# 

# Erwartung:

# 

# ```text

# Status: ready

# Result enthält gearbox=failed

# ```

# 

# \---

# 

# \## Electrical Analysis

# 

# Folgende Kombination erzeugt einen fachlichen Fehler:

# 

# ```text

# Auxiliary PTO: heavy-duty

# Monitoring \& Control: standard

# ```

# 

# Erwartung:

# 

# ```text

# Status: ready

# Result enthält monitoring=failed

# ```

# 

# \---

# 

# \# Projektstruktur

# 

# ```text

# .

# ├── configuration/

# │   ├── src/

# │   ├── pom.xml

# │   └── Dockerfile

# │

# ├── fluid-analysis/

# │   ├── src/

# │   ├── pom.xml

# │   └── Dockerfile

# │

# ├── mechanical-analysis/

# │   ├── src/

# │   ├── pom.xml

# │   └── Dockerfile

# │

# ├── electrical-analysis/

# │   ├── src/

# │   ├── pom.xml

# │   └── Dockerfile

# │

# ├── engine-management/

# │   ├── src/

# │   ├── pom.xml

# │   └── Dockerfile

# │

# ├── Dokumentation/

# │

# ├── docker-compose.yaml

# ├── .gitignore

# └── README.md

# ```

# 

# \---

# 

# \# Zentrale Architekturentscheidungen

# 

# Im Projekt wurden unter anderem folgende Entscheidungen getroffen:

# 

# \- Aufteilung der Analyse in mehrere eigenständige Microservices

# \- zentrale Koordination durch den Configuration Service

# \- parallele Ausführung von Fluid, Mechanical und Electrical Analysis

# \- nachgelagerter Start des Engine Management Service

# \- REST-basierte Kommunikation

# \- Callback-basierte Rückmeldung der Analyseergebnisse

# \- zentrale Persistenz im Configuration Service

# \- klare Trennung zwischen fachlichen und technischen Fehlern

# \- Circuit Breaker für technische Service-Ausfälle

# \- Fallback-Mechanismen zur kontrollierten Fehlerbehandlung

# \- Containerisierung jedes Microservices

# \- Docker Compose zur gemeinsamen Ausführung

# \- Multi-Stage-Builds zur reproduzierbaren Erstellung der Services

# 

# \---

# 

# \# Ziel des Projekts

# 

# Das Projekt wurde als \*\*Proof of Concept\*\* entwickelt, um zentrale Konzepte service-basierter Softwarearchitekturen praktisch umzusetzen.

# 

# Im Mittelpunkt standen dabei insbesondere:

# 

# \- Microservices

# \- lose Kopplung

# \- REST-Kommunikation

# \- parallele Verarbeitung

# \- zentrale Orchestrierung

# \- Dependency Injection

# \- Persistenz

# \- Resilience Pattern

# \- Circuit Breaker

# \- Fallback

# \- Docker

# \- Docker Compose

# \- reproduzierbares Deployment

