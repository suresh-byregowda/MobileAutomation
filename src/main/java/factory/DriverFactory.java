package factory;

import io.appium.java_client.AppiumDriver;
import utils.ConfigReader;

public final class DriverFactory {

    private static AppiumDriver driver;

    private DriverFactory() {}

    /* =======================================================
       CREATE DRIVER (LOCAL / BROWSERSTACK)
       ======================================================= */
    public static synchronized void createDriver() throws Exception {

        if (driver != null) {
            System.out.println(">>> Driver already created. Reusing existing instance.");
            return;
        }

        // Test environment (qa / uat / staging)
        String testEnv = ConfigReader.getOrDefault("env", "qa").toLowerCase();

        // Execution environment (local / browserstack)
        String runEnv = ConfigReader.getOrDefault("run_env", "local").toLowerCase();

        System.out.println(">>> Test Environment  : " + testEnv);
        System.out.println(">>> Execution Mode   : " + runEnv);

        switch (runEnv) {

            case "local":
                driver = LocalDriverManager.createLocalDriver();
                break;

            case "browserstack":
                driver = BrowserStackDriverManager.createBrowserStackDriver();
                break;

            default:
                throw new RuntimeException(
                        "❌ Invalid run_env: " + runEnv +
                                " (allowed values: local | browserstack)"
                );
        }
    }

    /* =======================================================
       GET DRIVER
       ======================================================= */
    public static AppiumDriver getDriver() {
        if (driver == null) {
            throw new RuntimeException(
                    "❌ Driver not initialized. Call DriverFactory.createDriver() first."
            );
        }
        return driver;
    }

    /* =======================================================
       QUIT DRIVER
       ======================================================= */
    public static synchronized void quitDriver() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception ignored) {}
            finally {
                driver = null;
                System.out.println(">>> Driver quit successfully.");
            }
        }
    }
}
