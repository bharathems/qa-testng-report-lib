// java
package org.exp.reportservice.testng;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;


public class TestNGReportParser {
    static int totalMethodsSize = 0;

    static int totalMethodsPassedCount = 0;
    static int totalMethodsFailedCount = 0;
    static int totalMethodsSkippedCount = 0;
    static List<TestNGResult> testNgResults = new ArrayList<>();

    // Helper to create an inline badge compatible with email clients (Outlook-friendly)
    private static String badge(String text, String bgColor, String textColor) {
        return "<span style=\"display:inline-block;padding:4px 8px;margin:0;border-radius:4px;background:" + bgColor
                + ";color:" + textColor + ";font-size:12px;font-weight:bold;border:1px solid rgba(0,0,0,0.06);\">" + text + "</span>";
    }

    public static StringBuilder parser(String applicationName, Path testNgResultsXml) throws Exception {
        StringBuilder htmlBuilder = new StringBuilder();
        String formattedDate = new SimpleDateFormat("d-MMM-yyyy").format(new Date());


        final String TABLE_STYLE = "border-collapse:separate;font-family:Arial,sans-serif;font-size:12px;width:100%;background:#ffffff;border-radius:8px;overflow:hidden;border-spacing:0;";
        final String TH_STYLE = "style=\"background-color:#0b57a4;color:#fff;padding:12px 10px;border-bottom:2px solid #1565c0;border-right:1px solid #e6eef6;text-align:left;font-weight:bold;font-size:14px;font-family:Arial,sans-serif;\"";
        final String TD_STYLE = "style=\"padding:10px 8px;border:1px solid #eef3fb;text-align:left;vertical-align:top;color:#223047;background:#ffffff;font-size:13px;\"";
        final String TABLE_ATTR = "border=\"0\" cellpadding=\"6\" cellspacing=\"0\"";
        final String CAPTION_STYLE = "style=\"text-align:left;font-weight:700;padding:8px 6px;font-size:13px;color:#0b2b4a;\"";
        // Styles for sections and headings
        final String SECTION_STYLE = "style=\"background:#f8fafc;border-radius:8px;padding:20px;margin:16px 0;box-shadow:0 2px 8px #e2e8f0;\"";
        final String SECTION_HEAD_STYLE = "style=\"font-size:18px;font-weight:700;color:#0b57a4;margin-bottom:8px;font-family:Arial,Helvetica,sans-serif;\"";
        final String SECTION_BODY_STYLE = "style=\"font-size:14px;color:#16325c;font-family:Arial,Helvetica,sans-serif;\"";
        htmlBuilder.append("<a id=\"reportDetails\" name=\"reportDetails\"></a>");
        htmlBuilder.append("<table width=\"100%\" border=\"0\" cellpadding=\"8\" cellspacing=\"0\" bgcolor=\"#f8fafc\" style=\"background:#f8fafc;width:100%;border:1px solid #e6eef6;\">");
        htmlBuilder.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"background:#f0f6ff;border:1px solid #e6eef6;width:100%;\">")
                .append("<tr><td align=\"left\" style=\"padding:20px;font-family:Arial,Helvetica,sans-serif;color:#223047;\">")
                .append("<div style=\"font-size:22px;font-weight:700;color:#0b1220;line-height:1.2;margin:0 0 8px 0;\">Automation Test Execution Report</div>")
                .append("<div style=\"font-size:13px;color:#475569;line-height:1.4;margin:0;\">")
                .append("<strong style=\"font-weight:700;\">Application:</strong> ").append(escapeHtml(applicationName))
                .append(" &nbsp;&nbsp; <strong style=\"font-weight:700;\">Date:</strong> ").append(formattedDate)
                .append(" &nbsp;&nbsp; <strong style=\"font-weight:700;\">Executed By:</strong> QE Team")
                .append("</div>")
                .append("</td></tr></table>");

