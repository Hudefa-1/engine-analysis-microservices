package org.example.fluidanalysis;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

@RestController
public class FluidAnalysisController {

    private final RestClient configurationClient;

    public FluidAnalysisController(
            @Value("${configuration.service.url}")
            String configurationServiceUrl) {

        this.configurationClient =
                RestClient.create(configurationServiceUrl);
    }



    @GetMapping("/status")
    public String status() {
        return "Der Fluid Analysis Service läuft.";
    }

    @PostMapping("/analysis")
    public FluidAnalysisResult analyse(
            @RequestBody FluidAnalysisRequest request)
            throws InterruptedException {

        // Meldet dem Configuration Service:
        // Die Analyse wurde gestartet.
        sendUpdate(
                request.analysisId(),
                "running",
                null
        );

        // Simuliert einen zeitaufwendigen Algorithmus.
        Thread.sleep(20000);

        // Simulierte Ergebnisse der drei Prüfungen.
        String oilResult = "ok";
        String fuelResult = "ok";
        String coolingResult = "ok";

        if ("high-pressure".equalsIgnoreCase(request.fuelSystem())
                && "water".equalsIgnoreCase(request.coolingSystem())) {

            coolingResult = "failed";
        }

        String completeResult =
                "oil=" + oilResult
                        + "; fuel=" + fuelResult
                        + "; cooling=" + coolingResult;

        String status = "ready";

        sendUpdate(
                request.analysisId(),
                status,
                completeResult
        );

        return new FluidAnalysisResult(
                request.analysisId(),
                status,
                oilResult,
                fuelResult,
                coolingResult
        );
    }

    private void sendUpdate(
            Long analysisId,
            String status,
            String result) {

        AnalysisUpdate update = new AnalysisUpdate(
                "fluid",
                status,
                result
        );

        configurationClient.post()
                .uri("/analysis/{analysisId}/update", analysisId)
                .body(update)
                .retrieve()
                .toBodilessEntity();
    }

    public record FluidAnalysisRequest(
            Long analysisId,
            String oilSystem,
            String fuelSystem,
            String coolingSystem
    ) {
    }

    public record FluidAnalysisResult(
            Long analysisId,
            String status,
            String oilSystemResult,
            String fuelSystemResult,
            String coolingSystemResult
    ) {
    }

    public record AnalysisUpdate(
            String serviceName,
            String status,
            String result
    ) {
    }
}
