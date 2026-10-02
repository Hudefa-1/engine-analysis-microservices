package org.example.configuration;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

@Service
public class AnalysisClientService {

    private final AnalysisUpdateService analysisUpdateService;

    private final RestClient fluidClient;
    private final RestClient mechanicalClient;
    private final RestClient electricalClient;
    private final RestClient engineManagementClient;

    public AnalysisClientService(
            AnalysisUpdateService analysisUpdateService,

            @Value("${fluid.service.url}")
            String fluidServiceUrl,

            @Value("${mechanical.service.url}")
            String mechanicalServiceUrl,

            @Value("${electrical.service.url}")
            String electricalServiceUrl,

            @Value("${engine-management.service.url}")
            String engineManagementServiceUrl) {

        this.analysisUpdateService = analysisUpdateService;

        this.fluidClient =
                RestClient.create(fluidServiceUrl);

        this.mechanicalClient =
                RestClient.create(mechanicalServiceUrl);

        this.electricalClient =
                RestClient.create(electricalServiceUrl);

        this.engineManagementClient =
                RestClient.create(engineManagementServiceUrl);
    }


    @CircuitBreaker(name = "fluidService", fallbackMethod = "fluidFallback")
    public void startFluidAnalysis(ConfigurationController.FluidAnalysisRequest request) {

        fluidClient.post()
                .uri("/analysis")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
    private void fluidFallback(
            ConfigurationController.FluidAnalysisRequest request,
            Throwable throwable) {

        analysisUpdateService.technicalFailure(
                request.analysisId(),
                "fluid"
        );

        System.out.println(
                "Fluid Service nicht erreichbar: "
                        + throwable.getMessage()
        );
    }

    @CircuitBreaker(
            name = "mechanicalService",
            fallbackMethod = "mechanicalFallback"
    )
    public void startMechanicalAnalysis(
            ConfigurationController.MechanicalAnalysisRequest request) {

        mechanicalClient.post()
                .uri("/analysis")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    private void mechanicalFallback(
            ConfigurationController.MechanicalAnalysisRequest request,
            Throwable throwable) {

        analysisUpdateService.technicalFailure(
                request.analysisId(),
                "mechanical"
        );

        System.out.println(
                "Mechanical Service nicht erreichbar: "
                        + throwable.getMessage()
        );
    }


    // ---------------- ELECTRICAL ----------------

    @CircuitBreaker(
            name = "electricalService",
            fallbackMethod = "electricalFallback"
    )
    public void startElectricalAnalysis(
            ConfigurationController.ElectricalAnalysisRequest request) {

        electricalClient.post()
                .uri("/analysis")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    private void electricalFallback(
            ConfigurationController.ElectricalAnalysisRequest request,
            Throwable throwable) {

        analysisUpdateService.technicalFailure(
                request.analysisId(),
                "electrical"
        );

        System.out.println(
                "Electrical Service nicht erreichbar: "
                        + throwable.getMessage()
        );
    }


    // ---------------- ENGINE MANAGEMENT ----------------

    @CircuitBreaker(
            name = "engineManagementService",
            fallbackMethod = "engineManagementFallback"
    )
    public void startEngineManagementAnalysis(
            ConfigurationController.EngineManagementRequest request) {

        engineManagementClient.post()
                .uri("/analysis")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    private void engineManagementFallback(
            ConfigurationController.EngineManagementRequest request,
            Throwable throwable) {

        analysisUpdateService.technicalFailure(
                request.analysisId(),
                "engine-management"
        );

        System.out.println(
                "Engine Management Service nicht erreichbar: "
                        + throwable.getMessage()
        );
    }

}
