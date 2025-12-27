package org.exp.reportservice.testrunners;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingSetup {
    static {
        Logger.getLogger("com.openhtmltopdf.css-parse").setLevel(Level.OFF);
        // optional: Logger.getLogger("com.openhtmltopdf").setLevel(Level.SEVERE);
    }

    public static void init() { /* force classload if needed */ }
}