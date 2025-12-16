package factory;

import com.fasterxml.jackson.databind.JsonNode;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import utils.CapabilityApplier;
import utils.ConfigReader;
import utils.JsonCapabilityLoader;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

public class LocalDriverManager {

    /* =======================================================
       CREATE LOCAL DRIVER (APPIUM 3 + SERVER MANAGER SAFE)
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

        // ✅ OPTIONAL server start (only if manager exists & enabled)
        startAppiumIfManagerPresent();

        // ✅ Always validate connectivity
        verifyAppiumServerIsRunning(serverUrl);

        System.out.println(">>> Connecting to Appium Server: " + serverUrl);

        String platform =
                ConfigReader.getOrDefault("platform", "android").toLowerCase();

        /* ===================================================
           ANDROID
           =================================================== */
        if (platform.contains("android")) {

            UiAutomator2Options options = new UiAutomator2Options();

            // 1️⃣ JSON caps
            JsonNode androidCaps =
                    JsonCapabilityLoader.getPlatformCaps("android");
            CapabilityApplier.applyAndroidCaps(options, androidCaps);

            // 2️⃣ Runtime-only
            options.setPlatformName("Android");
            options.setAutomationName("UiAutomator2");
            options.setDeviceName(ConfigReader.get("device_name"));
            options.setAppPackage(ConfigReader.get("app_package"));
            options.setAppActivity(ConfigReader.get("app_activity"));
<<<<<<< HEAD
            options.setDisableWindowAnimation(true);
=======
            options.setAutoGrantPermissions(true);
>>>>>>> 6c963797aed92ae92152724e5eeedeac698e375e

            // 3️⃣ Chromedriver (cross-platform safe)
            applyChromedriverIfPresent(options);

            return new AndroidDriver(new URL(serverUrl), options);
        }

        /* ===================================================
           IOS
           =================================================== */
        if (platform.contains("ios")) {

            XCUITestOptions options = new XCUITestOptions();

            JsonNode iosCaps =
                    JsonCapabilityLoader.getPlatformCaps("ios");
            CapabilityApplier.applyIosCaps(options, iosCaps);

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

            String appPath = ConfigReader.get("app_path");
            if (appPath != null && !appPath.isBlank()) {
                options.setApp(appPath);
            }

            return new IOSDriver(new URL(serverUrl), options);
        }

        throw new RuntimeException("❌ Invalid platform in config: " + platform);
    }

    /* =======================================================
       OPTIONAL APPIUM SERVER START
       ======================================================= */
    private static void startAppiumIfManagerPresent() {
        try {
            Class<?> mgr = Class.forName("factory.AppiumServerManager");

            Boolean autoStart =
                    Boolean.parseBoolean(
                            ConfigReader.getOrDefault(
                                    "appium_autostart", "false"
                            )
                    );

            if (autoStart) {
                mgr.getMethod("startServerIfRequired").invoke(null);
            }

        } catch (ClassNotFoundException ignored) {
            // AppiumServerManager not used → manual start mode
        } catch (Exception e) {
            throw new RuntimeException(
                    "❌ Failed to start Appium via AppiumServerManager", e
            );
        }
    }

    /* =======================================================
       CHROMEDRIVER HELPER (OS SAFE)
       ======================================================= */
    private static void applyChromedriverIfPresent(
            UiAutomator2Options options
    ) {

        String chromeDriverPath =
                ConfigReader.get("chromedriver_executable");

        if (chromeDriverPath == null || chromeDriverPath.isBlank()) {
            return;
        }

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

    /* =======================================================
       APPIUM HEALTH CHECK
       ======================================================= */
    private static void verifyAppiumServerIsRunning(String serverUrl) {
        try {
            URL status = new URL(serverUrl + "/status");
            HttpURLConnection connection =
                    (HttpURLConnection) status.openConnection();
            connection.setConnectTimeout(3000);
            connection.connect();

            if (connection.getResponseCode() != 200) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "❌ Appium server is NOT reachable at " + serverUrl +
                            "\n➡ Start it manually or enable appium_autostart=true"
            );
        }
    }
}
