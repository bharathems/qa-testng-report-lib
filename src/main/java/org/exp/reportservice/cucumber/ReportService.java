package org.exp.reportservice.cucumber;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.exp.reportservice.cucumber.CucumberReportParser.results;

public class ReportService {

    public static void sendCucumberReportsInEmail(String noteOnFailures, String applicationName, String reportJsonFileName, String mailTo, String mailSubject) throws Exception{
        Map<String, Integer> statusCounts = new HashMap<>();
        Map<String, Map<String, Integer>> featureMap = new HashMap<>();
        Optional<File> jsonReportOptional = TargetFileFinder.findInTarget(reportJsonFileName);
        File jsonReportPath = jsonReportOptional.orElseThrow(() ->
                new IllegalStateException("`" + reportJsonFileName + "` not found under `target`"));
        String reportPath = jsonReportPath.getPath();
        File jsonReport = new File(reportPath);

        StringBuilder htmlResults = CucumberReportParser.parseReport(jsonReport, applicationName, noteOnFailures);
        for (ScenarioResult scenarioResult : results) {
            statusCounts.put(scenarioResult.status, statusCounts.getOrDefault(scenarioResult.status, 0) + 1);
            featureMap.putIfAbsent(scenarioResult.feature, new HashMap<>());
            Map<String, Integer> inner = featureMap.get(scenarioResult.feature);
            inner.put(scenarioResult.status, inner.getOrDefault(scenarioResult.status, 0) + 1);
        }

        File barChartSummary = CucumberChartGenerator.createPieChartForOverAllSummary(statusCounts, "Overall Summary", "target/barchart1.png");
        File barChart = CucumberChartGenerator.createBarChart1(featureMap, "Execution Status by Feature/Page", "target/barchart.png");
        EmailSender.send(mailTo, mailSubject, htmlResults, barChartSummary, barChart);
    }


    public static void sendReportsInEmail(String noteOnFailures, String applicationName, String reportJsonFileName, String mailTo, String mailSubject) throws Exception {
        sendCucumberReportsInEmail(noteOnFailures, applicationName, reportJsonFileName, mailTo, mailSubject);
    }

    public static void sendReportsInEmail(String applicationName, String reportJsonFileName, String mailTo, String mailSubject) throws Exception {
        sendCucumberReportsInEmail(null, applicationName, reportJsonFileName, mailTo, mailSubject);
    }

    public static void sendReportsInEmail(String applicationName,  String mailTo, String mailSubject) throws Exception {
        sendCucumberReportsInEmail(null, applicationName, "cucumber-json-report.json", mailTo, mailSubject);
    }
}
