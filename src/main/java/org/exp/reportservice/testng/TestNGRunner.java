package org.exp.reportservice.testng;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.exp.reportservice.testng.ChartGenerator.createBarChart;
import static org.exp.reportservice.testng.ChartGenerator.createPieChart;
import static org.exp.reportservice.testng.TestNGReportParser.resultsBharath;

public class TestNGRunner {

    public static StringBuilder legendHtml = new StringBuilder();
    public static void sendTestNGReportsInEmail(boolean executeFlag, String mailTo, String mailSubject, String optionalFilePath) {
        if (!executeFlag) {
            return;
        }
        Path path = Paths.get(System.getProperty("user.dir")).resolve("test-output").resolve("testng-results.xml");
        if (optionalFilePath != null) {
            path = Paths.get(optionalFilePath);
        }

        try {
            Map<String, Integer> statusCounts = new HashMap<>();
            Map<String, Map<String, Integer>> featureMap = new HashMap<>();

            StringBuilder htmlResults = TestNGReportParser.parser(path);
            for (TestNGResult res : resultsBharath) {
                statusCounts.put(res.methodStatus, statusCounts.getOrDefault(res.methodStatus, 0) + 1);
                featureMap.putIfAbsent(res.className, new HashMap<>());
                Map<String, Integer> inner = featureMap.get(res.className);
                inner.put(res.methodStatus, inner.getOrDefault(res.methodStatus, 0) + 1);
            }
            File pieChart = createPieChart(statusCounts, "Overall Summary", "target/piechart1.png");
            File barChart = createBarChart(featureMap, "Execution Status by Methods", "target/barchart.png");
            // Replace placeholders in HTML template
//            replacePlaceholder(htmlResults, "{donutGraphPlaceHolder}", legendHtml.toString());
//            replacePlaceholder(htmlResults, "{calloutsHtml}", ChartGenerator.buildCallouts(featureMap));
            SendTestNGResultsInEmail.sendResultsEmail(mailTo, mailSubject, htmlResults, pieChart, barChart);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        sendTestNGReportsInEmail(true, "bharath.potlabhatni@experian.com", "Production Sanity Execution Report", null);
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
