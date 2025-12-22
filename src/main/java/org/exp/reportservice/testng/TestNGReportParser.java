// java
package org.exp.reportservice.testng;

import org.exp.reportservice.commons.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.exp.reportservice.commons.CommonFunctions.overAllSummary;
import static org.exp.reportservice.commons.CommonFunctions.statusPill;
import static org.exp.reportservice.commons.HeaderAndFooter.*;
import static org.exp.reportservice.constants.GlobalConstants.setHtmlBuilder;


public class TestNGReportParser {

    static int totalMethodsSize = 0;

    static int totalMethodsPassedCount = 0;
    static int totalMethodsFailedCount = 0;
    static int totalMethodsSkippedCount = 0;
    static List<TestNGResult> testNgResults = new ArrayList<>();
    static final String TABLE_STYLE = "border-collapse:separate;font-family:Arial,sans-serif;font-size:12px;width:100%;background:#ffffff;border-radius:8px;overflow:hidden;border-spacing:0;";
    static final String TH_STYLE = "style=\"background-color:#0b57a4;color:#fff;padding:12px 10px;border-bottom:2px solid #1565c0;border-right:1px solid #e6eef6;text-align:left;font-weight:bold;font-size:14px;font-family:Arial,sans-serif;\"";
    static final String TD_STYLE = "style=\"padding:10px 8px;border:1px solid #eef3fb;text-align:left;vertical-align:top;color:#223047;background:#ffffff;font-size:13px;\"";
    static final String TABLE_ATTR = "border=\"0\" cellpadding=\"6\" cellspacing=\"0\"";
    static final String CAPTION_STYLE = "style=\"text-align:left;font-weight:700;padding:8px 6px;font-size:13px;color:#0b2b4a;\"";

    public static StringBuilder parser(String noteOnFailure, String applicationName, Path testNgResultsXml) throws Exception {
        StringBuilder htmlBuilder = new StringBuilder();


        setHeader(htmlBuilder, applicationName);
        String summaryTable = overAllSummary("Methods").toString();

        System.out.println(summaryTable);

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

                    boolean hasFail = false, hasSkip = false, hasPass = false;
                    NodeList elementsByTagName = testClassesElement.getElementsByTagName("test-method"); //get all METHODS in CLASSES

                    // We'll accumulate rows for this class, detect aggregate class status by scanning methods first,
                    // then prepend the class/test header rows so class status is accurate (dynamic).
                    StringBuilder classRowsBuilder = new StringBuilder();
                    // iterate methods and build method rows into the class buffer
                    for (Node testClassMethodName_nodes : nodeListIterable(elementsByTagName)) { //loop through METHODS in CLASSES
                        String method_errorMessage = "";

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

                            method_errorMessage = readErrorMessage(testClassesMethodElement);

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
                        classRowsBuilder.append("<td " + TD_STYLE + method_errorMessage + ">").append(statusPill(methodStatus, 10, 1200)).append("</td>");
                        classRowsBuilder.append("<td " + TD_STYLE + ">").append("").append("</td>");
                        classRowsBuilder.append("</tr>");

                        testNgResults.add(new TestNGResult(suiteName, testName, testClassName, testMethodName, testStatus, methodStatus.toLowerCase(), testDurationMS));
                    } //Loop through METHODS in CLASSES

                    // Determine aggregate class status dynamically after scanning methods
                    String classStatus = hasFail ? "FAIL" : (hasSkip && !hasPass ? "SKIP" : "PASS");

                    // Prepend the header rows for test and class (only once per class)
                    scnHtmlBuilder.append("<a id=\"methods-details-table\" name=\"methods-details-table\"></a>");
                    scnHtmlBuilder.append("<tr id=\"feature_"+testName+"  name = \"feature_"+testName+" \">");
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


        summaryTable = summaryTable.replace("{methodsTotalCount}", String.valueOf(totalMethodsSize))
                .replace("{passedBadge}", String.valueOf(totalMethodsPassedCount))
                .replace("{failedBadge}", String.valueOf(totalMethodsFailedCount))
                .replace("{skippedBadge}", String.valueOf(totalMethodsSkippedCount));

        htmlBuilder.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" role=\"presentation\">\n" +
                "  <tr>\n" +
                "    <td height=\"6\" style=\"font-size:12px;line-height:6px;mso-line-height-rule:exactly;\">&nbsp;</td>\n" +
                "  </tr>\n" +
                "</table>");//Line height before summary

        htmlBuilder.append(summaryTable);




        if(noteOnFailure!=null && !noteOnFailure.isBlank() && totalMethodsFailedCount > 0) {
            htmlBuilder.append("<div style=\"font-family:Arial,sans-serif;border:1px solid #ffd89c;background:#fff7e6;color:#7a4b00;border-radius:10px;padding:4px 14px;margin-top:12px;font-size:15px;\"> <b>Note</b>: "+noteOnFailure+"</div>");
        }
        htmlBuilder.append("<img src='cid:pie' alt='pie'>");

        htmlBuilder.append(featuresTableBuilder);
                                                                                                                                                                                                                                                                                                    htmlBuilder.append("</div><br><img src='cid:bar' alt='bar'><br>")
                .append("<h2 style=\"font-family:Arial,Helvetica,sans-serif;font-size:16px;text-decoration:underline;color:#0b57a4;\">Feature-wise Scenario Execution Details</h2>")
                .append("<hr style=\"border:none;border-bottom:1px solid #0b57a4;margin:2px 0;\">")
                .append(scnHtmlBuilder);
        footer_backToAllTheWayTop(htmlBuilder);
        setFooter(htmlBuilder);

        System.out.println("---------------------------");
        System.out.println(htmlBuilder);
        setHtmlBuilder(new StringBuilder(htmlBuilder));
        HTMLReportGeneration.htmlReportGenerator();
        System.out.println("---------------------------");
        return htmlBuilder;
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

    private static String readErrorMessage(Element testClassesMethodElement) {
        String method_errorMessage = "";
        NodeList exceptionNodes = testClassesMethodElement.getElementsByTagName("exception");
        if (exceptionNodes.getLength() > 0) {
            Element ex = (Element) exceptionNodes.item(0);
            NodeList fullStack = ex.getElementsByTagName("full-stacktrace");
            if (fullStack.getLength() > 0) {
                method_errorMessage = fullStack.item(0).getTextContent();
            } else {
                NodeList msg = ex.getElementsByTagName("message");
                if (msg.getLength() > 0) {
                    method_errorMessage = msg.item(0).getTextContent();
                } else {
                    method_errorMessage = ex.getTextContent();
                }
            }
            if (method_errorMessage != null) method_errorMessage = method_errorMessage.trim();
        }

        String safeError = escapeHtml(method_errorMessage == null ? "" : method_errorMessage)
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .replace("\n", "&#10;")   // preserve line breaks in HTML title
                .replace("'", "&#39;")
                .replace("\"", "&quot;");
        return safeError.isEmpty() ? "" : " title='" + safeError + "'";
    }
}