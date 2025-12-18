package base;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import listeners.ExtentTestManager;
import utils.ConfigReader;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;

public abstract class BasePage {

    protected AppiumDriver driver;

    private static final DateTimeFormatter TS_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    // Read once (cheap + safe)
    private static final boolean ALL_SCREENS =
            Boolean.parseBoolean(
                    ConfigReader.getOrDefault("allscreens", "false")
            );

    protected BasePage(AppiumDriver driver) {
        if (driver == null)
            throw new IllegalStateException(
                    "Driver is NULL! Ensure driver is initialized before using page objects."
            );
        this.driver = driver;
        PageFactory.initElements(
                new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this
        );
    }

    /* =======================================================
       STEP EXECUTION (NO FORCED SCREENSHOT)
       ======================================================= */
    public void perform(String action, Runnable runnable) {
        perform(action, runnable, false);
    }

    public void perform(String action, Runnable runnable, boolean forceScreenshot) {
        String label = action + " [" + timestamp() + "]";

        logInfo(label);
        runnable.run();

        maybeAttachScreenshot(label, forceScreenshot);
    }

    protected <T> T perform(String action, Callable<T> callable) {
        return perform(action, callable, false);
    }

    protected <T> T perform(String action, Callable<T> callable, boolean forceScreenshot) {
        String label = action + " [" + timestamp() + "]";

        logInfo(label);

        try {
            T result = callable.call();
            maybeAttachScreenshot(label, forceScreenshot);
            return result;
        } catch (Exception e) {
            logFail("Error during: " + action + " → " + e.getMessage());
            attachScreenshot(label + " (FAILED)");
            throw new RuntimeException(e);
        }
    }

    /* =======================================================
       SCREENSHOT CONTROL
       ======================================================= */
    private void maybeAttachScreenshot(String label, boolean force) {
        if (ALL_SCREENS || force) {
            attachScreenshot(label);
        }
    }

    protected void attachScreenshot(String label) {
        try {
            if (ExtentTestManager.getTest() == null) return;

            String base64 =
                    ((TakesScreenshot) driver)
                            .getScreenshotAs(OutputType.BASE64);

            ExtentTestManager.getTest().info(
                    "Screenshot: " + label,
                    MediaEntityBuilder
                            .createScreenCaptureFromBase64String(base64, label)
                            .build()
            );
        } catch (Exception ex) {
            try {
                ExtentTestManager.getTest()
                        .warning("Failed to capture screenshot: " + ex.getMessage());
            } catch (Exception ignored) {}
        }
    }

    /* =======================================================
       WAIT HELPERS
       ======================================================= */
    protected void waitForVisible(WebElement element, int seconds) {
        new WebDriverWait(driver, Duration.ofSeconds(seconds))
                .until(ExpectedConditions.visibilityOf(element));
    }

    protected void waitForVisible(WebElement element) {
        waitForVisible(element, 20);
    }

    /* =======================================================
       ACTION HELPERS
       ======================================================= */
    protected void click(WebElement element, String msg) {
        perform(msg, element::click);
    }

    protected void clickWithScreenshot(WebElement element, String msg) {
        perform(msg, element::click, true);
    }

    protected void type(WebElement element, String text, String msg) {
        perform(msg, () -> element.sendKeys(text));
    }

    protected void typeWithScreenshot(WebElement element, String text, String msg) {
        perform(msg, () -> element.sendKeys(text), true);
    }

    protected String get(WebElement element, String msg) {
        return perform(msg, element::getText);
    }

    /* =======================================================
       LOGGING HELPERS
       ======================================================= */
    protected void logInfo(String message) {
        try {
            ExtentTestManager.getTest()
                    .log(Status.INFO, message);
        } catch (Exception ignored) {}
    }

    protected void logFail(String message) {
        try {
            ExtentTestManager.getTest()
                    .log(Status.FAIL, message);
        } catch (Exception ignored) {}
    }

    /* =======================================================
       PLATFORM HELPERS
       ======================================================= */
    protected String platformName() {
        Object o = driver.getCapabilities().getCapability("platformName");
        return o == null ? "" : o.toString().toLowerCase();
    }

    protected boolean isAndroid() { return platformName().contains("android"); }
    protected boolean isIOS() { return platformName().contains("ios"); }

    private String timestamp() {
        return LocalDateTime.now().format(TS_FORMAT);
    }
}
