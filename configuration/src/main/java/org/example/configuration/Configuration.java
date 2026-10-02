package org.example.configuration;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Configuration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String engineName;
    private String startingSystem;
    private String auxiliaryPto;
    private String oilSystem;
    private String fuelSystem;
    private String coolingSystem;
    private String exhaustSystem;
    private String mountingSystem;
    private String engineManagementSystem;
    private String monitoringControlSystem;
    private String powerTransmission;
    private String gearboxOptions;

    public Configuration() {
    }

    public Long getId() {
        return id;
    }

    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public String getStartingSystem() {
        return startingSystem;
    }

    public void setStartingSystem(String startingSystem) {
        this.startingSystem = startingSystem;
    }

    public String getAuxiliaryPto() {
        return auxiliaryPto;
    }

    public void setAuxiliaryPto(String auxiliaryPto) {
        this.auxiliaryPto = auxiliaryPto;
    }

    public String getOilSystem() {
        return oilSystem;
    }

    public void setOilSystem(String oilSystem) {
        this.oilSystem = oilSystem;
    }

    public String getFuelSystem() {
        return fuelSystem;
    }

    public void setFuelSystem(String fuelSystem) {
        this.fuelSystem = fuelSystem;
    }

    public String getCoolingSystem() {
        return coolingSystem;
    }

    public void setCoolingSystem(String coolingSystem) {
        this.coolingSystem = coolingSystem;
    }

    public String getExhaustSystem() {
        return exhaustSystem;
    }

    public void setExhaustSystem(String exhaustSystem) {
        this.exhaustSystem = exhaustSystem;
    }

    public String getMountingSystem() {
        return mountingSystem;
    }

    public void setMountingSystem(String mountingSystem) {
        this.mountingSystem = mountingSystem;
    }

    public String getEngineManagementSystem() {
        return engineManagementSystem;
    }

    public void setEngineManagementSystem(String engineManagementSystem) {
        this.engineManagementSystem = engineManagementSystem;
    }

    public String getMonitoringControlSystem() {
        return monitoringControlSystem;
    }

    public void setMonitoringControlSystem(String monitoringControlSystem) {
        this.monitoringControlSystem = monitoringControlSystem;
    }

    public String getPowerTransmission() {
        return powerTransmission;
    }

    public void setPowerTransmission(String powerTransmission) {
        this.powerTransmission = powerTransmission;
    }

    public String getGearboxOptions() {
        return gearboxOptions;
    }

    public void setGearboxOptions(String gearboxOptions) {
        this.gearboxOptions = gearboxOptions;
    }
}
