package org.exp.reportservice.cucumber;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.StreamSupport;


public class CucumberReportParser {


    public static List<ScenarioResult> results = new ArrayList<>();

    static int totalScenarioSize = 0;
    static int totalScenarioPassedCount = 0;
    static int totalScenarioFailedCount = 0;
    static int totalScenarioSkippedCount = 0;

    static int scenarioSize = 0;
    static int scenarioPassedCount = 0;
    static int scenarioFailedCount = 0;
    static int scenarioSkippedCount = 0;

    private static String slug(String s) {
        return s == null
                ? ""
                : s.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }

    public static StringBuilder parseReport(File jsonFile, String applicationName) throws IOException {
//        String platformName = "";
        String formattedDate = new SimpleDateFormat("d-MMM-yyyy").format(new Date());

        StringBuilder html = new StringBuilder();
        StringBuilder featureSummaryRows = new StringBuilder();
        StringBuilder featureDetailsBlocks = new StringBuilder();
        final String TABLE_STYLE = "border-collapse:separate;font-family:Arial,sans-serif;font-size:12px;width:100%;background:#ffffff;border-radius:8px;overflow:hidden;border-spacing:0;";
        final String TH_STYLE = "style=\"background-color:#0b57a4;color:#fff;padding:12px 10px;border-bottom:2px solid #1565c0;border-right:1px solid #e6eef6;text-align:left;font-weight:bold;font-size:14px;font-family:Arial,sans-serif;\"";
        final String TD_STYLE = "style=\"padding:10px 8px;border:1px solid #eef3fb;text-align:left;vertical-align:top;color:#223047;background:#ffffff;font-size:13px;\"";
        final String TABLE_ATTR = "border=\"0\" cellpadding=\"6\" cellspacing=\"0\"";
        final String CAPTION_STYLE = "style=\"text-align:left;font-weight:700;padding:8px 6px;font-size:13px;color:#0b2b4a;\"";
        // Styles for sections and headings
        final String SECTION_STYLE = "style=\"background:#f8fafc;border-radius:8px;padding:20px;margin:16px 0;box-shadow:0 2px 8px #e2e8f0;\"";
        final String SECTION_HEAD_STYLE = "style=\"font-size:20px;font-weight:700;color:#0b57a4;margin-bottom:8px;font-family:Arial,Helvetica,sans-serif;\"";
        final String SECTION_BODY_STYLE = "style=\"font-size:14px;color:#16325c;font-family:Arial,Helvetica,sans-serif;\"";

        html.append("<html><head><meta charset='utf-8'>")
                .append("<a id=\"reportDetails\" name=\"reportDetails\"></a>")
                .append("<style type='text/css'>")
                .append("body{margin:0;padding:0;background:#f5f7fb;color:#0b1220;font-family:Arial,Helvetica,sans-serif;font-size:13px;}")
                .append(".wrap{width:100%;max-width:980px;margin:0 auto;padding:14px;}")
                .append(".card{background:#ffffff;border:1px solid #e3e8f3;border-radius:12px;padding:16px;box-shadow:0 2px 6px rgba(15,23,42,.04);} ")
                .append(".title{font-size:22px;line-height:1.2;margin:0 0 6px 0;color:#0b1220;font-weight:bold;} ")
                .append(".meta{margin:0;color:#3a4556;}")
                .append(".section{background:#ffffff;border:1px solid #e3e8f3;border-radius:12px;padding:0;margin-top:14px;box-shadow:0 2px 6px rgba(15,23,42,.04);} ")
                .append(".section-head{background:#3b82f6;color:#ffffff;padding:10px 14px;border-radius:12px 12px 0 0;font-weight:bold;letter-spacing:.2px;} ")
                .append(".section-head-teal{background:#0ea5a4;color:#ffffff;padding:10px 14px;border-radius:12px 12px 0 0;font-weight:bold;letter-spacing:.2px;} ")
                .append(".section-body{padding:14px;}")
                .append(".sub-head{background:#f3f6ff;color:#0b1220;padding:10px 14px;border-radius:10px 10px 0 0;border:1px solid #e3e8f3;border-bottom:none;font-weight:bold;}")
                .append(".kpi-table{width:100%;border-collapse:separate;border-spacing:12px 10px;} ")
                .append(".kpi{background:#f9fbff;border:1px solid #e3e8f3;border-radius:12px;padding:10px;text-align:center;} ")
                .append(".kpi-label{font-weight:bold;color:#2e3645;font-size:12px;margin-bottom:6px;} ")
                .append(".kpi-badge{display:inline-block;border-radius:999px;padding:8px 14px;font-weight:bold;border:1px solid transparent;font-size:14px;text-decoration:none;} ")
                .append(".b-blue{background:#2880f6;color:#ffffff;border-color:#1e6ee1;} ")
                .append(".b-green{background:#18c27f;color:#ffffff;border-color:#12a86e;} ")
                .append(".b-red{background:#ff4d4f;color:#ffffff;border-color:#e13a3c;} ")
                .append(".b-yellow{background:#ffbd2e;color:#4b3200;border-color:#e6a624;} ")
                .append(".note{border:1px solid #ffd89c;background:#fff7e6;color:#7a4b00;border-radius:10px;padding:12px 14px;font-weight:bold;margin-top:12px;font-size:15px;} ")
                .append("table.modern{width:100%;border-collapse:separate;border-spacing:0;background:#fff;border:1px solid #e3e8f3;border-radius:12px;} ")
                .append("table.modern thead th{background:#eef2ff;color:#0b1220;font-size:12px;font-weight:bold;padding:10px;border-bottom:1px solid #e3e8f3;text-align:left;} ")
                .append("table.modern td{padding:10px;border-bottom:1px solid #e3e8f3;color:#3a4556;} ")
                .append("table.modern tbody tr:nth-child(odd) td{background:#fbfcff;} ")
                .append("table.modern tbody tr:last-child td{border-bottom:none;} ")
                .append(".feat{font-weight:bold;color:#0b1220;} ")
                .append(".pill{display:inline-block;border-radius:999px;padding:5px 10px;border:1px solid transparent;font-weight:bold;font-size:12px;} ")
                .append(".ok{background:#e7f8f1;color:#077a55;border-color:#b6f0dc;} ")
                .append(".err{background:#ffecec;color:#b42318;border-color:#ffc5c0;} ")
                .append(".skp{background:#fff7e6;color:#9a5b00;border-color:#ffdca8;} ")
                .append(".desc{margin-top:2px;font-size:12px;color:#6b7280;} ")
                .append("a{color:#1d4ed8;text-decoration:underline;} a:link,a:visited{color:#1d4ed8;} ")
                .append(".kpi-link,.kpi-link:link,.kpi-link:visited,.kpi-link:hover,.kpi-link:active{color:#ffffff !important;text-decoration:underline !important;} ")
                .append(".u{ text-decoration:underline !important; border-bottom:1px solid rgba(255,255,255,.9); mso-border-alt:solid #ffffff 1px; }")
                .append(".backtop{font-size:12px;margin-top:10px;} ")
                .append("</style></head><body><div class='wrap'>");

        html.append("<table width=\"100%\" border=\"0\" cellpadding=\"8\" cellspacing=\"0\" bgcolor=\"#f8fafc\" style=\"background:#f8fafc;width:100%;border:1px solid #e6eef6;\">");
        html.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"background:#f0f6ff;border:1px solid #e6eef6;width:100%;\">")
                .append("<tr><td align=\"left\" style=\"padding:20px;font-family:Arial,Helvetica,sans-serif;color:#223047;\">")
                .append("<div style=\"font-size:22px;font-weight:700;color:#0b1220;line-height:1.2;margin:0 0 8px 0;\">Automation Test Execution Report</div>")
                .append("<div style=\"font-size:13px;color:#475569;line-height:1.4;margin:0;\">")
                .append("<strong style=\"font-weight:700;\">Application:</strong> ").append(escapeHtml(applicationName))
                .append(" &nbsp;&nbsp; <strong style=\"font-weight:700;\">Date:</strong> ").append(formattedDate)
                .append(" &nbsp;&nbsp; <strong style=\"font-weight:700;\">Executed By:</strong> QE Team")
                .append("</div>")
                .append("</td></tr></table>");

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

