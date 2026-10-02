package org.example.configuration;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisRepository
        extends JpaRepository<Analysis, Long> {
}