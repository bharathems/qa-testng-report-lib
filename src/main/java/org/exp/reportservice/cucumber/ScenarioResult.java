package org.exp.reportservice.cucumber;

public class ScenarioResult {
    public String feature;
    public String scenarioId;
    public String scenarioName;
    public String status;
    public String scenarioDescription;

    public ScenarioResult(String feature, String scenarioId, String scenarioName, String scenarioDescription, String status) {
        this.feature = feature;
        this.scenarioId = scenarioId;
        this.scenarioName = scenarioName;
        this.status = status;
        this.scenarioDescription = scenarioDescription;
    }
}
