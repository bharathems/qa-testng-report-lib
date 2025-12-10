package org.exp.reportservice.cucumber;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class JsonThenPostProcess {

    private static final Path TARGET_DIR = Paths.get("target/cucumber-parallel/reports");
    private static final Path MERGED_REPORT = TARGET_DIR.resolve("cucumber-json-report.json");

    public static void main(String[] args) {
        try {
            // Manually trigger the report merging
//            mergeCucumberJsonReports(); //Optional
            ReportService.sendReportsInEmail("APP Name", true,
                    "cucumber-json-report.json",
                    "bharath.potlabhatni@experian.com",
                    "PRODUCTION Sanity Execution Report ");
        } catch (Exception e) {
            System.err.println("Error during report processing: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void mergeCucumberJsonReports() throws IOException {
            // Set your JSON report directory and output file
            String reportsDir = "target/cucumber-parallel/reports/";
            String outputFilePath = "target/cucumber-parallel/reports/cucumber-json-report.json";

            File dir = new File(reportsDir);
            File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));

            if (files == null || files.length == 0) {
                System.out.println("No JSON files found in " + reportsDir);
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> mergedFeatures = new ArrayList<>();

            Map<String, Map<String, Object>> featureMap = new LinkedHashMap<>();

            for (File file : files) {
                List<Map<String, Object>> features = mapper.readValue(
                        file, new TypeReference<List<Map<String, Object>>>() {
                        }
                );

                for (Map<String, Object> feature : features) {
                    String uri = (String) feature.get("uri");
                    if (!featureMap.containsKey(uri)) {
                        featureMap.put(uri, feature);
                    } else {
                        // Merge scenarios (elements) under the same feature
                        List<Map<String, Object>> existingElements = (List<Map<String, Object>>) featureMap.get(uri).get("elements");
                        List<Map<String, Object>> newElements = (List<Map<String, Object>>) feature.get("elements");
                        existingElements.addAll(newElements);
                    }
                }
            }

            mergedFeatures.addAll(featureMap.values());

            // Write merged JSON
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputFilePath), mergedFeatures);
            System.out.println("Merged report written to: " + outputFilePath);
    }
}
