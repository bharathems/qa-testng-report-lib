// src/test/java/org/c3p/automation/framework/hardhat/TestNGResult.java
package org.exp.reportservice.testng;

public class TestNGResult {
    public final String suiteName;
    public final String testName;
    public final String className;
    public final String testMethodName;
    public final String testStatus;
    public final String methodStatus;
    public final String durationMs;

    public TestNGResult(String suiteName, String testName, String className, String testMethodName, String testStatus, String methodStatus, String durationMs) {
        this.suiteName = suiteName;
        this.testName = testName;
        this.className = className;
        this.testMethodName = testMethodName;
        this.testStatus = testStatus;
        this.methodStatus = methodStatus;
        this.durationMs = durationMs;
    }
}