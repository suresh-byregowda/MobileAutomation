package listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Singleton manager for ExtentReports instance.
 * Generates ONLY HTML reports (recommended).
 */
public class ExtentManager {

    private static ExtentReports extent;

    private static final String OUTPUT_FOLDER = "test-output/extent";
    private static final String HTML_REPORT = OUTPUT_FOLDER + "/ExtentReport.html";

    public synchronized static ExtentReports getInstance() {
        if (extent == null) {
            createInstance();
        }
        return extent;
    }

    private static void createInstance() {

        try {
            Path out = Paths.get(OUTPUT_FOLDER);
            if (!Files.exists(out)) {
                Files.createDirectories(out);
            }
        } catch (Exception ignored) {}

        // ============================
        // HTML REPORTER
        // ============================
        ExtentSparkReporter htmlReporter = new ExtentSparkReporter(HTML_REPORT);
        htmlReporter.config().setDocumentTitle("Automation Test Report");
        htmlReporter.config().setReportName("PocAndroid Test Execution");
        htmlReporter.config().setTheme(Theme.STANDARD);

        // ============================
        // EXTENT SETUP
        // ============================
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);

        // ============================
        // META / SYSTEM INFO
        // ============================
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("User", System.getProperty("user.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("Platform", System.getProperty("platform", "Android"));
    }
}
