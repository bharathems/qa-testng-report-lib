package org.exp.reportservice.cucumber;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.exp.reportservice.commons.CommonFunctions;
import org.exp.reportservice.commons.HTMLReportGeneration;
import org.exp.reportservice.commons.HeaderAndFooter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.StreamSupport;

import static org.exp.reportservice.commons.CommonFunctions.statusPill;
import static org.exp.reportservice.commons.HeaderAndFooter.escapeHtml;
import static org.exp.reportservice.constants.GlobalConstants.setHtmlBuilder;


public class CucumberReportParser {
    public static List<ScenarioResult> results = new ArrayList<>();
    private static int totalScenarioSize;
    private static int totalScenarioPassedCount;
    private static int totalScenarioFailedCount;
    private static int totalScenarioSkippedCount;



    private static final String TABLE_STYLE = "border-collapse:separate;font-family:Arial,sans-serif;font-size:12px;width:100%;background:#ffffff;border-radius:8px;overflow:hidden;border-spacing:0;";
    private static final String TH_STYLE = "style=\"background-color:#0b57a4;color:#fff;padding:12px 10px;border-bottom:2px solid #1565c0;border-right:1px solid #e6eef6;text-align:left;font-weight:bold;font-size:14px;font-family:Arial,sans-serif;\"";
    private static final String TD_STYLE = "style=\"padding:10px 8px;border:1px solid #eef3fb;text-align:left;vertical-align:top;color:#223047;background:#ffffff;font-size:13px;\"";
    private static final String TABLE_ATTR = "border=\"0\" cellpadding=\"6\" cellspacing=\"0\"";
    private static final String CAPTION_STYLE = "style=\"text-align:left;font-weight:700;padding:8px 6px;font-size:13px;color:#0b2b4a;\"";



    public static StringBuilder parseReport(File jsonFile, String applicationName, String noteOnFailures) throws IOException {
        int scenarioSize = 0;
        int scenarioPassedCount = 0;
        int scenarioFailedCount = 0;
        int scenarioSkippedCount = 0;

        StringBuilder html = new StringBuilder();
        StringBuilder featureSummaryRows = new StringBuilder();
        StringBuilder featureDetailsBlocks = new StringBuilder();


        HeaderAndFooter.setHeader(html, applicationName);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonFile);

        List<JsonNode> features = StreamSupport.stream(root.spliterator(), false)
                .sorted(Comparator.comparing(n -> n.path("name").asText("")))
                .toList();

