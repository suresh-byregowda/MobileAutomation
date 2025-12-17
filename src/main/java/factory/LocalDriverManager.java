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
import java.time.Duration;

public class LocalDriverManager {

    /* =======================================================
       CREATE LOCAL DRIVER
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

        verifyAppiumServerIsRunning(serverUrl);
        System.out.println(">>> Connecting to Appium Server: " + serverUrl);

        String platform =
                ConfigReader.getOrDefault("platform", "android").toLowerCase();

        /* ===================================================
           ANDROID
           =================================================== */
        if ("android".equals(platform)) {

            UiAutomator2Options options = new UiAutomator2Options();

            JsonNode androidCaps =
                    JsonCapabilityLoader.getPlatformCaps("android");
            CapabilityApplier.applyAndroidCaps(options, androidCaps);

            options.setPlatformName("Android");
            options.setAutomationName("UiAutomator2");
            options.setDeviceName(ConfigReader.get("device_name"));
            options.setAppPackage(ConfigReader.get("app_package"));
            options.setAppActivity(ConfigReader.get("app_activity"));
            options.setDisableWindowAnimation(true);
            options.setAutoGrantPermissions(true);

            applyChromedriverIfPresent(options);

            AppiumDriver driver =
                    new AndroidDriver(new URL(serverUrl), options);

            driver.manage()
                    .timeouts()
                    .implicitlyWait(Duration.ofSeconds(5));

            return driver;
        }

        /* ===================================================
           IOS
           =================================================== */
        if ("ios".equals(platform)) {

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

            AppiumDriver driver =
                    new IOSDriver(new URL(serverUrl), options);

            driver.manage()
                    .timeouts()
                    .implicitlyWait(Duration.ofSeconds(5));

            return driver;
        }

        throw new RuntimeException("❌ Invalid platform in config: " + platform);
    }

    /* =======================================================
       CHROMEDRIVER HELPER (OS SAFE)
       ======================================================= */
    private static void applyChromedriverIfPresent(
            UiAutomator2Options options
    ) {
        String basePath = ConfigReader.get("chromedriver_base_path");
        if (basePath == null || basePath.isBlank()) return;

        String os = detectOSFolder();
        String driverName =
                os.equals("win") ? "chromedriver.exe" : "chromedriver";

        File chromeDriver = new File(
                System.getProperty("user.dir"),
                basePath + File.separator + os + File.separator + driverName
        );

        if (!chromeDriver.exists()) {
            throw new RuntimeException(
                    "❌ Chromedriver not found for OS [" + os + "] at: "
                            + chromeDriver.getAbsolutePath()
            );
        }

        options.setChromedriverExecutable(
                chromeDriver.getAbsolutePath()
        );
    }

    private static String detectOSFolder() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) return "win";
        if (os.contains("mac")) return "mac";
        if (os.contains("nux") || os.contains("linux")) return "linux";
        throw new RuntimeException("Unsupported OS: " + os);
    }

    /* =======================================================
       APPIUM HEALTH CHECK
       ======================================================= */
    private static void verifyAppiumServerIsRunning(String serverUrl) {
        try {
            URL status = new URL(serverUrl + "/status");
            HttpURLConnection conn =
                    (HttpURLConnection) status.openConnection();
            conn.setConnectTimeout(3000);
            conn.connect();

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "❌ Appium server is NOT reachable at " + serverUrl +
                            "\n➡ Start it manually or via AppiumServerManager"
            );
        }
    }
}
