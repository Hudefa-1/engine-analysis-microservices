package org.example.configuration;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Analysis")
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long configurationId;

    private String fluidStatus = "waiting";
    private String mechanicalStatus = "waiting";
    private String electricalStatus = "waiting";
    private String engineManagementStatus = "waiting";

    private String fluidResult;
    private String mechanicalResult;
    private String electricalResult;
    private String engineManagementResult;

    private String overallResult = "pending";



    public Analysis() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFluidStatus() {
        return fluidStatus;
    }

    public void setFluidStatus(String fluidStatus) {
        this.fluidStatus = fluidStatus;
    }

    public Long getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(Long configurationId) {
        this.configurationId = configurationId;
    }

    public String getMechanicalStatus() {
        return mechanicalStatus;
    }

    public void setMechanicalStatus(String mechanicalStatus) {
        this.mechanicalStatus = mechanicalStatus;
    }

    public String getElectricalStatus() {
        return electricalStatus;
    }

    public void setElectricalStatus(String electricalStatus) {
        this.electricalStatus = electricalStatus;
    }

    public String getEngineManagementStatus() {
        return engineManagementStatus;
    }

    public void setEngineManagementStatus(String engineManagementStatus) {
        this.engineManagementStatus = engineManagementStatus;
    }

    public String getFluidResult() {
        return fluidResult;
    }

    public void setFluidResult(String fluidResult) {
        this.fluidResult = fluidResult;
    }

    public String getMechanicalResult() {
        return mechanicalResult;
    }

    public void setMechanicalResult(String mechanicalResult) {
        this.mechanicalResult = mechanicalResult;
    }

    public String getElectricalResult() {
        return electricalResult;
    }

    public void setElectricalResult(String electricalResult) {
        this.electricalResult = electricalResult;
    }

    public String getEngineManagementResult() {
        return engineManagementResult;
    }

    public void setEngineManagementResult(String engineManagementResult) {
        this.engineManagementResult = engineManagementResult;
    }

    public String getOverallResult() {
        return overallResult;
    }

    public void setOverallResult(String overallResult) {
        this.overallResult = overallResult;
    }
}