        for (JsonNode feature : features) {
            String featureName = feature.path("name").asText("NA");
            JsonNode scenariosNode = feature.get("elements");
            if (scenariosNode == null || !scenariosNode.isArray()) {
                continue;
            }

            scenarioSize = 0;
            scenarioPassedCount = 0;
            scenarioFailedCount = 0;
            scenarioSkippedCount = 0;

            List<JsonNode> scenarios = StreamSupport.stream(scenariosNode.spliterator(), false)
                    .sorted(Comparator.comparing(n -> n.path("name").asText("")))
                    .toList();


            featureDetailsBlocks.append("<table id=\"feature-details\" " + TABLE_ATTR + " style=\"" + TABLE_STYLE + "margin-bottom:12px;\">")
                    .append("<a id=\"scn"+featureName+"\" name=\"scn"+featureName+"\"></a>")
                    .append("<caption " + CAPTION_STYLE + " align=\"left\">"
                            + " <span style=\"color:#0b57a4;font-weight:700;font-family:Arial,Helvetica,sans-serif;\">FEATURE NAME - </span>" + escapeHtml(featureName) + "</caption>");

            featureDetailsBlocks.append("<tr><th " + TH_STYLE + ">S#</th><th " + TH_STYLE + ">Scenario</th><th " + TH_STYLE + ">Status</th></tr>");


            int idx = 1;
            for (JsonNode scenario : scenarios) {
                String scenarioId = scenario.path("id").asText("NA");
                String scenarioName = scenario.path("name").asText("NA");
                if (scenarioName.contains("<testcaseid>")) {
                    scenarioName = scenarioName.split("<testcaseid>")[0];
                }
                String scenarioDesc = scenario.path("description").asText("NA");
                String status = "Pass";
                String pill = "<span class='pill ok'>Pass</span>";
                String errorMessage = "";
                boolean isScenarioPassed = getScenarioStatus(scenario);
                System.out.println("Scenario '%s' after status: %s".formatted(scenarioName, isScenarioPassed));
                if (!isScenarioPassed) {
                    status = "Fail";
                    pill = "<span class='pill err'>Fail</span>";
                    // try to capture error message from 'after' nodes
                    JsonNode afterNodes = scenario.get("after");
                    if (afterNodes != null && afterNodes.isArray()) {
                        for (JsonNode after : afterNodes) {
                            String em = after.path("result").path("error_message").asText("");
                            if (em != null && !em.isBlank()) {
                                errorMessage = em;
                                break;
                            }
                        }
                    }
                    scenarioFailedCount++;
                    scenarioSize++;
                }

                JsonNode stepsNode = scenario.get("steps");
                if (isScenarioPassed
                        && stepsNode != null
                        && stepsNode.isArray())
                {
                    for (JsonNode step : stepsNode) {
                        String stepStatus = step.path("result").path("status").asText("NA");
                        if ("failed".equalsIgnoreCase(stepStatus)) {
                            status = "Fail";
                            pill = "<span class='pill err'>Fail</span>";
                            // capture error message from failed step if available
                            String em = step.path("result").path("error_message").asText("");
                            if (em != null && !em.isBlank()) {
                                errorMessage = em;
                            }
                            scenarioFailedCount++;
                            scenarioSize++;
                            break;
                        } else if ("skipped".equalsIgnoreCase(stepStatus)) {
                            status = "Skip";
                            pill = "<span class='pill skp'>Skip</span>";
                            scenarioSkippedCount++;
                            scenarioSize++;
                        }
                    }
                }
                if ("Pass".equals(status)) {
                    scenarioPassedCount++;
                    scenarioSize++;
                }
                featureDetailsBlocks.append("<tr>")
                        .append("<td " + TD_STYLE +" >").append(idx++).append("</td>")
                        .append("<td " + TD_STYLE +" >").append(scenarioName);
                if (scenarioDesc != null && !"NA".equalsIgnoreCase(scenarioDesc.trim())) {
                    featureDetailsBlocks.append("<div class='desc'>").append(scenarioDesc).append("</div>");
                }
                String color = status.equalsIgnoreCase("fail") ? "red" : "green";
                // add title attribute only when there is an error message
                String titleAttr = " title='"+errorMessage+"'";//set elements[1].steps[2].result.status tool tip when its failed
                featureDetailsBlocks.append("</td>")
                        .append("<td " + TD_STYLE + titleAttr + " style=\"color: " + color + ";font-weight:bold;\"> " + statusPill(status) + " </td>")
//                        .append("<td " + TD_STYLE + titleAttr + " style=\"color: " + color + ";font-weight:bold;\"> " + statusPill(status) + " </td>")
                        .append("</tr>");



                results.add(new ScenarioResult(featureName, scenarioId, scenarioName, scenarioDesc, status));
            }

            featureDetailsBlocks.append("</tbody></table>")
                    .append("<div class='backtop' style='font-size:12px;margin-top:10px;'><a href='#reportDetails'>&uarr; Back to top</a></div>")
                    .append("</div></div>");

            // replace the feature name append with this (inside the features loop)
            String safeFeatureName = escapeHtml(featureName.trim());
            featureSummaryRows.append("<tr>")
                    .append("<td " + TD_STYLE + "> <a href=\"#scn" + safeFeatureName + "\" style=\"text-decoration:underline;color:#223047;font-size:13px;line-height:1.2;font-weight:600;\">" + escapeHtml(safeFeatureName.toUpperCase()) + "</a></td>")
                    .append("<td  " + TD_STYLE + " ><span class='pill'>").append(scenarioSize).append("</span></td>")
                    .append("<td  " + TD_STYLE + " ><span class='pill ok'>").append(scenarioPassedCount).append("</span></td>")
                    .append("<td  " + TD_STYLE + " ><span class='pill err'>").append(scenarioFailedCount).append("</span></td>")
                    .append("<td  " + TD_STYLE + " ><span class='pill skp'>").append(scenarioSkippedCount).append("</span></td>")
                    .append("</tr>");

            totalScenarioSize += scenarioSize;
            totalScenarioPassedCount += scenarioPassedCount;
            totalScenarioFailedCount += scenarioFailedCount;
            totalScenarioSkippedCount += scenarioSkippedCount;
        }

