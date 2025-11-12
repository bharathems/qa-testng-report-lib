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

class ChartGenerator {

    public static File createPieChart(Map<String, Integer> data, String title, String path) throws IOException {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data map cannot be null or empty.");
        }

        // Build dataset and normalize keys
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        int total = 0;
        for (Map.Entry<String, Integer> e : data.entrySet()) {
            String raw = e.getKey();
            String key = (raw == null) ? "UNKNOWN" : raw.trim();
            if ("pass".equalsIgnoreCase(key)) key = "PASS";
            else if ("fail".equalsIgnoreCase(key) || "failure".equalsIgnoreCase(key)) key = "FAIL";
            int val = (e.getValue() == null) ? 0 : e.getValue();
            dataset.setValue(key, val);
            total += val;
        }

        // Use RingPlot for a clean donut look
        org.jfree.chart.plot.RingPlot plot = new org.jfree.chart.plot.RingPlot(dataset);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0}: {1} ({2})",
                new DecimalFormat("0"),              // integer count
                new DecimalFormat("0.0%")));
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setSectionOutlinesVisible(false);
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.setLabelBackgroundPaint(new Color(255, 255, 255, 230));
        plot.setLabelOutlineStroke(new BasicStroke(0f));
        plot.setSimpleLabels(true);
        plot.setLabelGap(0.02);
        plot.setMaximumLabelWidth(0.30);
        plot.setIgnoreZeroValues(true);
        plot.setIgnoreNullValues(true);

        // Chart setup
        JFreeChart chart = new JFreeChart(title, new Font("SansSerif", Font.BOLD, 16), plot, false);
        chart.setAntiAlias(true);
        chart.setTextAntiAlias(true);
        chart.setBackgroundPaint(Color.WHITE);
        chart.setBorderVisible(false);

        plot.setShadowPaint(null);
        plot.setSeparatorPaint(Color.WHITE);
        plot.setSeparatorStroke(new BasicStroke(2f));
        try { plot.setSectionDepth(0.30); } catch (Throwable ignored) {}

        // Colors
        Map<String, Color> palette = new HashMap<>();
        palette.put("PASS", new Color(0x2E8B57)); // sea green
        palette.put("FAIL", new Color(0xC0392B)); // red
        palette.put("SKIP", new Color(0xF39C12)); // orange
        Color fallback = new Color(0x95A5A6); // gray

        for (Comparable<?> keyObj : dataset.getKeys()) {
            String name = keyObj.toString().trim().toUpperCase();
            Color c = palette.getOrDefault(name, fallback);
            plot.setSectionPaint(keyObj, c);
            plot.setSectionOutlinePaint(keyObj, Color.WHITE);
            plot.setSectionOutlineStroke(keyObj, new BasicStroke(2.0f));
        }

        // Build RIGHT-side legend with only PASS / FAIL / SKIP (in that order)
        // Replace the legend-building loop with this (inside createPieChart)
        String[] rightOrder = {"PASS", "FAIL", "SKIP"};
        DecimalFormat pct = new DecimalFormat("0.0%");
        org.jfree.chart.LegendItemCollection legendItems = new org.jfree.chart.LegendItemCollection();
        for (String label : rightOrder) {
            double value = safeGetValue(dataset, label);
            String percentText = (total > 0) ? pct.format(value / (double) total) : "0.0%";
//            String legendLabel = String.format("%s  u2014  %d  (%s)", label, (int) value, percentText);
            String legendLabel = String.format("%s  \u2014  %d  (%s)", label, (int) value, percentText);
            java.awt.Shape swatch = new java.awt.geom.Rectangle2D.Double(-6, -6, 12, 12);
            java.awt.Paint fill = plot.getSectionPaint(label);
            if (fill == null) fill = palette.getOrDefault(label, fallback);
            org.jfree.chart.LegendItem li = new org.jfree.chart.LegendItem(legendLabel, null, null, null, swatch, fill);
            legendItems.add(li);
        }

        // Attach custom legend on the RIGHT
        chart.removeLegend();
        org.jfree.chart.LegendItemSource source = new org.jfree.chart.LegendItemSource() {
            @Override
            public org.jfree.chart.LegendItemCollection getLegendItems() {
                return legendItems;
            }
        };
        org.jfree.chart.title.LegendTitle customLegend = new org.jfree.chart.title.LegendTitle(source);
        customLegend.setItemFont(new Font("SansSerif", Font.PLAIN, 12));
        customLegend.setBackgroundPaint(Color.WHITE);
        customLegend.setPadding(new org.jfree.chart.ui.RectangleInsets(8, 8, 8, 12));
        customLegend.setPosition(RectangleEdge.RIGHT);
        customLegend.setHorizontalAlignment(org.jfree.chart.ui.HorizontalAlignment.LEFT);
        chart.addSubtitle(customLegend);

        // Title styling
        TextTitle chartTitle = chart.getTitle();
        chartTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        chartTitle.setPadding(8, 8, 6, 8);

        // Save image
        File output = new File(path);
        int width = 700; // slightly wider to accommodate right legend
        int height = 380;
        ChartUtils.saveChartAsPNG(output, chart, width, height);
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
            // Use only the simple class name (after the last dot) as the category label
            String displayFeature = feature;
            int lastDot = feature.lastIndexOf('.');
            if (lastDot >= 0 && lastDot < feature.length() - 1) {
                displayFeature = feature.substring(lastDot + 1);
            }
            Map<String, Integer> statusMap = data.get(feature);
            for (String status : statusOrder) {
                Integer value = statusMap.entrySet().stream()
                        .filter(e -> e.getKey().equalsIgnoreCase(status))
                        .map(Map.Entry::getValue)
                        .findFirst()
                        .orElse(0);
                dataset.addValue(value, status, displayFeature);
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
//        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
//        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(org.jfree.chart.axis.CategoryLabelPositions.UP_45);
        domainAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 16));
        domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        domainAxis.setAxisLineStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        domainAxis.setTickMarkStroke(new BasicStroke(1.5f));
        domainAxis.setTickMarksVisible(true);

        domainAxis.setMaximumCategoryLabelLines(2);
        domainAxis.setCategoryMargin(0.12f);
        domainAxis.setLowerMargin(0.02);
        domainAxis.setUpperMargin(0.02);

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

        ChartUtils.saveChartAsPNG(output, chart, 700, 500);
        return output;
    }

    private static double safeGetValue(DefaultPieDataset<String> dataset, String key) {
        int idx = dataset.getIndex(key);
        if (idx >= 0) {
            Number v = dataset.getValue(idx);
            return (v == null) ? 0d : v.doubleValue();
        }
        return 0d;
    }

}
