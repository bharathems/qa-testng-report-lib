package org.exp.reportservice.constants;

public class GlobalConstants {
    private static StringBuilder htmlBuilder = new StringBuilder();

    public static synchronized StringBuilder getHtmlBuilder() {
        return htmlBuilder;
    }
    public static synchronized void setHtmlBuilder(StringBuilder builder) {
        htmlBuilder = (builder == null) ? new StringBuilder() : builder;
    }
}