        System.out.println(totalScenarioSize);

        html.append("<a id='top'></a>");

        String overAllSummary = CommonFunctions.overAllSummary("Scenarios").toString()
                .replace("{methodsTotalCount}", String.valueOf(totalScenarioSize))
                .replace("{passedBadge}", String.valueOf(totalScenarioPassedCount))
                .replace("{failedBadge}", String.valueOf(totalScenarioFailedCount))
                .replace("{skippedBadge}", String.valueOf(totalScenarioSkippedCount));

        html.append(overAllSummary);

        if(noteOnFailures!=null && !noteOnFailures.isBlank() && totalScenarioFailedCount > 0) {
            html.append("<div class='note'><b>Note</b>: "+noteOnFailures+"</div>");
        }

        html.append("<div style='height:12px'></div>")
                .append("<img src='cid:pie' alt='Overall Summary Chart'>")
                .append("</div></div>");

        html.append("<a id=\"methods-details-table\" name=\"scenarios-details-table\"></a>");
        html.append("<div class='section'>")
                .append("</br><h2 style=\"font-family:Arial,Helvetica,sans-serif;font-size:16px;text-decoration:underline;color:#0b57a4;\">Feature-wise Summary</h2>")
                .append("<div class='section-body'>")
                .append("<table id=\"feature-details\" " + TABLE_ATTR + " style=\"" + TABLE_STYLE + "margin-bottom:12px;\"><thead><tr>")
                .append("<th " + TH_STYLE + ">Feature/Page</th><th " + TH_STYLE + ">Total</th><th " + TH_STYLE + ">Passed</th><th " + TH_STYLE + ">Failed</th><th " + TH_STYLE + ">Skipped</th>")
                .append("</tr\"></thead><tbody>")
                .append(featureSummaryRows)
                .append("</tbody></table>")
                .append("<div style='height:12px'></div>")
                .append("<br><img src='cid:bar' alt='Feature Summary Chart'><br>")
                .append("</div></div>");

        html.append("<div class='section' id='details'>")
                .append("</br><h2 style=\"font-family:Arial,Helvetica,sans-serif;font-size:16px;text-decoration:underline;color:#0b57a4;\">Feature-wise Scenario Execution Details</h2>")
                .append("<hr style=\"border:none;border-bottom:1px solid #0b57a4;margin:2px 0;\">")
                .append("<div class='section-body'>")
                .append(featureDetailsBlocks)
                .append("</div></div>");


        HeaderAndFooter.setFooter(html);
        setHtmlBuilder(new StringBuilder(html));
        HTMLReportGeneration.htmlReportGenerator();
        return html;
    }

    private static boolean getScenarioStatus(JsonNode scenario) {
        JsonNode scenarioAfterNodes = scenario.get("after");
        if (scenarioAfterNodes != null && scenarioAfterNodes.isArray()) {
            return StreamSupport.stream(scenarioAfterNodes.spliterator(), false)
                    .map(after -> after.get("result"))
                    .map(result -> result.get("status").asText())
                    .allMatch(status -> status.equals("passed"));
        }
        return true;
    }

    private static String slug(String s) {
        return s == null
                ? ""
                : s.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }

}