        String summaryTableMain =
                "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family:Arial,sans-serif;margin-bottom:16px;\">"
                        + "  <tr>"
                        + "    <td style=\"padding:8px;\">"
                        + "      <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse:collapse;\">"
                        + "        <tr>"
                        + "          <td style=\"width:25%;padding:8px;vertical-align:top;\">"
                        + "            <table width=\"100%\" cellpadding=\"10\" cellspacing=\"0\" style=\"background:#f7f7f7;border:1px solid #e6e6e6;border-radius:8px;\">"
                        + "              <tr><td style=\"font-size:22px;font-weight:700;color:#333;text-align:center;\">{methodsTotalCount}</td></tr>"
                        + "              <tr><td style=\"font-size:13px;color:#666;text-align:center;\">Total Methods</td></tr>"
                        + "            </table>"
                        + "          </td>"
                        + "          <td style=\"width:25%;padding:8px;vertical-align:top;\">"
                        + "            <table width=\"100%\" cellpadding=\"10\" cellspacing=\"0\" style=\"background:#e9f7ec;border:1px solid #d6eed6;border-radius:8px;\">"
                        + "              <tr><td style=\"font-size:22px;font-weight:700;color:#2d6a33;text-align:center;\">{passedBadge}</td></tr>"
                        + "              <tr><td style=\"font-size:13px;color:#466b3f;text-align:center;\">Passed</td></tr>"
                        + "            </table>"
                        + "          </td>"
                        + "          <td style=\"width:25%;padding:8px;vertical-align:top;\">"
                        + "            <table width=\"100%\" cellpadding=\"10\" cellspacing=\"0\" style=\"background:#fdecea;border:1px solid #f3c6c2;border-radius:8px;\">"
                        + "              <tr><td style=\"font-size:22px;font-weight:700;color:#a94442;text-align:center;\">{failedBadge}</td></tr>"
                        + "              <tr><td style=\"font-size:13px;color:#8a2b2b;text-align:center;\">Failed</td></tr>"
                        + "            </table>"
                        + "          </td>"
                        + "          <td style=\"width:25%;padding:8px;vertical-align:top;\">"
                        + "            <table width=\"100%\" cellpadding=\"10\" cellspacing=\"0\" style=\"background:#fff8e6;border:1px solid #f0e0b8;border-radius:8px;\">"
                        + "              <tr><td style=\"font-size:22px;font-weight:700;color:#7a5b18;text-align:center;\">{skippedBadge}</td></tr>"
                        + "              <tr><td style=\"font-size:13px;color:#6b592d;text-align:center;\">Skipped</td></tr>"
                        + "            </table>"
                        + "          </td>"
                        + "        </tr>"
                        + "      </table>"
                        + "    </td>"
                        + "  </tr>"
                        + "</table>";

        String summaryTable =
                "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family:Arial,sans-serif;margin-bottom:16px;\">"
                        + "  <tr>"
                        + "    <td style=\"padding:6px;\">"
                        + "      <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse:collapse;\">"
                        + "        <tr>"
                        + "          <td style=\"width:25%;padding:6px;vertical-align:top;\">"
                        + "            <table width=\"100%\" cellpadding=\"8\" cellspacing=\"0\" style=\"background:#f7f7f7;border:1px solid #e6e6e6;border-radius:8px;\">"
                        + "              <tr><td style=\"font-size:18px;font-weight:700;color:#333;text-align:center;\">{methodsTotalCount}</td></tr>"
                        + "              <tr><td style=\"font-size:13px;color:#666;text-align:center;\">Total Methods</td></tr>"
                        + "            </table>"
                        + "          </td>"
                        + "          <td style=\"width:25%;padding:6px;vertical-align:top;\">"
                        + "            <table width=\"100%\" cellpadding=\"8\" cellspacing=\"0\" style=\"background:#e9f7ec;border:1px solid #d6eed6;border-radius:8px;\">"
                        + "              <tr><td style=\"font-size:18px;font-weight:700;color:#2d6a33;text-align:center;\">{passedBadge}</td></tr>"
                        + "              <tr><td style=\"font-size:13px;color:#466b3f;text-align:center;\">Passed</td></tr>"
                        + "            </table>"
                        + "          </td>"
                        + "          <td style=\"width:25%;padding:6px;vertical-align:top;\">"
                        + "            <table width=\"100%\" cellpadding=\"8\" cellspacing=\"0\" style=\"background:#fdecea;border:1px solid #f3c6c2;border-radius:8px;\">"
                        + "              <tr><td style=\"font-size:18px;font-weight:700;color:#a94442;text-align:center;\">{failedBadge}</td></tr>"
                        + "              <tr><td style=\"font-size:13px;color:#8a2b2b;text-align:center;\">Failed</td></tr>"
                        + "            </table>"
                        + "          </td>"
                        + "          <td style=\"width:25%;padding:6px;vertical-align:top;\">"
                        + "            <table width=\"100%\" cellpadding=\"8\" cellspacing=\"0\" style=\"background:#fff8e6;border:1px solid #f0e0b8;border-radius:8px;\">"
                        + "              <tr><td style=\"font-size:18px;font-weight:700;color:#7a5b18;text-align:center;\">{skippedBadge}</td></tr>"
                        + "              <tr><td style=\"font-size:13px;color:#6b592d;text-align:center;\">Skipped</td></tr>"
                        + "            </table>"
                        + "          </td>"
                        + "        </tr>"
                        + "      </table>"
                        + "    </td>"
                        + "  </tr>"
                        + "</table>";