            final String featureAnchor = "feature-" + slug(featureName);


            featureDetailsBlocks.append("<table id=\"feature-details\" " + TABLE_ATTR + " style=\"" + TABLE_STYLE + "margin-bottom:12px;\">")
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
                boolean isScenarioPassed = getScenarioStatus(scenario);
                System.out.println("Scenario '%s' after status: %s".formatted(scenarioName, isScenarioPassed));
                if (!isScenarioPassed) {
                    status = "Fail";
                    pill = "<span class='pill err'>Fail</span>";
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
//                scnHtmlBuilder.append("<tr><td " + TD_STYLE + " colspan=\"4\" style=\"text-align:center;font-weight:bold;\">").append(suiteName).append("</td></tr>");
                featureDetailsBlocks.append("<tr>")
                        .append("<td " + TD_STYLE +" >").append(idx++).append("</td>")
                        .append("<td " + TD_STYLE +" >").append(scenarioName);
                if (scenarioDesc != null && !"NA".equalsIgnoreCase(scenarioDesc.trim())) {
                    featureDetailsBlocks.append("<div class='desc'>").append(scenarioDesc).append("</div>");
                }
                featureDetailsBlocks.append("</td>")
                        .append("<td " + TD_STYLE +" >").append(pill).append("</td>")
                        .append("</tr>");

                results.add(new ScenarioResult(featureName, scenarioId, scenarioName, scenarioDesc, status));
            }

