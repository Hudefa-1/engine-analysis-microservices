package org.example.enginemanagement;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

@RestController
public class EngineManagementController {

    private final RestClient configurationClient;

    public EngineManagementController(
            @Value("${configuration.service.url}")
            String configurationServiceUrl) {

        this.configurationClient =
                RestClient.create(configurationServiceUrl);
    }

    @GetMapping("/status")
    public String status() {
        return "Der Engine Management Service läuft.";
    }

    @PostMapping("/analysis")
    public EngineManagementResult analyse(
            @RequestBody EngineManagementRequest request)
            throws InterruptedException {

        sendUpdate(request.analysisId(), "running", null);

        // Simuliert den Analysealgorithmus
        Thread.sleep(15000);

        String result;

        if (request.fluidResult().contains("failed")
                || request.mechanicalResult().contains("failed")
                || request.electricalResult().contains("failed")) {

            result = "failed";
        } else {
            result = "ok";
        }

        sendUpdate(request.analysisId(), "ready", result);

        return new EngineManagementResult(
                request.analysisId(),
                "ready",
                result
        );
    }

    private void sendUpdate(
            Long analysisId,
            String status,
            String result) {

        AnalysisUpdate update = new AnalysisUpdate(
                "engine-management",
                status,
                result
        );

        configurationClient.post()
                .uri("/analysis/{analysisId}/update", analysisId)
                .body(update)
                .retrieve()
                .toBodilessEntity();
    }

    public record EngineManagementRequest(
            Long analysisId,
            String engineManagementSystem,
            String fluidResult,
            String mechanicalResult,
            String electricalResult
    ) {
    }

    public record EngineManagementResult(
            Long analysisId,
            String status,
            String result
    ) {
    }

    public record AnalysisUpdate(
            String serviceName,
            String status,
            String result
    ) {
    }
}