        // Use alternating row backgrounds for the feature summary table (email-friendly using bgcolor)
        StringBuilder featuresTableBuilder = new StringBuilder();
        featuresTableBuilder.append("<table " + TABLE_ATTR + " style=\"" + TABLE_STYLE + "margin-bottom:10px;\">")
                .append("<tr><th " + TH_STYLE + ">Test name</th><th " + TH_STYLE + ">Total Methods</th><th " + TH_STYLE + ">Passed</th><th " + TH_STYLE + ">Failed</th><th " + TH_STYLE + ">Skipped</th></tr>");
        int featureRowIndex = 0;
        if (testNgResultsXml == null || !Files.isRegularFile(testNgResultsXml)) {
            throw new IllegalArgumentException("File not found: " + testNgResultsXml);
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc;
        try (InputStream is = Files.newInputStream(testNgResultsXml)) {
            doc = db.parse(is);
        }

        NodeList suiteNames = doc.getElementsByTagName("suite"); //get all suites
        StringBuilder scnHtmlBuilder = new StringBuilder();

        for (Node suiteNode : nodeListIterable(suiteNames)) { // loop through suites
            if (suiteNode.getNodeType() != Node.ELEMENT_NODE) continue;
            Element suiteElement = (Element) suiteNode;
            String suiteName = suiteElement.getAttribute("name");

            scnHtmlBuilder.append("<table id=\"feature-details\" " + TABLE_ATTR + " style=\"" + TABLE_STYLE + "margin-bottom:12px;\">")
                    .append("<caption " + CAPTION_STYLE + ">"
                            + " <span style=\"color:#0b57a4;font-weight:700;font-family:Arial,Helvetica,sans-serif;\">SUITE NAME - </span>" + escapeHtml(suiteName) + "</caption>")
                    .append("<tr>")
                    .append("<th " + TH_STYLE + ">Test Name</th>")
                    .append("<th " + TH_STYLE + ">Class Name</th>")
                    .append("<th " + TH_STYLE + ">Method Name</th>")
                    .append("<th " + TH_STYLE + ">Status</th>")
                    .append("<th " + TH_STYLE + ">Time (ms/s)</th>")
                    .append("</tr>");

            String createTestName = "<tr>\n" + "<td colspan=\"8\" " + TD_STYLE + " style=\"text-align:center;color:lightcoral;font-weight:bold;\">" + suiteElement.getAttribute("name") + "(" + suiteElement.getAttribute("duration-ms") + ")</td>\n" + "</tr>";

            scnHtmlBuilder.append("<tr><td " + TD_STYLE + " colspan=\"4\" style=\"text-align:center;font-weight:bold;\">").append(suiteName).append("</td></tr>");

            NodeList suite_test = suiteElement.getElementsByTagName("test"); //get all tests in suites
            for (Node suite_testNodes : nodeListIterable(suite_test)) { //loop through tests in suites
                if (suite_testNodes.getNodeType() != Node.ELEMENT_NODE) continue;
                int methodsSize = 0;
                int methodsPassedCount = 0;
                int methodsFailedCount = 0;
                int methodsSkippedCount = 0;
                Element testElement = (Element) suite_testNodes;
                String testName = testElement.getAttribute("name");
                String testDurationMS;
                String durationAttr = testElement.getAttribute("duration-ms");
                if (durationAttr == null || durationAttr.isEmpty()) {
                    testDurationMS = "";
                } else {
                    try {
                        long durationMs = Long.parseLong(durationAttr);
                        if (durationMs < 1000) {
                            testDurationMS = durationMs + " ms";
                        } else {
                            long durationSec = durationMs / 1000;
                            testDurationMS = durationSec + " s";
                        }
                    } catch (NumberFormatException e) {
                        testDurationMS = durationAttr;
                    }
                }
                String testStatus = getTestMethodStatus(testElement);
                String color = testStatus.equalsIgnoreCase("fail") ? "red" : "green";
                createTestName += " <tr>\n" + "<td " + TD_STYLE + ">" + testName + "</td>\n" + "<td " + TD_STYLE + " style=\"text-align:left;\">&nbsp;</td>" + "<td " + TD_STYLE + " style=\"color:" + color + ";font-weight:bold;\">" + statusPill(testStatus) + "</td>\n" + "<td " + TD_STYLE + "> " + testDurationMS + " </td>\n" + "</tr>";

                //loop through classes
                NodeList testClasses = testElement.getElementsByTagName("class");//get all classes in tests

                boolean firstRowForTest = true;
                for (Node testClass_nodes : nodeListIterable(testClasses)) { // loop through classes in tests
                    if (testClass_nodes.getNodeType() != Node.ELEMENT_NODE) continue;
                    Element testClassesElement = (Element) testClass_nodes;
                    String testClassName = testClassesElement.getAttribute("name");// class name
//                    String simpleClassName = (testClassName == null) ? "" :
//                            (testClassName.contains(".") ? testClassName.substring(testClassName.lastIndexOf('.') + 1) : testClassName);
                    boolean hasFail = false, hasSkip = false, hasPass = false;
                    NodeList elementsByTagName = testClassesElement.getElementsByTagName("test-method"); //get all METHODS in CLASSES

                    // We'll accumulate rows for this class, detect aggregate class status by scanning methods first,
                    // then prepend the class/test header rows so class status is accurate (dynamic).
                    StringBuilder classRowsBuilder = new StringBuilder();
                    // iterate methods and build method rows into the class buffer
                    for (Node testClassMethodName_nodes : nodeListIterable(elementsByTagName)) { //loop through METHODS in CLASSES

                        if (testClassMethodName_nodes.getNodeType() != Node.ELEMENT_NODE) continue;

                        Element testClassesMethodElement = (Element) testClassMethodName_nodes;
                        // Skip TestNG config methods (before/after/config) - only process real test methods
                        if ("true".equalsIgnoreCase(testClassesMethodElement.getAttribute("is-config"))) {
                            continue;
                        }

                        String testMethodName = testClassesMethodElement.getAttribute("name");
                        String methodStatus = testClassesMethodElement.getAttribute("status");//Method status

                        if (methodStatus.equalsIgnoreCase("fail")) {
                            hasFail = true;
                            totalMethodsFailedCount++;
                            totalMethodsSize++;
                            methodsFailedCount++;
                            methodsSize++;
                        } else if (methodStatus.equalsIgnoreCase("pass")) {
                            hasPass = true;
                            totalMethodsPassedCount++;
                            totalMethodsSize++;
                            methodsPassedCount++;
                            methodsSize++;
                        } else {
                            hasSkip = true;
                            totalMethodsSkippedCount++;
                            totalMethodsSize++;
                            methodsSkippedCount++;
                            methodsSize++;
                        }

                        // Build a full row for this method and append to class buffer
                        classRowsBuilder.append("<tr>");
                        classRowsBuilder.append("<td " + TD_STYLE + ">").append("").append("</td>");
//                        classRowsBuilder.append("<td " + TD_STYLE + "><div style=\"text-align:center;\">").append("&nbsp;").append("</div></td>");
                        classRowsBuilder.append("<td " + TD_STYLE + " align=\"center\">").append("&nbsp;").append("</td>");
                        classRowsBuilder.append("<td " + TD_STYLE + ">").append(escapeHtml(testMethodName)).append("</td>");
                        classRowsBuilder.append("<td " + TD_STYLE + ">").append(statusPill(methodStatus, 10, 1200)).append("</td>");
                        classRowsBuilder.append("<td " + TD_STYLE + ">").append("").append("</td>");
                        classRowsBuilder.append("</tr>");

                        testNgResults.add(new TestNGResult(suiteName, testName, testClassName, testMethodName, testStatus, methodStatus.toLowerCase(), testDurationMS));
                    } //Loop through METHODS in CLASSES

                    // Determine aggregate class status dynamically after scanning methods
                    String classStatus = hasFail ? "FAIL" : (hasSkip && !hasPass ? "SKIP" : "PASS");

                    // Prepend the header rows for test and class (only once per class)
                    scnHtmlBuilder.append("<a id=\"feature-details-table\" name=\"feature-details-table\"></a>");
                    scnHtmlBuilder.append("<tr id=\"abc\">");
                    scnHtmlBuilder.append("<a id=\"feature_"+testName+"\" name=\"feature_"+testName+"\"></a>");
                    scnHtmlBuilder.append("<td " + TD_STYLE + ">").append(firstRowForTest ? escapeHtml(testName) : "").append("</td>");

                    scnHtmlBuilder.append("<td " + TD_STYLE + "><div style=\"text-align:left;\">").append("&nbsp;").append("</div></td>");
                    scnHtmlBuilder.append("<td " + TD_STYLE + "><div style=\"text-align:left;\">").append("&nbsp;").append("</div></td>");
                    scnHtmlBuilder.append("<td " + TD_STYLE + ">").append(firstRowForTest ? statusPill(testStatus) : "").append("</td>");
                    scnHtmlBuilder.append("<td " + TD_STYLE + ">").append(firstRowForTest ? testDurationMS : "").append("</td>");
                    scnHtmlBuilder.append("</tr>");

                    scnHtmlBuilder.append("<tr>");
                    scnHtmlBuilder.append("<td " + TD_STYLE + "><div style=\"text-align:left;\">").append("").append("</div></td>");
                    scnHtmlBuilder.append("<td " + TD_STYLE + "><div style=\"text-align:left;\">").append(escapeHtml(testClassName)).append("</div></td>");
                    scnHtmlBuilder.append("<td " + TD_STYLE + "><div style=\"text-align:left;\">").append("&nbsp;").append("</div></td>");
                    scnHtmlBuilder.append("<td " + TD_STYLE + ">").append(statusPill(classStatus, 11, 1200)).append("</td>");
                    scnHtmlBuilder.append("<td " + TD_STYLE + ">").append("").append("</td>");
                    scnHtmlBuilder.append("</tr>");

                    // Append collected method rows for the class
                    scnHtmlBuilder.append(classRowsBuilder.toString());
                    firstRowForTest = false;
                } //Classes

                String rowBg = (featureRowIndex % 2 == 0) ? "#ffffff" : "#fbfdff";
                featuresTableBuilder.append("<tr bgcolor=\"" + rowBg + "\">")
                        .append("<td " + TD_STYLE + "> <a href=\"#feature_" + testName + "\" style=\"text-decoration:underline;color:#223047;font-size:13px;line-height:1.2;font-weight:600;\">" + escapeHtml(testName.toUpperCase()) + "</a></td>")
                        .append("<td " + TD_STYLE + ">" + methodsSize + "</td>")
                        .append("<td " + TD_STYLE + ">" + methodsPassedCount + "</td>")
                        .append("<td " + TD_STYLE + ">" + methodsFailedCount + "</td>")
                        .append("<td " + TD_STYLE + ">" + methodsSkippedCount + "</td>")
                        .append("</tr>");
                featureRowIndex++;
            } //Tests
            scnHtmlBuilder.append("</table>").append("<hr style=\"border:none;border-bottom:1px solid #000;margin:16px 0;line-height:0;\">");
        }
        featuresTableBuilder.append("</table>");


        String passedBadgeHtml = "<a href=\"#feature-details-table\" title=\"Jump to details\" style=\"text-decoration:none;color:inherit;\">"
                + "<span style=\"display:inline-block;font-size:20px;font-weight:700;color:#2d6a33;line-height:1;text-align:center;text-decoration:underline;\">"
                + totalMethodsPassedCount + "</span></a>";
        String failedBadgeHtml = "<a href=\"#feature-details-table\" title=\"Jump to details\" style=\"text-decoration:none;color:inherit;\">"
                + "<span style=\"display:inline-block;font-size:20px;font-weight:700;color:#a94442;line-height:1;text-align:center;text-decoration:underline;\">"
                + totalMethodsFailedCount + "</span></a>";
        String skippedBadgeHtml = "<a href=\"#feature-details-table\" title=\"Jump to details\" style=\"text-decoration:none;color:inherit;\">"
                + "<span style=\"display:inline-block;font-size:20px;font-weight:700;color:#7a5b18;line-height:1;text-align:center;text-decoration:underline;\">"
                + totalMethodsSkippedCount + "</span></a>";
        String totalBadgeHtml = "<a href=\"#feature-details-table\" title=\"Jump to details\" style=\"text-decoration:none;color:inherit;\">"
                + "<span style=\"display:inline-block;font-size:20px;font-weight:700;color:#333;line-height:1;text-align:center;text-decoration:underline;\">"
                + totalMethodsSize + "</span></a>";
        summaryTable = summaryTable.replace("{methodsTotalCount}", totalBadgeHtml)
                .replace("{passedBadge}", passedBadgeHtml)
                .replace("{failedBadge}", failedBadgeHtml)
                .replace("{skippedBadge}", skippedBadgeHtml);
//        htmlBuilder.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#f8fafc\" style=\"background:#f8fafc;width:100%;\"><tr><td>");
//        htmlBuilder.append("<div class='section' " + SECTION_STYLE + " >")
//                .append("<div class='section-head' " + SECTION_HEAD_STYLE + " >Overall Summary</div>")
//                .append("<div class='section-body' " + SECTION_BODY_STYLE + " >")
        htmlBuilder.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" role=\"presentation\">\n" +
                "  <tr>\n" +
                "    <td height=\"6\" style=\"font-size:12px;line-height:6px;mso-line-height-rule:exactly;\">&nbsp;</td>\n" +
                "  </tr>\n" +
                "</table>");//Line height before summary
        htmlBuilder.append("<div class='section' style=\"margin-top:6px;\">")
            .append("<h2 style=\"font-family:Arial,Helvetica,sans-serif;font-size:18px;text-decoration:underline;color:#0b57a4;margin-top:0;\">Overall Summary</h2>")
                .append("<div class='section-body'>")
                .append(summaryTable);
        htmlBuilder.append("<img src='cid:pie' alt='pie'>");
//        htmlBuilder.append("<div class='section' " + SECTION_STYLE + " >")
//                .append("<div class='section-head' " + SECTION_HEAD_STYLE + " >Overall Summary</div>")
//                .append("<div class='section-body' " + SECTION_BODY_STYLE + " >")
//                .append(summaryTable)
        htmlBuilder.append(featuresTableBuilder);
        htmlBuilder.append("</div><br><img src='cid:bar' alt='bar'><br>")
//                .append(featuresTableBuilder)
                .append("<h2 style=\"font-family:Arial,Helvetica,sans-serif;font-size:16px;text-decoration:underline;color:#0b57a4;\">Feature-wise Scenario Execution Details</h2>")
                .append("<hr style=\"border:none;border-bottom:1px solid #0b57a4;margin:2px 0;\">")
                .append(scnHtmlBuilder)
                .append("<a id=\"feature-details-last\" name=\"feature-details-last\"></a>")
                .append("<div style=\"text-align:left;margin-top:8px;margin-bottom:16px;\">"
                        + "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"display:inline-table;vertical-align:middle;border-collapse:separate;\">"
                        + "<tr><td style=\"padding:0;\">"
                        + "<a href=\"#reportDetails\" aria-label=\"Back to top\" title=\"Jump to top of the report\" style=\"display:inline-block;background:linear-gradient(180deg,#eaf4ff 0%,#f0f6ff 100%);color:#0b57a4;text-decoration:none;padding:6px 10px;border-radius:8px;font-weight:700;font-family:Arial,Helvetica,sans-serif;font-size:12px;line-height:1;border:1px solid #d6e9ff;box-shadow:none;\">"
                        + "&#8679;&nbsp;Back to top"
                        + "</a></td></tr></table>"
                        + "</div>");
        htmlBuilder.append("<br><br><footer style=\"font-family:Arial,Helvetica,sans-serif;font-size:13px;color:#555;text-align:center;margin-top:20px;padding-top:10px;border-top:1px solid #ddd;\">")
                .append("This report was generated automatically by the QE Team.<br>")
                .append("&copy; ").append(new SimpleDateFormat("yyyy").format(new Date())).append(" QE Team. All rights reserved.")
                .append("</footer>");
//        htmlBuilder.append("</td></tr></table>");
        System.out.println("---------------------------");
        System.out.println(htmlBuilder);
        System.out.println("---------------------------");
        return htmlBuilder;
    }

    public static void printAsTable(List<TestNGResult> results) {
        if (results == null || results.isEmpty()) {
            System.out.println("No test-method entries found.");
            return;
        }

        // simple column widths
        int wSuite = Math.max("Suite name".length(), results.stream().mapToInt(r -> r.suiteName.length()).max().orElse(6));
        int wTest = Math.max("Test name".length(), results.stream().mapToInt(r -> r.testName.length()).max().orElse(6));
        int wClass = Math.max("Method name".length(), results.stream().mapToInt(r -> (r.className).length()).max().orElse(6));
        int wMethod = Math.max("Method name".length(), results.stream().mapToInt(r -> (r.testMethodName).length()).max().orElse(6));
        int wClassMethod = Math.max("Method name".length(), results.stream().mapToInt(r -> (r.className + "#" + r.testMethodName).length()).max().orElse(6));
        int wStatus = Math.max("Status".length(), results.stream().mapToInt(r -> r.methodStatus.length()).max().orElse(6));
        int wTime = Math.max("Time (ms)".length(), results.stream().mapToInt(r -> r.durationMs.length()).max().orElse(8));

        String fmt = "%-" + wSuite + "s | %-" + wTest + "s | %-" + wClass + "s | %-" + wStatus + "s | %" + wTime + "s%n";

        System.out.printf(fmt, "Suite Name", "Test name", "Class name", "Method name", "Status", "Time (ms)");
        System.out.println(String.join("", Collections.nCopies(wTest + wClass + wStatus + wTime + 9, "-")));

        for (TestNGResult r : results) {
            String methodName = (r.className == null || r.className.isEmpty()) ? r.testMethodName : r.className;
            System.out.printf(fmt, r.suiteName, r.testName, methodName, r.methodStatus, r.durationMs);
        }
    }

    static String getTestMethodStatus(Element testElement) {
        NodeList testMethods = testElement.getElementsByTagName("test-method");
        String methodStatus = "PASS";
        for (int k = 0; k < testMethods.getLength(); k++) {
            Node testMethod_nodes = testMethods.item(k);
            if (testMethod_nodes.getNodeType() == Node.ELEMENT_NODE) {
                Element testMethodElement = (Element) testMethod_nodes;
                String testMethodStatus = testMethodElement.getAttribute("status");
                if (testMethodStatus.equalsIgnoreCase("fail")) {
                    methodStatus = "FAIL";
                    break;
                }
            }
        }
        return methodStatus;
    }

    static Iterable<Node> nodeListIterable(final NodeList nl) {
        return new Iterable<Node>() {
            @Override
            public Iterator<Node> iterator() {
                return new Iterator<Node>() {
                    int idx = 0;
                    @Override public boolean hasNext() { return idx < nl.getLength(); }
                    @Override public Node next() { return nl.item(idx++); }
                    @Override public void remove() { throw new UnsupportedOperationException(); }
                };
            }
        };
    }
    /**
     * Render a compact status "pill" for PASS / FAIL / SKIP (email-friendly inline styles).
     */
    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }

    /**
     * Render a compact status "pill" for PASS / FAIL / SKIP (email-friendly inline styles).
     */
    private static String statusPill1(String statusRaw) {
        String status = (statusRaw == null) ? "UNKNOWN" : statusRaw.toUpperCase();
        String bg = "#f2f2f2";
        String color = "#333";
//        String icon = "";

        switch (status) {
            case "PASS":
                bg = "#e9f7ec"; color = "#1f7a3a";;
                break;
            case "FAIL":
                bg = "#fdecea"; color = "#a94442"; ;
                break;
            case "SKIP":
                bg = "#fff8e6"; color = "#a67b00";;
                break;
            default:
                bg = "#eef2f5"; color = "#475569";
        }

        // pill style: rounded, bold, centered; min-width keeps uniform look
        return "<span style=\"display:inline-block;padding:4px 10px;border-radius:999px;background:" + bg
                + ";color:" + color + ";font-weight:700;font-size:12px;min-width:56px;text-align:center;line-height:1;\">"
                + escapeHtml(status) + "</span>";
    }

    private static String statusPill(String statusRaw) {
        return  statusPillCode(statusRaw, 0, 0);
    }
    private static String statusPill(String statusRaw, int fontSize, int fontWeight) {
        return  statusPillCode(statusRaw, fontSize, fontWeight);
    }

    private static String statusPillCode_1(String statusRaw, int fontSize, int fontWeight){
        int _fontSize = fontSize == 0 ? 13 : fontSize;
        int _fontWeight = fontWeight == 0 ? 700 : fontWeight;
        String status = (statusRaw == null) ? "UNKNOWN" : statusRaw.toUpperCase();
        String bg = "#f2f2f2", color = "#333";
        switch (status) {
            case "PASS": bg = "#e9f7ec"; color = "#1f7a3a"; break;
            case "FAIL": bg = "#fdecea"; color = "#d32f2f"; break;
            case "SKIP": bg = "#fff8e6"; color = "#f9a825"; break;
            default: bg = "#eef2f5"; color = "#475569";
        }
        return "<span style=\"display:inline-block;"
                + "padding:8px 20px;"
                + "border-radius:999px;"
                + "background:" + bg + ";"
                + "color:" + color + ";"
                + "font-weight:"+fontWeight+";"
                + "font-size:"+fontSize+"px;"
                + "min-width:80px;"
                + "text-align:center;"
                + "line-height:1.2;"
                + "font-family:Arial,Helvetica,sans-serif;"
                + "box-shadow:0 2px 6px #e2e8f0;\">"
                + escapeHtml(status) + "</span>";
    }

    private static String statusPillCode(String statusRaw, int fontSize, int fontWeight){
        int _fontSize = fontSize == 0 ? 12 : fontSize;
        int _fontWeight = fontWeight == 0 ? 700 : fontWeight;
        String fontWeightCss = (_fontWeight >= 700) ? "bold" : String.valueOf(_fontWeight);

        String status = (statusRaw == null) ? "UNKNOWN" : statusRaw.toUpperCase();
        String bg = "#f2f2f2", color = "#333";
        switch (status) {
            case "PASS": bg = "#e9f7ec"; color = "#1f7a3a"; break;
            case "FAIL": bg = "#fdecea"; color = "#d32f2f"; break;
            case "SKIP": bg = "#fff8e6"; color = "#f9a825"; break;
            default: bg = "#eef2f5"; color = "#475569";
        }

        // Outlook-friendly pill: use a single-cell table with bgcolor and inline padding.
        StringBuilder pill = new StringBuilder();
        pill.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"display:inline-block;vertical-align:middle;border-collapse:separate;\">")
                .append("<tr>")
                .append("<td align=\"center\" valign=\"middle\" bgcolor=\"").append(bg).append("\"")
                .append(" style=\"padding:6px 12px; background:").append(bg).append("; color:").append(color)
                .append("; font-weight:").append("bold").append("; font-size:").append(_fontSize).append("px;")
                .append("font-family:Arial,Helvetica,sans-serif; line-height:1; text-align:center;")
                .append("border-radius:999px; -webkit-border-radius:999px; -moz-border-radius:999px;")
                .append("mso-border-alt:0; mso-padding-alt:6px 12px;\">");

        String content = escapeHtml(status);
//        if (_fontWeight >= 700) {
//            // ensure Outlook renders bold by using semantic tag
//            content = "<strong style=\"font-weight:inherit;\">" + content + "</strong>";
//        }
        content = "<strong style=\"font-weight:inherit;\">" + content + "</strong>";
        pill.append(content)
                .append("</td>")
                .append("</tr>")
                .append("</table>");

        return pill.toString();
    }

}