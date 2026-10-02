package org.example.configuration;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class ConfigurationController {

    private final ConfigurationRepository configurationRepository;
    private final AnalysisRepository analysisRepository;
    private final AnalysisClientService analysisClientService;
    private final AnalysisUpdateService analysisUpdateService;


    public ConfigurationController(
            ConfigurationRepository configurationRepository,
            AnalysisRepository analysisRepository,
            AnalysisClientService analysisClientService,
            AnalysisUpdateService analysisUpdateService) {

        this.configurationRepository = configurationRepository;
        this.analysisRepository = analysisRepository;
        this.analysisClientService = analysisClientService;
        this.analysisUpdateService = analysisUpdateService;
    }

    @GetMapping("/status")
    public String status() {
        return "Der Configuration Service läuft.";
    }

    @PostMapping("/configurations")
    public Configuration saveConfiguration(
            @RequestBody Configuration configuration) {

        return configurationRepository.save(configuration);
    }

    @GetMapping("/configurations")
    public List<Configuration> getConfigurations() {
        return configurationRepository.findAll();
    }

    @PostMapping("/configurations/{configurationId}/analysis")
    public Analysis createAnalysis(
            @PathVariable Long configurationId) {

        Configuration configuration =
                configurationRepository.findById(configurationId)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Konfiguration wurde nicht gefunden."
                        ));

        Analysis analysis = new Analysis();
        analysis.setConfigurationId(configurationId);

        Analysis savedAnalysis =
                analysisRepository.save(analysis);

        Long analysisId = savedAnalysis.getId();

        FluidAnalysisRequest fluidRequest =
                new FluidAnalysisRequest(
                        analysisId,
                        configuration.getOilSystem(),
                        configuration.getFuelSystem(),
                        configuration.getCoolingSystem()
                );

        MechanicalAnalysisRequest mechanicalRequest =
                new MechanicalAnalysisRequest(
                        analysisId,
                        configuration.getExhaustSystem(),
                        configuration.getMountingSystem(),
                        configuration.getPowerTransmission(),
                        configuration.getGearboxOptions()
                );

        ElectricalAnalysisRequest electricalRequest =
                new ElectricalAnalysisRequest(
                        analysisId,
                        configuration.getStartingSystem(),
                        configuration.getAuxiliaryPto(),
                        configuration.getMonitoringControlSystem()
                );

        CompletableFuture.runAsync(
                () -> analysisClientService
                        .startFluidAnalysis(fluidRequest)
        );

        CompletableFuture.runAsync(
                () -> analysisClientService
                        .startMechanicalAnalysis(mechanicalRequest)
        );

        CompletableFuture.runAsync(
                () -> analysisClientService
                        .startElectricalAnalysis(electricalRequest)
        );

        return savedAnalysis;
    }

    @GetMapping("/analysis/{analysisId}")
    public Analysis getAnalysis(
            @PathVariable Long analysisId) {

        return analysisRepository.findById(analysisId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Analyse wurde nicht gefunden."
                ));
    }

    @PostMapping("/analysis/{analysisId}/update")
    public synchronized Analysis updateAnalysis(
            @PathVariable Long analysisId,
            @RequestBody AnalysisUpdate update) {

        Analysis savedAnalysis =
                analysisUpdateService.update(
                        analysisId,
                        update.serviceName(),
                        update.status(),
                        update.result()
                );

        boolean fluidDone =
                "ready".equals(savedAnalysis.getFluidStatus())
                        || "failed".equals(savedAnalysis.getFluidStatus());

        boolean mechanicalDone =
                "ready".equals(savedAnalysis.getMechanicalStatus())
                        || "failed".equals(savedAnalysis.getMechanicalStatus());

        boolean electricalDone =
                "ready".equals(savedAnalysis.getElectricalStatus())
                        || "failed".equals(savedAnalysis.getElectricalStatus());

        boolean engineManagementNotStarted =
                "waiting".equals(savedAnalysis.getEngineManagementStatus());

        if (fluidDone
                && mechanicalDone
                && electricalDone
                && engineManagementNotStarted) {

            Configuration configuration =
                    configurationRepository
                            .findById(savedAnalysis.getConfigurationId())
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Konfiguration wurde nicht gefunden."
                            ));

            savedAnalysis = analysisUpdateService.update(
                    savedAnalysis.getId(),
                    "engine-management",
                    "running",
                    null
            );

            EngineManagementRequest request =
                    new EngineManagementRequest(
                            savedAnalysis.getId(),
                            configuration.getEngineManagementSystem(),
                            savedAnalysis.getFluidResult(),
                            savedAnalysis.getMechanicalResult(),
                            savedAnalysis.getElectricalResult()
                    );

            CompletableFuture.runAsync(
                    () -> analysisClientService
                            .startEngineManagementAnalysis(request)
            );
        }

        return savedAnalysis;
    }

    public record FluidAnalysisRequest(
            Long analysisId,
            String oilSystem,
            String fuelSystem,
            String coolingSystem
    ) {
    }

    public record MechanicalAnalysisRequest(
            Long analysisId,
            String exhaustSystem,
            String mountingSystem,
            String powerTransmission,
            String gearboxOptions
    ) {
    }

    public record ElectricalAnalysisRequest(
            Long analysisId,
            String startingSystem,
            String auxiliaryPto,
            String monitoringControlSystem
    ) {
    }

    public record EngineManagementRequest(
            Long analysisId,
            String engineManagementSystem,
            String fluidResult,
            String mechanicalResult,
            String electricalResult
    ) {
    }
}