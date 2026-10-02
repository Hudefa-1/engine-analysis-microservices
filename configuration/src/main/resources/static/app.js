let currentAnalysisId = null;
let currentConfigurationId = null;

const saveButton = document.getElementById("saveButton");
const analysisButton = document.getElementById("analysisButton");

saveButton.addEventListener("click", saveConfiguration);
analysisButton.addEventListener("click", startAnalysis);

async function saveConfiguration() {

    const configuration = {
        engineName: document.getElementById("engineName").value,
        startingSystem: document.getElementById("startingSystem").value,
        auxiliaryPto: document.getElementById("auxiliaryPto").value,
        oilSystem: document.getElementById("oilSystem").value,
        fuelSystem: document.getElementById("fuelSystem").value,
        coolingSystem: document.getElementById("coolingSystem").value,
        exhaustSystem: document.getElementById("exhaustSystem").value,
        mountingSystem: document.getElementById("mountingSystem").value,
        engineManagementSystem: document.getElementById("engineManagementSystem").value,
        monitoringControlSystem: document.getElementById("monitoringControlSystem").value,
        powerTransmission: document.getElementById("powerTransmission").value,
        gearboxOptions: document.getElementById("gearboxOptions").value
    };

    const response = await fetch("/configurations", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(configuration)
    });

    const savedConfiguration = await response.json();0

    currentConfigurationId = savedConfiguration.id;

    analysisButton.disabled = false;

    console.log(savedConfiguration);

    document.getElementById("configurationMessage").textContent =
        "Konfiguration gespeichert";

    document.getElementById("analysisMessage").textContent = "";
}

async function startAnalysis() {

    const response = await fetch(
        "/configurations/"
        + currentConfigurationId
        + "/analysis",
        {
            method: "POST"
        }
    );

    const analysis = await response.json();

    currentAnalysisId = analysis.id;

    console.log(analysis);

    document.getElementById("analysisMessage").textContent =
        "Analyse gestartet";

    startStatusPolling();
}

function updateSpinner(spinnerId, status) {

    const spinner = document.getElementById(spinnerId);

    if (status === "running") {
        spinner.classList.remove("hidden");
    } else {
        spinner.classList.add("hidden");
    }
}

function startStatusPolling() {

    const interval = setInterval(async () => {

        const response = await fetch(
            "/analysis/" + currentAnalysisId
        );

        const analysis = await response.json();

        document.getElementById("fluidStatus").textContent =
            analysis.fluidStatus;
        document.getElementById("fluidResult").textContent =
            displayResult(
                analysis.fluidStatus,
                analysis.fluidResult
            );

        document.getElementById("mechanicalStatus").textContent =
            analysis.mechanicalStatus;
        document.getElementById("mechanicalResult").textContent =
            displayResult(
                analysis.mechanicalStatus,
                analysis.mechanicalResult
            );

        document.getElementById("electricalStatus").textContent =
            analysis.electricalStatus;
        document.getElementById("electricalResult").textContent =
            displayResult(
                analysis.electricalStatus,
                analysis.electricalResult
            );

        document.getElementById("engineStatus").textContent =
            analysis.engineManagementStatus;
        updateSpinner(
            "fluidSpinner",
            analysis.fluidStatus
        );

        updateSpinner(
            "mechanicalSpinner",
            analysis.mechanicalStatus
        );

        updateSpinner(
            "electricalSpinner",
            analysis.electricalStatus
        );

        updateSpinner(
            "engineSpinner",
            analysis.engineManagementStatus
        );
        document.getElementById("engineResult").textContent =
            displayResult(
                analysis.engineManagementStatus,
                analysis.engineManagementResult
            );

        document.getElementById("overallResult").textContent =
            analysis.overallResult;

        const fluidDone =
            analysis.fluidStatus === "ready"
            || analysis.fluidStatus === "failed";

        const mechanicalDone =
            analysis.mechanicalStatus === "ready"
            || analysis.mechanicalStatus === "failed";

        const electricalDone =
            analysis.electricalStatus === "ready"
            || analysis.electricalStatus === "failed";

        const engineDone =
            analysis.engineManagementStatus === "ready"
            || analysis.engineManagementStatus === "failed";

        const finished =
            fluidDone
            && mechanicalDone
            && electricalDone
            && engineDone;

        if (finished) {
            clearInterval(interval);
        }

    }, 2000);
}

function displayResult(status, result) {

    if (status === "failed" && result == null) {
        return "Technischer Fehler – Analyse konnte nicht durchgeführt werden";
    }

    if (result == null) {
        return "-";
    }

    return result;
}
