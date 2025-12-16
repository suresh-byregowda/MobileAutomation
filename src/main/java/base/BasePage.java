package base;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import listeners.ExtentTestManager;

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

public abstract class BasePage {

    protected AppiumDriver driver;

    private static final DateTimeFormatter TS_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    protected BasePage(AppiumDriver driver) {
        if (driver == null)
            throw new IllegalStateException("Driver is NULL! Ensure driver is initialized before using page objects.");
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

    // High-level step wrapper that logs and attaches screenshot under the same step
    public void perform(String action, Runnable runnable) {
        String label = action + " [" + timestamp() + "]";

        // Log the step (creates the row in Extent)
        try {
            ExtentTestManager.getTest().log(Status.INFO, label);
        } catch (Exception ignored) {}

        // Execute step
        runnable.run();

        // Attach screenshot to the same step (media attached to the most recent log)
        attachScreenshotUnderStep(label);
    }

    protected <T> T perform(String action, java.util.concurrent.Callable<T> callable) {
        String label = action + " [" + timestamp() + "]";

        try {
            ExtentTestManager.getTest().log(Status.INFO, label);
        } catch (Exception ignored) {}

        try {
            T result = callable.call();
            attachScreenshotUnderStep(label);
            return result;
        } catch (Exception e) {
            try {
                ExtentTestManager.getTest().log(Status.FAIL, "Error during: " + action + " → " + e.getMessage());
            } catch (Exception ignored) {}
            attachScreenshotUnderStep("FAILED: " + label);
            throw new RuntimeException(e);
        }
    }

    // Wait helpers
    protected void waitForVisible(WebElement element, int seconds) {
        new WebDriverWait(driver, Duration.ofSeconds(seconds))
                .until(ExpectedConditions.visibilityOf(element));
    }

    protected void waitForVisible(WebElement element) {
        waitForVisible(element, 20);
    }

    // Attach screenshot under the most recent step log using MediaEntityBuilder
    protected void attachScreenshotUnderStep(String label) {
        try {
            String base64 = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
            if (ExtentTestManager.getTest() != null) {
                ExtentTestManager.getTest().info(
                        "Screenshot: " + label,
                        MediaEntityBuilder.createScreenCaptureFromBase64String(base64, label).build()
                );
            }
        } catch (Exception ex) {
            try { ExtentTestManager.getTest().warning("Failed to capture screenshot: " + ex.getMessage()); } catch (Exception ignored) {}
        }
    }

    // Action helpers
    protected void click(WebElement element, String msg) {
        perform(msg, element::click);
    }

    protected void type(WebElement element, String text, String msg) {
        perform(msg, () -> element.sendKeys(text));
    }

    protected String get(WebElement element, String msg) {
        return perform(msg, element::getText);
    }

    // Logging helpers (safe)
    protected void log(String message) {
        try { ExtentTestManager.getTest().log(Status.INFO, message); } catch (Exception ignored) {}
    }

    protected void fail(String message) {
        try { ExtentTestManager.getTest().log(Status.FAIL, message); } catch (Exception ignored) {}
    }

    private String timestamp() {
        return LocalDateTime.now().format(TS_FORMAT);
    }

    // Platform helpers
    protected String platformName() {
        Object o = driver.getCapabilities().getCapability("platformName");
        return o == null ? "" : o.toString().toLowerCase();
    }

    protected boolean isAndroid() { return platformName().contains("android"); }
    protected boolean isIOS() { return platformName().contains("ios"); }
}
