package factory;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.options.XCUITestOptions;
import utils.ConfigReader;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

public class LocalDriverManager {

    /* =======================================================
       CREATE LOCAL DRIVER (APPIUM 3 COMPATIBLE)
       ======================================================= */
    public static AppiumDriver createLocalDriver() throws Exception {

        String runEnv = ConfigReader.getOrDefault("run_env", "local");

        if (!"local".equalsIgnoreCase(runEnv)) {
            throw new RuntimeException(
                    "❌ LocalDriverManager must NOT be used for run_env=" + runEnv
            );
        }

        String serverUrl = ConfigReader.getOrDefault(
                "appium_url",
                "http://127.0.0.1:4723"
        );

        // ---- Fail fast if Appium server is not running ----
        verifyAppiumServerIsRunning(serverUrl);

        System.out.println(">>> Connecting to Appium Server: " + serverUrl);

        String platform = ConfigReader
                .getOrDefault("platform", "android")
                .toLowerCase();

        /* ===================================================
           ANDROID DRIVER
           =================================================== */
        if (platform.contains("android")) {

            UiAutomator2Options options = new UiAutomator2Options();
            options.setPlatformName("Android");
            options.setAutomationName("UiAutomator2");
            options.setDeviceName(ConfigReader.get("device_name"));
            options.setAppPackage(ConfigReader.get("app_package"));
            options.setAppActivity(ConfigReader.get("app_activity"));
            options.setAutoGrantPermissions(true);

            // ---- Chromedriver (ONLY if webview is used) ----
            String chromeDriverPath = ConfigReader.get("chromedriver_executable");

            if (chromeDriverPath != null && !chromeDriverPath.isBlank()) {

                File chromeDriver = new File(chromeDriverPath);
                if (!chromeDriver.isAbsolute()) {
                    chromeDriver = new File(
                            System.getProperty("user.dir"),
                            chromeDriverPath
                    );
                }

                if (!chromeDriver.exists()) {
                    throw new RuntimeException(
                            "❌ Chromedriver not found at: " +
                                    chromeDriver.getAbsolutePath()
                    );
                }

                options.setChromedriverExecutable(
                        chromeDriver.getAbsolutePath()
                );
            }

            return new AndroidDriver(
                    new URL(serverUrl),
                    options
            );
        }

        /* ===================================================
           IOS DRIVER
           =================================================== */
        if (platform.contains("ios")) {

            XCUITestOptions options = new XCUITestOptions();
            options.setPlatformName("iOS");
            options.setAutomationName("XCUITest");
            options.setDeviceName(
                    ConfigReader.getOrDefault("device_name", "iPhone")
            );
            options.setPlatformVersion(
                    ConfigReader.getOrDefault("platform_version", "")
            );
            options.setUdid(
                    ConfigReader.getOrDefault("udid", "")
            );
            options.setAutoAcceptAlerts(true);

            String appPath = ConfigReader.get("app_path");
            if (appPath != null && !appPath.isBlank()) {
                options.setApp(appPath);
            }

            return new IOSDriver(
                    new URL(serverUrl),
                    options
            );
        }

        throw new RuntimeException("❌ Invalid platform in config: " + platform);
    }

    /* =======================================================
       APP IUM HEALTH CHECK
       ======================================================= */
    private static void verifyAppiumServerIsRunning(String serverUrl) {
        try {
            URL status = new URL(serverUrl + "/status");
            HttpURLConnection connection =
                    (HttpURLConnection) status.openConnection();
            connection.setConnectTimeout(2000);
            connection.connect();

            if (connection.getResponseCode() != 200) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "❌ Appium server is NOT running at " + serverUrl +
                            "\n➡ Start it manually using: appium"
            );
        }
    }
}
