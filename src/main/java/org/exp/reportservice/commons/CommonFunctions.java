package org.exp.reportservice.commons;

import static org.exp.reportservice.commons.HeaderAndFooter.escapeHtml;

public class CommonFunctions {

    private static String getSummaryHTML(String methodsOrScenarios){
        return  "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family:Arial,sans-serif;margin:0;padding-top:12px;\">"
                + "  <tr>"
                + "    <td style=\"padding:6px;\">"
                + "      <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse:collapse;\">"
                + "        <tr>"
                + "          <td style=\"width:25%;padding:6px;vertical-align:top;\">"
                + "            <table width=\"100%\" cellpadding=\"8\" cellspacing=\"0\" style=\"background:#f7f7f7;border:1px solid #e6e6e6;border-radius:8px;\">"
                + "              <tr><td style=\"font-size:18px;font-weight:700;color:#333;text-align:center;\"> <a href=\"#"+methodsOrScenarios+"-details-table\" title=\"Jump to details\" style=\"text-decoration:none;color:inherit;\">"
                + "               <span style=\"display:inline-block;font-size:20px;font-weight:700;color:#333;line-height:1;text-align:center;text-decoration:underline;\">"
                + "               {methodsTotalCount}</span></a></td></tr>"
                + "              <tr><td style=\"font-size:13px;color:#666;text-align:center;\">Total "+methodsOrScenarios+"</td></tr>"
                + "            </table>"
                + "          </td>"
                + "          <td style=\"width:25%;padding:6px;vertical-align:top;\">"
                + "            <table width=\"100%\" cellpadding=\"8\" cellspacing=\"0\" style=\"background:#e9f7ec;border:1px solid #d6eed6;border-radius:8px;\">"
                + "              <tr><td style=\"font-size:18px;font-weight:700;color:#2d6a33;text-align:center;\"><a href=\"#"+methodsOrScenarios+"-details-table\" title=\"Jump to details\" style=\"text-decoration:none;color:inherit;\">"
                + "               <span style=\"display:inline-block;font-size:20px;font-weight:700;color:#2d6a33;line-height:1;text-align:center;text-decoration:underline;\">"
                + "               {passedBadge}</span></a></td></tr>"
                + "              <tr><td style=\"font-size:13px;color:#466b3f;text-align:center;\">Passed</td></tr>"
                + "            </table>"
                + "          </td>"
                + "          <td style=\"width:25%;padding:6px;vertical-align:top;\">"
                + "            <table width=\"100%\" cellpadding=\"8\" cellspacing=\"0\" style=\"background:#fdecea;border:1px solid #f3c6c2;border-radius:8px;\">"
                + "              <tr><td style=\"font-size:18px;font-weight:700;color:#a94442;text-align:center;\"><a href=\"#"+methodsOrScenarios+"-details-table\" title=\"Jump to details\" style=\"text-decoration:none;color:inherit;\">"
                + "                <span style=\"display:inline-block;font-size:20px;font-weight:700;color:#a94442;line-height:1;text-align:center;text-decoration:underline;\">"
                + "                {failedBadge}</span></a></td></tr>"
                + "              <tr><td style=\"font-size:13px;color:#8a2b2b;text-align:center;\">Failed</td></tr>"
                + "            </table>"
                + "          </td>"
                + "          <td style=\"width:25%;padding:6px;vertical-align:top;\">"
                + "            <table width=\"100%\" cellpadding=\"8\" cellspacing=\"0\" style=\"background:#fff8e6;border:1px solid #f0e0b8;border-radius:8px;\">"
                + "              <tr><td style=\"font-size:18px;font-weight:700;color:#7a5b18;text-align:center;\"><a href=\"#"+methodsOrScenarios+"-details-table\" title=\"Jump to details\" style=\"text-decoration:none;color:inherit;\">"
                + "                <span style=\"display:inline-block;font-size:20px;font-weight:700;color:#7a5b18;line-height:1;text-align:center;text-decoration:underline;\">"
                + "                {skippedBadge}</span></a></td></tr>"
                + "              <tr><td style=\"font-size:13px;color:#6b592d;text-align:center;\">Skipped</td></tr>"
                + "            </table>"
                + "          </td>"
                + "        </tr>"
                + "      </table>"
                + "    </td>"
                + "  </tr>"
                + "</table>";
    }
    public static StringBuilder overAllSummary(String methodsOrScenarios) {
        return new StringBuilder()
                .append("<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"margin:0;padding:0;\">")
                .append("<tr><td style=\"padding:0;\">")
                .append("<div style=\"margin:0;padding:0;\">")
                .append("<h2 style=\"font-family:Arial,Helvetica,sans-serif;font-size:18px;text-decoration:underline;color:#0b57a4;margin:0;padding:0;\">Overall Summary</h2>")
                .append("</div>")
                .append("</td></tr></table>")
                .append(getSummaryHTML(methodsOrScenarios))
                .append("<div class='section-body' style='margin:-24px 0 8px;'>")
                .append("</div>");

    }

    public static String statusPill(String statusRaw) {
        return  statusPillCode(statusRaw, 0, 0);
    }
    public static String statusPill(String statusRaw, int fontSize, int fontWeight) {
        return  statusPillCode(statusRaw, fontSize, fontWeight);
    }


    private static String statusPillCode(String statusRaw, int fontSize, int fontWeight){
        int _fontSize = fontSize == 0 ? 12 : fontSize;

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
