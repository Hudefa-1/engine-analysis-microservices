package org.example.configuration;

public record AnalysisUpdate(
        String serviceName,
        String status,
        String result
) {
}
