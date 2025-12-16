package utils;

import com.aventstack.extentreports.Status;
import listeners.ExtentTestManager;

import java.time.LocalDateTime;

/**
 * Central logging utility for console + Extent report.
 * Safe to call anywhere in framework (null-safe).
 */
public final class Log {

    private Log() {}

    // ===========================
    // INFO
    // ===========================
    public static void info(String message) {
        System.out.println(timestamp() + " [INFO] " + message);

        try {
            if (ExtentTestManager.getTest() != null) {
                ExtentTestManager.getTest().log(Status.INFO, message);
            }
        } catch (Exception ignored) {}
    }

    // ===========================
    // PASS
    // ===========================
    public static void pass(String message) {
        System.out.println(timestamp() + " [PASS] " + message);

        try {
            if (ExtentTestManager.getTest() != null) {
                ExtentTestManager.getTest().log(Status.PASS, message);
            }
        } catch (Exception ignored) {}
    }

    // ===========================
    // WARNING
    // ===========================
    public static void warn(String message) {
        System.out.println(timestamp() + " [WARN] " + message);

        try {
            if (ExtentTestManager.getTest() != null) {
                ExtentTestManager.getTest().log(Status.WARNING, message);
            }
        } catch (Exception ignored) {}
    }

    // ===========================
    // ERROR / FAIL
    // ===========================
    public static void error(String message) {
        System.err.println(timestamp() + " [ERROR] " + message);

        try {
            if (ExtentTestManager.getTest() != null) {
                ExtentTestManager.getTest().log(Status.FAIL, message);
            }
        } catch (Exception ignored) {}
    }

    // ===========================
    // THROWABLE LOGGING
    // ===========================
    public static void exception(Throwable throwable) {
        error(throwable.getMessage());

        try {
            if (ExtentTestManager.getTest() != null) {
                ExtentTestManager.getTest().fail(throwable);
            }
        } catch (Exception ignored) {}
    }

    // ===========================
    // TIME HELPER
    // ===========================
    private static String timestamp() {
        return LocalDateTime.now().toString();
    }
}
