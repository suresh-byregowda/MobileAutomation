package factory;

import io.appium.java_client.AppiumDriver;
import utils.ConfigReader;

public final class DriverFactory {

    private static final ThreadLocal<AppiumDriver> DRIVER =
            new ThreadLocal<>();

    private DriverFactory() {}

    public static void createDriver() throws Exception {

        if (DRIVER.get() != null) return;

        AppiumDriver driver =
                ConfigReader.isBrowserStack()
                        ? BrowserStackDriverManager.createBrowserStackDriver()
                        : LocalDriverManager.createLocalDriver();

        DRIVER.set(driver);
    }

    public static AppiumDriver getDriver() {
        AppiumDriver driver = DRIVER.get();
        if (driver == null) {
            throw new RuntimeException("❌ Driver not initialized for this thread.");
        }
        return driver;
    }

    public static void quitDriver() {

        AppiumDriver driver = DRIVER.get();

        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception ignored) {}
            finally {
                DRIVER.remove();
                System.out.println(">>> Driver quit for thread.");
            }
        }
    }
}
