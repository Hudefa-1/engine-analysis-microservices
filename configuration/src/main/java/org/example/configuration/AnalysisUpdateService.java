package org.example.configuration;

import org.springframework.stereotype.Service;

@Service
public class AnalysisUpdateService {

    private final AnalysisRepository analysisRepository;

    public AnalysisUpdateService(
            AnalysisRepository analysisRepository) {

        this.analysisRepository = analysisRepository;
    }

    public synchronized Analysis update(
            Long analysisId,
            String serviceName,
            String status,
            String result) {

        Analysis analysis = analysisRepository
                .findById(analysisId)
                .orElseThrow();

        switch (serviceName.toLowerCase()) {

            case "fluid" -> {
                analysis.setFluidStatus(status);
                analysis.setFluidResult(result);
            }

            case "mechanical" -> {
                analysis.setMechanicalStatus(status);
                analysis.setMechanicalResult(result);
            }

            case "electrical" -> {
                analysis.setElectricalStatus(status);
                analysis.setElectricalResult(result);
            }

            case "engine-management" -> {
                analysis.setEngineManagementStatus(status);
                analysis.setEngineManagementResult(result);

                if ("ready".equals(status)) {
                    analysis.setOverallResult(result);
                }
            }
        }

        return analysisRepository.save(analysis);
    }


    public synchronized Analysis technicalFailure(
            Long analysisId,
            String serviceName) {

        Analysis analysis = analysisRepository
                .findById(analysisId)
                .orElseThrow();

        switch (serviceName.toLowerCase()) {

            case "fluid" -> {
                analysis.setFluidStatus("failed");
                analysis.setFluidResult(null);
            }

            case "mechanical" -> {
                analysis.setMechanicalStatus("failed");
                analysis.setMechanicalResult(null);
            }

            case "electrical" -> {
                analysis.setElectricalStatus("failed");
                analysis.setElectricalResult(null);
            }

            case "engine-management" -> {
                analysis.setEngineManagementStatus("failed");
                analysis.setEngineManagementResult(null);
            }
        }


        if (!"engine-management".equalsIgnoreCase(serviceName)) {
            analysis.setEngineManagementStatus("failed");
            analysis.setEngineManagementResult(null);
        }

        analysis.setOverallResult("failed");

        return analysisRepository.save(analysis);
    }
}
