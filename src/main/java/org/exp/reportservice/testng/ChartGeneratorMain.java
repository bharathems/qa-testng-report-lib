package org.exp.reportservice.testng;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ChartGeneratorMain {

    public static File createStatusPieChart(List<TestNGResult> results, int width, int height) throws Exception {
        Map<String, Integer> counts = new HashMap<>();
        for (TestNGResult r : results) {
            String s = (r.methodStatus== null) ? "UNKNOWN" : r.methodStatus.trim().toUpperCase();
            if (s.isEmpty()) s = "UNKNOWN";
            counts.put(s, counts.getOrDefault(s, 0) + 1);
        }

        DefaultPieDataset dataset = new DefaultPieDataset();
        counts.forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart("Test Results", dataset, true, true, false);
        chart.setAntiAlias(true);
        chart.setTextAntiAlias(true);
//        chart.setBackgroundPaint(Color.WHITE); //enable this to se white/No background
        chart.setBackgroundPaint(new Color(245, 245, 245)); // light gray
        chart.setBorderVisible(false);


        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setSectionOutlinesVisible(false);
        plot.setCircular(true);
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.setNoDataMessage("No data available");

        // Shadow effect
        plot.setShadowPaint(Color.GRAY);
        plot.setShadowXOffset(2);
        plot.setShadowYOffset(2);

        if (counts.containsKey("PASS")) plot.setSectionPaint("PASS", new Color(0x2E8B57)); // sea green
        if (counts.containsKey("FAIL")) plot.setSectionPaint("FAIL", new Color(0xC0392B)); // red
        if (counts.containsKey("SKIP")) plot.setSectionPaint("SKIP", new Color(0xF39C12)); // orange
//        plot.setBackgroundPaint(Color.WHITE);
//        plot.setOutlineVisible(false);

        // Custom section colors
//        for (Comparable<?> key : dataset.getKeys()) {
//            String name = key.toString().trim().toLowerCase();
//            if (name.startsWith("pass")) {
//                plot.setSectionPaint(key, new Color(144, 238, 144)); // green
//            } else if (name.startsWith("fail") || name.startsWith("failure")) {
//                plot.setSectionPaint(key, new Color(255, 99, 71)); // red
//            } else if (name.contains("skip")) {
//                plot.setSectionPaint(key, new Color(251, 188, 5)); // yellow
//            } else {
//                plot.setSectionPaint(key, Color.LIGHT_GRAY); // fallback
//            }
//            plot.setExplodePercent(key, 0.05); // slight explosion
//        }

        plot.setNoDataMessage("No data available");
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0}: {1} ({2})", new DecimalFormat("0"), new DecimalFormat("0.00%")));
        plot.setLabelBackgroundPaint(new Color(255, 255, 255, 230));
        plot.setLabelOutlineStroke(new BasicStroke(0f));

        // Legend font
        chart.getLegend().setItemFont(new Font("SansSerif", Font.PLAIN, 12));
        chart.getLegend().setPosition(RectangleEdge.BOTTOM);

        TextTitle chartTitle = chart.getTitle();
        chartTitle.setFont(new Font("SansSerif", Font.BOLD, 14));

        File output = new File("target/piechart.png");
        ChartUtils.saveChartAsPNG(output, chart, 500, 350);
        return output;

        // standard colors
//        if (counts.containsKey("PASS")) plot.setSectionPaint("PASS", new Color(0x2E8B57)); // sea green
//        if (counts.containsKey("FAIL")) plot.setSectionPaint("FAIL", new Color(0xC0392B)); // red
//        if (counts.containsKey("SKIP")) plot.setSectionPaint("SKIP", new Color(0xF39C12)); // orange
//        plot.setBackgroundPaint(Color.WHITE);
//        plot.setOutlineVisible(false);
//
//        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
//            ChartUtils.writeChartAsPNG(baos, chart, width, height);
//            return baos.toByteArray();
//        }
    }

    public static File createPieChart(Map<String, Integer> data, String title, String path) throws IOException {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data map cannot be null or empty.");
        }
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        data.forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);
        chart.setAntiAlias(true);
        chart.setTextAntiAlias(true);
