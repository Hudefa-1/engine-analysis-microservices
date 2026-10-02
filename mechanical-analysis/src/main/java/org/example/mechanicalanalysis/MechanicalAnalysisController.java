package org.example.mechanicalanalysis;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

@RestController
public class MechanicalAnalysisController {

    private final RestClient configurationClient;

    public MechanicalAnalysisController(
            @Value("${configuration.service.url}")
            String configurationServiceUrl) {

        this.configurationClient =
                RestClient.create(configurationServiceUrl);
    }

    @GetMapping("/status")
    public String status() {
        return "Der Mechanical Analysis Service läuft.";
    }

    @PostMapping("/analysis")
    public MechanicalAnalysisResult analyse(
            @RequestBody MechanicalAnalysisRequest request)
            throws InterruptedException {

        sendUpdate(request.analysisId(), "running", null);

        Thread.sleep(20000);

        String exhaustResult = "ok";
        String mountingResult = "ok";
        String powerTransmissionResult = "ok";
        String gearboxResult = "ok";

        if ("hybrid".equalsIgnoreCase(request.powerTransmission())
                && "manual".equalsIgnoreCase(request.gearboxOptions())) {

            gearboxResult = "failed";
        }

        String completeResult =
                "exhaust=" + exhaustResult
                        + "; mounting=" + mountingResult
                        + "; powerTransmission=" + powerTransmissionResult
                        + "; gearbox=" + gearboxResult;

        String status = "ready";

        sendUpdate(
                request.analysisId(),
                status,
                completeResult
        );

        return new MechanicalAnalysisResult(
                request.analysisId(),
                status,
                exhaustResult,
                mountingResult,
                powerTransmissionResult,
                gearboxResult
        );
    }

    private void sendUpdate(
            Long analysisId,
            String status,
            String result) {

        AnalysisUpdate update = new AnalysisUpdate(
                "mechanical",
                status,
                result
        );

        configurationClient.post()
                .uri("/analysis/{analysisId}/update", analysisId)
                .body(update)
                .retrieve()
                .toBodilessEntity();
    }

    public record MechanicalAnalysisRequest(
            Long analysisId,
            String exhaustSystem,
            String mountingSystem,
            String powerTransmission,
            String gearboxOptions
    ) {
    }

    public record MechanicalAnalysisResult(
            Long analysisId,
            String status,
            String exhaustSystemResult,
            String mountingSystemResult,
            String powerTransmissionResult,
            String gearboxOptionsResult
    ) {
    }

    public record AnalysisUpdate(
            String serviceName,
            String status,
            String result
    ) {
    }
}
