package org.exp.reportservice.testng;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.exp.reportservice.testng.ChartGenerator.createBarChart;
import static org.exp.reportservice.testng.ChartGenerator.createPieChart;
import static org.exp.reportservice.testng.TestNGReportParser.testNgResults;

public class TestExecutionManager {

    public static StringBuilder legendHtml = new StringBuilder();

    public static void emailTestReports(String applicationName, String mailTo, String mailSubject) {
        generateAndSendTestNGReports(null, applicationName, mailTo, mailSubject, null);
    }

    public static void emailTestReports(String applicationName, String mailTo, String mailSubject, String optionalFilePath) {
        generateAndSendTestNGReports(null, applicationName, mailTo, mailSubject, optionalFilePath);
    }

    public static void emailTestReportsWithNote(String optionalNoteOnFailure, String applicationName, String mailTo, String mailSubject, String optionalFilePath) {
        generateAndSendTestNGReports(optionalNoteOnFailure, applicationName, mailTo, mailSubject, optionalFilePath);
    }

    public static void emailTestReportsWithNote(String optionalNoteOnFailure, String applicationName, String mailTo, String mailSubject) {
        generateAndSendTestNGReports(optionalNoteOnFailure, applicationName, mailTo, mailSubject, null);
    }

    public static void generateAndSendTestNGReports(String noteOnFailure, String applicationName, String mailTo, String emailSubject, String optionalFilePath){
        Path path = Paths.get(System.getProperty("user.dir")).resolve("test-output").resolve("testng-results.xml");
        if (optionalFilePath != null) {
            path = Paths.get(optionalFilePath);
        }

        try {
            Map<String, Integer> statusCounts = new HashMap<>();
            Map<String, Map<String, Integer>> featureMap = new HashMap<>();

            StringBuilder htmlResults = TestNGReportParser.parser(noteOnFailure, applicationName, path);
            for (TestNGResult res : testNgResults) {
                statusCounts.put(res.methodStatus, statusCounts.getOrDefault(res.methodStatus, 0) + 1);
                featureMap.putIfAbsent(res.className, new HashMap<>());
                Map<String, Integer> inner = featureMap.get(res.className);
                inner.put(res.methodStatus, inner.getOrDefault(res.methodStatus, 0) + 1);
            }
            File pieChart = createPieChart(statusCounts, "Overall Summary", "target/piechart.png");
            File barChart = createBarChart(featureMap, "Execution Status by Methods", "target/barchart.png");
            TestReportMailer.emailTestReport(mailTo, emailSubject, htmlResults, pieChart, barChart);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        emailTestReports("C3P",
                "bharath.potlabhatni@experian.com",
                "Production Sanity Execution Report",
        null);
        emailTestReportsWithNote(
                "QE will perform post-validation failure analysis and circulate key findings.",
                "C3P",
                "bharath.potlabhatni@experian.com",
                "Production Sanity Execution Report");
    }

    private static void replacePlaceholder(StringBuilder builder, String placeholder, String replacement) {
        int start = builder.indexOf(placeholder);
        if (start == -1) {
            return;
        }

        int end = start + placeholder.length();
        builder.replace(start, end, replacement);
    }
}