//        chart.setBackgroundPaint(Color.WHITE); //enable this to se white/No background
        chart.setBackgroundPaint(new Color(245, 245, 245)); // light gray
        chart.setBorderVisible(false);


        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setSectionOutlinesVisible(false);
        plot.setCircular(true);
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.setNoDataMessage("No data available");

        // Shadow effect
        plot.setShadowPaint(Color.GRAY);
        plot.setShadowXOffset(2);
        plot.setShadowYOffset(2);

        // Custom section colors
        for (Comparable<?> key : dataset.getKeys()) {
            String name = key.toString().trim().toLowerCase();
            if (name.startsWith("pass")) {
                plot.setSectionPaint(key, new Color(144, 238, 144)); // green
            } else if (name.startsWith("fail") || name.startsWith("failure")) {
                plot.setSectionPaint(key, new Color(255, 99, 71)); // red
            } else if (name.contains("skip")) {
                plot.setSectionPaint(key, new Color(251, 188, 5)); // yellow
            } else {
                plot.setSectionPaint(key, Color.LIGHT_GRAY); // fallback
            }
            plot.setExplodePercent(key, 0.05); // slight explosion
        }

        plot.setNoDataMessage("No data available");
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0}: {1} ({2})", new DecimalFormat("0"), new DecimalFormat("0.00%")));
        plot.setLabelBackgroundPaint(new Color(255, 255, 255, 230));
        plot.setLabelOutlineStroke(new BasicStroke(0f));

        // Inside createPieChart method, after setting section paints:
        for (Comparable<?> key : dataset.getKeys()) {
            String name = key.toString().trim().toLowerCase();
            if (name.startsWith("pass") || name.startsWith("fail") || name.startsWith("failure")) {
                plot.setExplodePercent(key, 0.18); // Large gap for pass/fail
            } else {
                plot.setExplodePercent(key, 0.05);
            }
            plot.setSectionOutlinePaint(key, Color.DARK_GRAY);
            plot.setSectionOutlineStroke(key, new BasicStroke(3.0f));
        }

        // Legend font
        chart.getLegend().setItemFont(new Font("SansSerif", Font.PLAIN, 12));
        chart.getLegend().setPosition(RectangleEdge.BOTTOM);

        TextTitle chartTitle = chart.getTitle();
        chartTitle.setFont(new Font("SansSerif", Font.BOLD, 14));

        File output = new File(path);
        ChartUtils.saveChartAsPNG(output, chart, 500, 350);
        return output;
    }

    //bharath
    public static File createBarChart(Map<String, Map<String, Integer>> data, String title, String path) throws IOException {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data map cannot be null or empty.");
        }
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] statusOrder = {"pass", "fail", "skip"};
        for (String feature : data.keySet()) {
            Map<String, Integer> statusMap = data.get(feature);
            for (String status : statusOrder) {
                Integer value = statusMap.entrySet().stream()
                        .filter(e -> e.getKey().equalsIgnoreCase(status))
                        .map(Map.Entry::getValue)
                        .findFirst()
                        .orElse(0);
                dataset.addValue(value, status, feature);
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(title, "Feature", "Scenarios", dataset);
        chart.setBackgroundPaint(Color.WHITE); //enable this to set white/No background
//        chart.setBackgroundPaint(new Color(245, 245, 245)); // light gray
        chart.setBorderVisible(false);
        chart.setAntiAlias(true);
        chart.setTextAntiAlias(true);
        chart.getLegend().setItemFont(new Font("SansSerif", Font.PLAIN, 12));
        chart.getLegend().setPosition(RectangleEdge.BOTTOM);

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);        // Remove border
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(org.jfree.chart.axis.CategoryLabelPositions.UP_45);
        domainAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 12));
        domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10));
        domainAxis.setAxisLineStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        domainAxis.setTickMarkStroke(new BasicStroke(1.5f));
        domainAxis.setTickMarksVisible(true);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setTickUnit(new NumberTickUnit(1));
        rangeAxis.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        rangeAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        rangeAxis.setAutoRange(true);
        rangeAxis.setAxisLineStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        rangeAxis.setTickMarkStroke(new BasicStroke(1.5f));
        rangeAxis.setTickMarksVisible(false);


        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setShadowVisible(true);
        renderer.setShadowPaint(Color.LIGHT_GRAY);
        renderer.setItemMargin(0.1);

        renderer.setSeriesPaint(0, new Color(144, 238, 144));   // pass
        renderer.setSeriesPaint(1, new Color(255, 99, 71));   // fail
        renderer.setSeriesPaint(2, new Color(251, 188, 5));   // skip

        File output = new File(path);
        TextTitle chartTitle = chart.getTitle();
        chartTitle.setFont(new Font("SansSerif", Font.BOLD, 14));

        int minWidth = 600;
        int widthPerFeature = 60;
        int chartWidth = Math.max(minWidth, data.size() * widthPerFeature);

        ChartUtils.saveChartAsPNG(output, chart, chartWidth, 400);
        return output;
    }
    public static File createBarChart_imp(Map<String, Map<String, Integer>> data, String title, String path) throws IOException {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data map cannot be null or empty.");
        }
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] statusOrder = {"Pass", "Fail", "Skip"};
        for (String feature : data.keySet()) {
            Map<String, Integer> statusMap = data.get(feature);
            for (String status : statusOrder) {
                Integer value = statusMap.getOrDefault(status, 0);
                dataset.addValue(value, status, feature);
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(title, "Feature", "Scenarios", dataset);
        chart.setBackgroundPaint(Color.WHITE); //enable this to set white/No background
//        chart.setBackgroundPaint(new Color(245, 245, 245)); // light gray
        chart.setBorderVisible(false);
        chart.setAntiAlias(true);
        chart.setTextAntiAlias(true);
        chart.getLegend().setItemFont(new Font("SansSerif", Font.PLAIN, 12));
        chart.getLegend().setPosition(RectangleEdge.BOTTOM);

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);        // Remove border
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(org.jfree.chart.axis.CategoryLabelPositions.UP_45);
        domainAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 12));
        domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10));
        domainAxis.setAxisLineStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        domainAxis.setTickMarkStroke(new BasicStroke(1.5f));
        domainAxis.setTickMarksVisible(true);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setTickUnit(new NumberTickUnit(1));
        rangeAxis.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        rangeAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        rangeAxis.setAutoRange(true);
        rangeAxis.setAxisLineStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        rangeAxis.setTickMarkStroke(new BasicStroke(1.5f));
        rangeAxis.setTickMarksVisible(false);


        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setShadowVisible(true);
        renderer.setShadowPaint(Color.LIGHT_GRAY);
        renderer.setItemMargin(0.1);

        renderer.setSeriesPaint(0, new Color(144, 238, 144));   // pass
        renderer.setSeriesPaint(1, new Color(255, 99, 71));   // fail
        renderer.setSeriesPaint(2, new Color(251, 188, 5));   // skip

        File output = new File(path);
        TextTitle chartTitle = chart.getTitle();
        chartTitle.setFont(new Font("SansSerif", Font.BOLD, 14));

        int minWidth = 600;
        int widthPerFeature = 60;
        int chartWidth = Math.max(minWidth, data.size() * widthPerFeature);

        ChartUtils.saveChartAsPNG(output, chart, chartWidth, 400);
        return output;
    }
}
