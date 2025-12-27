package org.exp.reportservice.commons;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public final class HtmlToPdfConverter {

    private HtmlToPdfConverter() { /* utility */ }

    public static void convert(Path htmlFile, Path outputPdf) throws IOException {
        Objects.requireNonNull(htmlFile, "htmlFile must not be null");
        Objects.requireNonNull(outputPdf, "outputPdf must not be null");

        String html = Files.readString(htmlFile, StandardCharsets.UTF_8);
        String baseUri = htmlFile.getParent() != null ? htmlFile.getParent().toUri().toString() : null;
        convertString(html, outputPdf, baseUri);
    }

    public static void convertString(String html, Path outputPdf, String baseUri) throws IOException {
        Objects.requireNonNull(html, "html must not be null");
        Objects.requireNonNull(outputPdf, "outputPdf must not be null");

        String xhtml = normalizeToXhtml(html, baseUri);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(xhtml, baseUri);
            builder.toStream(os);
            builder.run();

            Path parent = outputPdf.getParent();
            if (parent != null) Files.createDirectories(parent);
            Files.write(outputPdf, os.toByteArray());
        } catch (Exception e) {
            throw new IOException("Failed to render HTML to PDF", e);
        }
    }

    private static String normalizeToXhtml(String html, String baseUri) {
        Document doc = (baseUri != null && !baseUri.isBlank()) ? Jsoup.parse(html, baseUri) : Jsoup.parse(html);
        Document.OutputSettings settings = new Document.OutputSettings();
        settings.syntax(Document.OutputSettings.Syntax.xml);
        settings.escapeMode(Entities.EscapeMode.xhtml);
        settings.charset(StandardCharsets.UTF_8);
        settings.prettyPrint(false);
        doc.outputSettings(settings);
        return doc.html();
    }

    // simple CLI: java ... HtmlToPdfConverter input.html output.pdf
    public static void main(String[] args) throws Exception {
        Path targetDir = Paths.get("target");
        try {
            Files.createDirectories(targetDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Path reportHtml = targetDir.resolve("report.html");
        String date = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
        Path reportPDF = targetDir.resolve("report-" + date + ".pdf");
        convert(reportHtml, reportPDF);
    }
}