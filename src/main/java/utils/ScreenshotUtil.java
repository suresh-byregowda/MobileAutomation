package utils;

import factory.DriverFactory;
import listeners.ExtentTestManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Screenshot utility for Extent Reports.
 * Supports:
 *  - Base64 for embedding into report
 *  - PNG file saving into test-output/screenshots/
 */
public final class ScreenshotUtil {

    private ScreenshotUtil() {}

    // =======================================
    //  BASE64 SCREENSHOT (Extent embedding)
    // =======================================
    public static String getBase64() {
        try {
            return ((TakesScreenshot) DriverFactory.getDriver())
                    .getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            try {
                ExtentTestManager.getTest()
                        .warning("Screenshot (base64) failed: " + e.getMessage());
            } catch (Exception ignored) {}
            return null;
        }
    }

    // =======================================
    //  SAVE PNG FILE + RETURN PATH
    // =======================================
    public static String saveScreenshot(String name) {
        try {
            File srcFile =
                    ((TakesScreenshot) DriverFactory.getDriver())
                            .getScreenshotAs(OutputType.FILE);

            String timestamp =
                    new SimpleDateFormat("yyyyMMdd_HHmmss")
                            .format(new Date());

            String dir = "test-output/screenshots/";
            File folder = new File(dir);
            if (!folder.exists()) folder.mkdirs();

            String path = dir + name + "_" + timestamp + ".png";
            File destFile = new File(path);

            FileUtils.copyFile(srcFile, destFile);
            return destFile.getAbsolutePath();

        } catch (Exception e) {
            try {
                ExtentTestManager.getTest()
                        .warning("Screenshot save failed: " + e.getMessage());
            } catch (Exception ignored) {}
            return null;
        }
    }

    // =======================================
    //  SAVE FILE + ATTACH TO EXTENT
    // =======================================
    public static void attachScreenshotToExtent(String label) {
        try {
            String base64 = getBase64();
            if (base64 != null && ExtentTestManager.getTest() != null) {
                ExtentTestManager.getTest()
                        .addScreenCaptureFromBase64String(base64, label);
            }
        } catch (Exception e) {
            try {
                ExtentTestManager.getTest()
                        .warning("Attach screenshot failed: " + e.getMessage());
            } catch (Exception ignored) {}
        }
    }

    public static String takeScreenshotOnFailure(String testName) {
        return saveScreenshot(testName + "_failure");
    }
}
