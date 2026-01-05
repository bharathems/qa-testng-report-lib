package org.exp.reportservice.commons;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.exp.reportservice.commons.HtmlToPdfConverter.convert;
import static org.exp.reportservice.constants.GlobalConstants.getHtmlBuilder;

public class HTMLReportGeneration {

    public static void htmlReportGenerator(){
        // save HTML to target/report.html
        String date = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
        Path targetDir = Paths.get("target");
        try {
            Files.createDirectories(targetDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Path reportHtml = targetDir.resolve("report-" + date + ".html");

        // fallback SVGs used when PNGs are not present
        String pieSvg = "<svg xmlns='http://www.w3.org/2000/svg' width='220' height='140'>"
                + "<rect width='100%' height='100%' fill='#ffffff'/>"
                + "<g transform='translate(60,70)'>"
                + "  <circle r='48' fill='#e9f7ec'/>"
                + "  <path d='M48 0 A48 48 0 0 1 0 48 L0 0 Z' fill='#4caf50'/>"
                + "  <path d='M0 48 A48 48 0 0 1 -34 -33 L0 0 Z' fill='#ff9800'/>"
                + "  <path d='M-34 -33 A48 48 0 0 1 48 0 L0 0 Z' fill='#f44336'/>"
                + "</g>"
                + "<text x='140' y='40' font-family='Arial' font-size='12' fill='#223047'>Pass / Fail / Skip</text>"
                + "</svg>";

        String barSvg = "<svg xmlns='http://www.w3.org/2000/svg' width='600' height='160'>"
                + "<rect width='100%' height='100%' fill='#ffffff'/>"
                + "<g transform='translate(60,120)'>"
                + "  <rect x='0'   y='-60'  width='80' height='60'  fill='#4caf50'/>"
                + "  <rect x='120' y='-100' width='80' height='100' fill='#f44336'/>"
                + "  <rect x='240' y='-40'  width='80' height='40'  fill='#ff9800'/>"
                + "  <text x='0'   y='30' font-family='Arial' font-size='12' fill='#223047'>Passed</text>"
                + "  <text x='120' y='30' font-family='Arial' font-size='12' fill='#223047'>Failed</text>"
                + "  <text x='240' y='30' font-family='Arial' font-size='12' fill='#223047'>Skipped</text>"
                + "</g>"
                + "</svg>";

        // try to load PNGs from target folder; fall back to embedded SVGs when missing
        Path piePng = targetDir.resolve("piechart.png");
        Path barPng = targetDir.resolve("barchart.png");

        String pieDataUri;
        String barDataUri;

        try {
            if (Files.exists(piePng) && Files.isRegularFile(piePng)) {
                byte[] pieBytes = Files.readAllBytes(piePng);
                pieDataUri = "data:image/png;base64," + Base64.getEncoder().encodeToString(pieBytes);
            } else {
                pieDataUri = "data:image/svg+xml;base64," + Base64.getEncoder()
                        .encodeToString(pieSvg.getBytes(StandardCharsets.UTF_8));
            }

            if (Files.exists(barPng) && Files.isRegularFile(barPng)) {
                byte[] barBytes = Files.readAllBytes(barPng);
                barDataUri = "data:image/png;base64," + Base64.getEncoder().encodeToString(barBytes);
            } else {
                barDataUri = "data:image/svg+xml;base64," + Base64.getEncoder()
                        .encodeToString(barSvg.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            // if reading fails, fall back to SVGs
            pieDataUri = "data:image/svg+xml;base64," + Base64.getEncoder()
                    .encodeToString(pieSvg.getBytes(StandardCharsets.UTF_8));
            barDataUri = "data:image/svg+xml;base64," + Base64.getEncoder()
                    .encodeToString(barSvg.getBytes(StandardCharsets.UTF_8));
        }

        String html = getHtmlBuilder().toString()
                .replace("cid:pie", pieDataUri)
                .replace("cid:bar", barDataUri);

        try {
            Files.write(reportHtml, html.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        System.out.println("Saved report to " + reportHtml.toAbsolutePath());

       try {
//           String date = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
           Path reportPDF = targetDir.resolve("report-" + date + ".pdf");
//           Path reportPDF = targetDir.resolve("report.pdf");
           Objects.requireNonNull(reportHtml, "htmlFile must not be null");
           Objects.requireNonNull(reportPDF, "pdfFile must not be null");
           convert(reportHtml, reportPDF);
       } catch (Exception e) {
           e.printStackTrace();
       }


    }
}