package org.exp.reportservice.commons;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HeaderAndFooter {

    public static void setHeader(StringBuilder htmlBuilder, String applicationName){
        String formattedDate = new SimpleDateFormat("d-MMM-yyyy").format(new Date());
        htmlBuilder.append("<html><head><meta charset='utf-8'>");
        htmlBuilder.append("<a id=\"reportDetails\" name=\"reportDetails\"></a>");
        htmlBuilder.append("<table width=\"100%\" border=\"0\" cellpadding=\"8\" cellspacing=\"0\" bgcolor=\"#f8fafc\" style=\"background:#f8fafc;width:100%;border:1px solid #e6eef6;margin-top:4px\">");
        htmlBuilder.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"background:#f0f6ff;border:1px solid #e6eef6;width:100%;\">")
                .append("<tr><td align=\"left\" style=\"padding:10px 20px;font-family:Arial,Helvetica,sans-serif;color:#223047;\">")
                .append("<div style=\"font-size:18px;font-weight:700;color:#0b1220;line-height:1.2;margin:-0 0 8px 0;\">Automation Test Execution Report</div>")
                .append("<div style=\"font-size:12px;color:#475569;line-height:1.0;margin:0;\">")
                .append("<strong style=\"font-weight:700;\">Application:</strong> ").append(escapeHtml(applicationName))
                .append(" &nbsp;&nbsp; <strong style=\"font-weight:700;\">Date:</strong> ").append(formattedDate)
                .append(" &nbsp;&nbsp; <strong style=\"font-weight:700;\">Executed By:</strong> QE Team")
                .append("</div>")
                .append("</td></tr></table>");
    }

    public static void footer_backToAllTheWayTop(StringBuilder htmlBuilder) {
        htmlBuilder.append("<a id=\"feature-details-last\" name=\"feature-details-last\"></a>")
                .append("<div style=\"text-align:left;margin-top:8px;margin-bottom:16px;\">"
                        + "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"display:inline-table;vertical-align:middle;border-collapse:separate;\">"
                        + "<tr><td style=\"padding:0;\">"
                        + "<a href=\"#reportDetails\" aria-label=\"Back to top\" title=\"Jump to top of the report\" style=\"display:inline-block;background:linear-gradient(180deg,#eaf4ff 0%,#f0f6ff 100%);color:#0b57a4;text-decoration:none;padding:6px 10px;border-radius:8px;font-weight:700;font-family:Arial,Helvetica,sans-serif;font-size:12px;line-height:1;border:1px solid #d6e9ff;box-shadow:none;\">"
                        + "&#8679;&nbsp;Back to top"
                        + "</a></td></tr></table>"
                        + "</div>");
    }
    public static void setFooter(StringBuilder htmlBuilder) {
        htmlBuilder.append("<br><br><footer style=\"font-family:Arial,Helvetica,sans-serif;font-size:13px;color:#555;text-align:center;margin-top:20px;padding-top:10px;border-top:1px solid #ddd;\">")
                .append("This report was generated automatically by the QE Team.<br>")
                .append("&copy; ").append(new SimpleDateFormat("yyyy").format(new Date())).append(" QE Team. All rights reserved.")
                .append("</footer>");
    }
    /**
     * Render a compact status "pill" for PASS / FAIL / SKIP (email-friendly inline styles).
     */
    public static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
}
