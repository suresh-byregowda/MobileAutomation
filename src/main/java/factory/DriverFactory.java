package factory;

import io.appium.java_client.AppiumDriver;
import utils.ConfigReader;

public final class DriverFactory {

    private static final ThreadLocal<AppiumDriver> DRIVER =
            new ThreadLocal<>();

    private DriverFactory() {}

    /* =======================================================
       CREATE DRIVER (LOCAL / BROWSERSTACK)
       ======================================================= */
    public static void createDriver() throws Exception {

        if (DRIVER.get() != null) {
            System.out.println(">>> Driver already exists for this thread.");
            return;
        }

        // Execution environment (local / browserstack)
        String runEnv =
                ConfigReader.getOrDefault("run_env", "local").toLowerCase();

        System.out.println(">>> Execution Mode   : " + runEnv);

        AppiumDriver driver;

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

        DRIVER.set(driver);
    }

    /* =======================================================
       GET DRIVER
       ======================================================= */
    public static AppiumDriver getDriver() {
        AppiumDriver driver = DRIVER.get();

        if (driver == null) {
            throw new RuntimeException(
                    "❌ Driver not initialized for this thread."
            );
        }
        return driver;
    }

    /* =======================================================
       QUIT DRIVER
       ======================================================= */
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
