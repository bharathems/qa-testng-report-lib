package org.exp.reportservice.testrunners;


import org.exp.reportservice.cucumber.CucumberChartGenerator;
import org.exp.reportservice.cucumber.CucumberReportParser;
import org.exp.reportservice.cucumber.ScenarioResult;
import org.exp.reportservice.cucumber.TargetFileFinder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.exp.reportservice.commons.EmailSender.send;
import static org.exp.reportservice.cucumber.CucumberReportParser.results;

public class CucumberEmailReportService {

    public static void main(String[] args) {
        try {
            // Manually trigger the report merging
//            mergeCucumberJsonReports(); //Optional
//            CucumberEmailReportService.sendReportsInEmail("QE will perform post-validation failure analysis and circulate key findings within 45 minutes to all stakeholders." ,
//                    "APP Name",
//                    "cucumber-json-report.json",
//                    "bharath.potlabhatni@experian.com",
//                    "PRODUCTION Sanity Execution Report ");
            CucumberEmailReportService.sendReportsInEmail(
                    "APP Name",
                    "cucumber-json-report.json",
                    "bharath.potlabhatni@experian.com",
                    "PRODUCTION Sanity Execution Report ");


        } catch (Exception e) {
            System.err.println("Error during report processing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void sendCucumberReportsInEmail(String noteOnFailures, String applicationName, String reportJsonFileName, String mailTo, String mailSubject) throws Exception{
        Map<String, Integer> statusCounts = new HashMap<>();
        Map<String, Map<String, Integer>> featureMap = new HashMap<>();
        Optional<File> jsonReportOptional = TargetFileFinder.findInTarget(reportJsonFileName);

        File jsonReportPath = jsonReportOptional.orElseThrow(() -> new IllegalStateException(
                "Cucumber report `" + reportJsonFileName + "` not found." +
                        " Provide an absolute path to the report, place the report on the classpath/resources, or ensure the report file is available under `target` in the consuming project."));


        String reportPath = jsonReportPath.getPath();
        File jsonReport = new File(reportPath);

        StringBuilder htmlResults = CucumberReportParser.parseReport(jsonReport, applicationName, noteOnFailures);
        for (ScenarioResult scenarioResult : results) {
            statusCounts.put(scenarioResult.status, statusCounts.getOrDefault(scenarioResult.status, 0) + 1);
            featureMap.putIfAbsent(scenarioResult.feature, new HashMap<>());
            Map<String, Integer> inner = featureMap.get(scenarioResult.feature);
            inner.put(scenarioResult.status, inner.getOrDefault(scenarioResult.status, 0) + 1);
        }

        File barChartSummary = CucumberChartGenerator.createPieChartForOverAllSummary(statusCounts, "Overall Summary", "target/pieChart.png");
        File barChart = CucumberChartGenerator.createBarChart(featureMap, "Execution Status by Feature/Page", "target/barchart.png");
        send(mailTo, mailSubject, htmlResults, barChartSummary, barChart);
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
