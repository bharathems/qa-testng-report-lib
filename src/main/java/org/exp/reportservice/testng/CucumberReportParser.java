//package org.exp.reportservice.testng;
//
////import com.fasterxml.jackson.databind.JsonNode;
////import com.fasterxml.jackson.databind.ObjectMapper;
////import utils.PropertiesLoader;
////import utils.logger.Logger;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Random;
//
//public class CucumberReportParser {
//
//
//    static String[] styles = {
//            "background:#3498db;color:#fff;",
//            "background:#e74c3c;color:#fff;",
//            "background:#2ecc71;color:#fff;",
//            "background:#f1c40f;color:#000;",
//            "background:#9b59b6;color:#fff;"
//    };
//    //    static Random random = new Random();
//    public static List<ScenarioResult> results = new ArrayList<>();
//    static int totalScenarioSize = 0;
//    static int scenarioSize = 0;
//    static int scenarioPassedCount = 0;
//    static int scenarioFailedCount = 0;
//    static int scenarioSkippedCount = 0;
//    static int totalScenarioPassedCount = 0;
//    static int totalScenarioFailedCount = 0;
//    static int totalScenarioSkippedCount = 0;
//    public static StringBuilder parseReport(File jsonFile) throws IOException {
//        String platformName ="UAT";
//                StringBuilder htmlBuilder = new StringBuilder();
//        String formattedDate = new SimpleDateFormat("d-MMM-yyyy").format(new Date());
//        StringBuilder styleHtmlBuilder = new StringBuilder();
//        StringBuilder headerRows = new StringBuilder();
//        StringBuilder featuresTableBuilder = new StringBuilder();
//        StringBuilder scnHtmlBuilder = new StringBuilder();
//        htmlBuilder.append("<html><head>")
//                .append("<h3>Production Sanity Execution Report</h3>"
//                        + "<p><b>Application:</b> " + platformName + " </p>"
//                        + "<p><b>Date:</b> " + formattedDate + "</p>"
//                        + "<p><b>Executed By:</b> Digital QE Team</p>"
//                );
//        styleHtmlBuilder.append("<style>")
//                .append("table { border-collapse: collapse; width: 60%; margin-bottom: 10px; }")
//                .append("th, td { border: 1px solid #ccc; padding: 4px; text-align: left; }")
//                .append("th { background-color: #f2f2f2; }")
//                .append(".passed, .pass { color: #2e7d32; font-weight: bold; }")   // Green
//                .append(".failed, .fail { color: #c62828; font-weight: bold; }")   // Red
//                .append(".skipped, .skip { color: #ef6c00; font-weight: bold; }")  // Yellow
//                .append("table, th, td {border: 1px solid black;} p {line-height: 2px; font-size: 14px;}")
//                .append("</style></head><body>");
//
//
//        htmlBuilder.append(styleHtmlBuilder);
//        String summaryTable = "<table class = \"summary\">"
//                + "<tr><th>Total Scenarios</th><th>Passed</th><th>Failed</th><th>Skipped</th></tr>"
//                + "<tr><td>{scenarioTotalCount}</td>"
//                + "<td>{scenarioPassedCount}</td>"
//                + "<td>{scenarioFailedCount}</td>"
//                + "<td>{scenarioSkippedCount}</td></tr></table>";
//
//        featuresTableBuilder.append("<table class = \"features\">"
//                + "<tr><th>Feature/Page</th><th>Total Scenarios</th><th>Passed</th><th>Failed</th><th>Skipped</th></tr>");
//        headerRows.append("<table role='presentation' cellspacing='0' cellpadding='6' border='0' align='center' "
//                + "style='margin:15px auto;border-collapse:separate;border-spacing:12px;text-align:center;'><tr>");
//
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode root = mapper.readTree(jsonFile);
//
//        for (JsonNode featureNode : root) { //loop through features
//            String featureName = featureNode.path("name").asText("NA");
//            JsonNode scenarios = featureNode.get("elements");
//            totalScenarioSize += scenarios.size();
//            scnHtmlBuilder.append("<table> <caption style=\"\n"
//                            + "    align-items: left;\n"
//                            + "    text-align: left;font-weight: bold; line-height: 1; margin: 0;\n"
//                            + "  padding: 0;\n"
//                            + "  line-height: 1.2;\n"
//                            + "\">" + featureName + "</caption>")
//                    .append("<tr><th>Feature Name</th><th>Scenario Name</th><th>Status</th></tr>");
//
//            for (JsonNode scenarioNode : scenarios) {  //loop through scenarios
//                String scenarioId = scenarioNode.path("id").asText("NA");
//                String scenarioName = scenarioNode.path("name").asText("NA");
//                String scenarioDescription = scenarioNode.path("description").asText("NA");
//                String statusWithFormatting = "<td class=\"pass\">Pass</td>";
//
//                for (JsonNode stepNode : scenarioNode.get("steps")) { // loop through steps
//                    String stepStatus = stepNode.path("result").path("status").asText("NA"); // null if missing
//                    //Looking for any failed step in the scenario
//                    if ("failed".equalsIgnoreCase(stepStatus)) { //Looking for any failed step in the scenario
//                        statusWithFormatting = "<td class=\"fail\">Fail</td>";
//                        totalScenarioFailedCount++;
//                        scenarioFailedCount++;
//                        scenarioSize++;
//                        break;
//                    } else if ("skipped".equalsIgnoreCase(stepStatus)) { //Looking for any skipped step in the scenario
//                        statusWithFormatting = "<td class=\"skip\">Skip</td>";
//                        totalScenarioSkippedCount++;
//                        scenarioSkippedCount++;
//                        scenarioSize++;
//                    }
//                } //steps loop
//                //If no failed or skipped step found, then scenario is passed
//                if (!statusWithFormatting.contains("Skip") && !statusWithFormatting.contains("Fail")) { //Looking for any passed step in the scenario
//                    totalScenarioPassedCount++;
//                    scenarioPassedCount++;
//                    scenarioSize++;
//                }
//
//                scnHtmlBuilder.append("<tr>")
//                        .append("<td>").append(featureName).append("</td>")
//                        .append("<td>").append(scenarioName).append("</td>")
//                        .append(statusWithFormatting)
//                        .append("</tr>");
//
//                results.add(new ScenarioResult(featureName, scenarioId, scenarioName, scenarioDescription,
//                        statusWithFormatting.contains("Pass") ? "Pass"
//                                : statusWithFormatting.contains("Fail") ? "Fail" : "Skip"));
//
//            } //Looping all scn in features loop
//            //Feature related scn total/pass/fail count
//            System.out.println(featureName.toUpperCase());
//            featuresTableBuilder.append("<tr><td>" + featureName.toUpperCase() + "</td>");
//            featuresTableBuilder.append("<td>" + scenarioSize + "</td>");
//            featuresTableBuilder.append("<td>" + scenarioPassedCount + "</td>");
//            featuresTableBuilder.append("<td>" + scenarioFailedCount + "</td>");
//            featuresTableBuilder.append("<td>" + scenarioSkippedCount + "</td>");
//            featuresTableBuilder.append("</tr>");
//
//            scnHtmlBuilder.append("</table>")
//                    .append("<hr style=\"border: none; border-bottom: 1px solid black; margin: 16px 0; line-height: 0;\">");
//
//            headerRows.append("<td style='" + styles[new Random().nextInt(styles.length)] + "padding:12px 18px;border-radius:8px;font-weight:bold;'>" + featureName + "<br/>" + scenarioSize + "</td>");
//
//
//            scenarioSize = 0;
//            scenarioPassedCount = 0;
//            scenarioFailedCount = 0;
//            scenarioSkippedCount = 0;
//
//        } //ending feature loop --main
//
//        featuresTableBuilder.append("</table>");
//        headerRows.append("</tr></table>");
//
//        summaryTable = summaryTable.replace("{scenarioTotalCount}", String.valueOf(totalScenarioSize))
//                .replace("{scenarioPassedCount}", String.valueOf(totalScenarioPassedCount))
//                .replace("{scenarioFailedCount}", String.valueOf(totalScenarioFailedCount))
//                .replace("{scenarioSkippedCount}", String.valueOf(totalScenarioSkippedCount));
//
//
//        htmlBuilder.append("<h1 style=\"font-size: 16px; text-decoration: underline;\">Overall Summary:</h1>");
//        htmlBuilder.append(summaryTable);
//        htmlBuilder.append("</br><table style='width:100%; border-collapse:collapse; margin:20px 0; border:none;'>")
////                .append("<tr><td  colspan=\"3\" style='background:#008080;color:#fff;font-weight:bold;padding:4px; height:2px;'>Overall Summary</td></tr>")
//                .append("<tr>")
//                // LEFT: Callouts
//                .append("<td style='width:30%; vertical-align:top; text-align:left; padding-right:20px; border:none;'>")
//                .append("{calloutsHtml}")
//                .append("</td>")
//
//                // CENTER: Donut Chart
//                .append("<td style='width:30%; vertical-align:top; text-align:right; padding-left:20px; border:none;'>")
//                .append("<img src='cid:pie' alt='Overall Summary Report' ")
//                .append("style='max-width:50%; height:auto; display:block; margin-left:auto;'/>")
//                .append("</td>")
//
//                // RIGHT: Status
//                .append("<td style='width:40%; vertical-align:middle; text-align:center; border:none;'>")
//                .append("{donutGraphPlaceHolder}")
//                .append("</td>")
//
//                .append("</tr>")
//                .append("</table>");
//
//
//
//        htmlBuilder.append("<h1 style=\"font-size: 16px; text-decoration: underline;\">Feature-wise Summary:</h1>");
//        htmlBuilder.append(featuresTableBuilder);
//        htmlBuilder.append("<br><img src='cid:bar'><br>")
//
//                .append("</br>")
//                .append("<h1 style=\"font-size: 16px; text-decoration: underline;\">Feature-wise Scenario Execution Details</h1>")
//                .append("<hr style=\"border: none; border-bottom: 1px solid black; margin: 2px 0;\">")//Long underline
//                .append(scnHtmlBuilder)
//                .append(" <br><br>\n"
//                        + "Regards,<br>\n"
//                        + "Digital QE team</body></html>");
//        return htmlBuilder;
//    }
//}