            featureDetailsBlocks.append("</tbody></table>")
                    .append("<div class='backtop'><a href='#reportDetails'>&uarr; Back to top</a></div>")
                    .append("</div></div>");




// replace the feature name append with this (inside the features loop)
            String safeFeatureName = escapeHtml(featureName.trim());
            featureSummaryRows.append("<tr>")
                    .append("<td class='feat'  " + TD_STYLE + " >").append(safeFeatureName)
                    .append("<a href='#").append(featureAnchor).append("' style=\"text-decoration:none;color:inherit;display:inline-block;max-width:360px;vertical-align:middle;word-break:break-word;white-space:normal;mso-line-height-rule:exactly;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;\">")
                    .append("</a></td>")
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

        String summaryTable =
                "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family:Arial,sans-serif;margin-bottom:16px;\">"
                        + "  <tr>"
                        + "    <td style=\"padding:6px;\">"
                        + "      <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse:collapse;\">"
                        + "        <tr>"
                        + "          <td style=\"width:25%;padding:6px;vertical-align:top;\">"
                        + "            <table width=\"100%\" cellpadding=\"8\" cellspacing=\"0\" style=\"background:#f7f7f7;border:1px solid #e6e6e6;border-radius:8px;\">"
                        + "              <tr><td style=\"font-size:18px;font-weight:700;color:#333;text-align:center;\">"+totalScenarioSize+"</td></tr>"
                        + "              <tr><td style=\"font-size:13px;color:#666;text-align:center;\">Total Scenarios</td></tr>"
                        + "            </table>"
                        + "          </td>"
                        + "          <td style=\"width:25%;padding:6px;vertical-align:top;\">"
                        + "            <table width=\"100%\" cellpadding=\"8\" cellspacing=\"0\" style=\"background:#e9f7ec;border:1px solid #d6eed6;border-radius:8px;\">"
                        + "              <tr><td style=\"font-size:18px;font-weight:700;color:#2d6a33;text-align:center;\">"+totalScenarioPassedCount+"</td></tr>"
                        + "              <tr><td style=\"font-size:13px;color:#466b3f;text-align:center;\">Passed</td></tr>"
                        + "            </table>"
                        + "          </td>"
                        + "          <td style=\"width:25%;padding:6px;vertical-align:top;\">"
                        + "            <table width=\"100%\" cellpadding=\"8\" cellspacing=\"0\" style=\"background:#fdecea;border:1px solid #f3c6c2;border-radius:8px;\">"
                        + "              <tr><td style=\"font-size:18px;font-weight:700;color:#a94442;text-align:center;\">"+totalScenarioFailedCount+"</td></tr>"
                        + "              <tr><td style=\"font-size:13px;color:#8a2b2b;text-align:center;\">Failed</td></tr>"
                        + "            </table>"
                        + "          </td>"
                        + "          <td style=\"width:25%;padding:6px;vertical-align:top;\">"
                        + "            <table width=\"100%\" cellpadding=\"8\" cellspacing=\"0\" style=\"background:#fff8e6;border:1px solid #f0e0b8;border-radius:8px;\">"
                        + "              <tr><td style=\"font-size:18px;font-weight:700;color:#7a5b18;text-align:center;\">"+totalScenarioSkippedCount+"</td></tr>"
                        + "              <tr><td style=\"font-size:13px;color:#6b592d;text-align:center;\">Skipped</td></tr>"
                        + "            </table>"
                        + "          </td>"
                        + "        </tr>"
                        + "      </table>"
                        + "    </td>"
                        + "  </tr>"
                        + "</table>";

