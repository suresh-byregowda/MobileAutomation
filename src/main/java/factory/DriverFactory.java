package factory;

import io.appium.java_client.AppiumDriver;
import utils.ConfigReader;
import utils.DeviceContext;

public final class DriverFactory {

    private static final ThreadLocal<AppiumDriver> DRIVER =
            new ThreadLocal<>();

    private DriverFactory() {}

    public static void createDriver() throws Exception {

        if (DRIVER.get() != null) return;

        String runEnv =
                ConfigReader.getOrDefault("run_env", "local").toLowerCase();

        AppiumDriver driver =
                "browserstack".equals(runEnv)
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
                DeviceContext.clear(); // ✅ IMPORTANT
                System.out.println(">>> Driver quit for thread.");
            }
        }
    }
}
