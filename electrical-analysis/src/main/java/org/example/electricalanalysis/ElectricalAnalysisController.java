package org.example.electricalanalysis;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

@RestController
public class ElectricalAnalysisController {

    private final RestClient configurationClient;

    public ElectricalAnalysisController(
            @Value("${configuration.service.url}")
            String configurationServiceUrl) {

        this.configurationClient =
                RestClient.create(configurationServiceUrl);
    }

    @GetMapping("/status")
    public String status() {
        return "Der Electrical Analysis Service läuft.";
    }

    @PostMapping("/analysis")
    public ElectricalAnalysisResult analyse(
            @RequestBody ElectricalAnalysisRequest request)
            throws InterruptedException {

        sendUpdate(request.analysisId(), "running", null);

        // Simuliert die Analyse
        Thread.sleep(20000);

        String startingResult = "ok";
        String auxiliaryPtoResult = "ok";
        String monitoringResult = "ok";

        if ("heavy-duty".equalsIgnoreCase(request.auxiliaryPto())
                && "standard".equalsIgnoreCase(request.monitoringControlSystem())) {

            monitoringResult = "failed";
        }

        String completeResult =
                "starting=" + startingResult
                        + "; auxiliaryPto=" + auxiliaryPtoResult
                        + "; monitoring=" + monitoringResult;

        String status = "ready";

        sendUpdate(
                request.analysisId(),
                status,
                completeResult
        );

        return new ElectricalAnalysisResult(
                request.analysisId(),
                status,
                startingResult,
                auxiliaryPtoResult,
                monitoringResult
        );
    }

    private void sendUpdate(
            Long analysisId,
            String status,
            String result) {

        AnalysisUpdate update = new AnalysisUpdate(
                "electrical",
                status,
                result
        );

        configurationClient.post()
                .uri("/analysis/{analysisId}/update", analysisId)
                .body(update)
                .retrieve()
                .toBodilessEntity();
    }

    public record ElectricalAnalysisRequest(
            Long analysisId,
            String startingSystem,
            String auxiliaryPto,
            String monitoringControlSystem
    ) {
    }

    public record ElectricalAnalysisResult(
            Long analysisId,
            String status,
            String startingSystemResult,
            String auxiliaryPtoResult,
            String monitoringControlSystemResult
    ) {
    }

    public record AnalysisUpdate(
            String serviceName,
            String status,
            String result
    ) {
    }
}