        html.append("<div class='section'>")
                .append("</br><h2 style=\"font-family:Arial,Helvetica,sans-serif;font-size:18px;text-decoration:underline;color:#0b57a4;\">Overall Summary</h2>")
                .append("<div class='section-body'>");
        html.append(summaryTable);
        html.append("</div></div>");


        if (totalScenarioFailedCount > 0) {
            html.append("<div class='note'><b>Note</b>: QE will perform post-validation failure analysis and circulate key findings <b>within 45 minutes</b> to all stakeholders.</div>");
        }

        html.append("<div style='height:12px'></div>")
                .append("<img src='cid:pieChart' alt='Overall Summary Chart'>")
                .append("</div></div>");

        html.append("<div class='section'>")
//                .append("<div class='section-head-teal'>Feature-wise Summary</div>")
                .append("</br><h2 style=\"font-family:Arial,Helvetica,sans-serif;font-size:16px;text-decoration:underline;color:#0b57a4;\">Feature-wise Summary</h2>")
                .append("<div class='section-body'>")
                .append("<table id=\"feature-details\" " + TABLE_ATTR + " style=\"" + TABLE_STYLE + "margin-bottom:12px;\"><thead><tr>")
//                .append("<table class='modern'><thead><tr>")
                .append("<th " + TH_STYLE + ">Feature/Page</th><th " + TH_STYLE + ">Total</th><th " + TH_STYLE + ">Passed</th><th " + TH_STYLE + ">Failed</th><th " + TH_STYLE + ">Skipped</th>")
                .append("</tr\"></thead><tbody>")
                .append(featureSummaryRows)
                .append("</tbody></table>")
                .append("<div style='height:12px'></div>")
                .append("<br><img src='cid:barChart' alt='Feature Summary Chart'><br>")
                .append("</div></div>");

        html.append("<div class='section' id='details'>")
                .append("</br><h2 style=\"font-family:Arial,Helvetica,sans-serif;font-size:16px;text-decoration:underline;color:#0b57a4;\">Feature-wise Scenario Execution Details</h2>")
                .append("<hr style=\"border:none;border-bottom:1px solid #0b57a4;margin:2px 0;\">")
//                .append("<div class='section-head-teal'>Feature-wise Scenario Execution Details</div>")
                .append("<div class='section-body'>")
                .append(featureDetailsBlocks)
                .append("</div></div>");


        html.append("<br><br><footer style=\"font-family:Arial,Helvetica,sans-serif;font-size:13px;color:#555;text-align:center;margin-top:20px;padding-top:10px;border-top:1px solid #ddd;\">")
                .append("This report was generated automatically by the QE Team.<br>")
                .append("&copy; ").append(new SimpleDateFormat("yyyy").format(new Date())).append(" QE Team. All rights reserved.")
                .append("</footer>")
                .append("</div></body></html>");;

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

    // add helper (place near other private methods)
    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
