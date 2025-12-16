package factory;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.json.simple.JSONObject;
import org.openqa.selenium.remote.DesiredCapabilities;
import utils.ConfigReader;

import java.net.URL;
import java.time.Duration;

public final class BrowserStackDriverManager {

    private BrowserStackDriverManager() {}

    public static AppiumDriver createBrowserStackDriver() throws Exception {

        System.out.println(">>> Starting BrowserStack Driver");

        // ---------------------------------------------------
        // Load BrowserStack capabilities (CLASSPATH SAFE)
        // ---------------------------------------------------
        JSONObject json = ConfigReader.loadJsonFromClasspath(
                "capabilities/browserstack.json"
        );

        // ---------------------------------------------------
        // Credentials (Jasypt decrypted inside ConfigReader)
        // ---------------------------------------------------
        String user = ConfigReader.get("browserstack.username");
        String key  = ConfigReader.get("browserstack.accessKey");

        if (isEmpty(user) || isEmpty(key)) {
            throw new RuntimeException("❌ BrowserStack credentials missing");
        }

        String remoteUrl = String.format(
                "https://%s:%s@hub-cloud.browserstack.com/wd/hub",
                user, key
        );

        System.out.println(
                ">>> Connecting to BrowserStack as user: " + mask(user)
        );

        // ---------------------------------------------------
        // Platform selection
        // ---------------------------------------------------
        String platform = ConfigReader
                .getOrDefault("platform", "android")
                .toLowerCase();

        JSONObject capsJson = platform.equals("android")
                ? (JSONObject) json.get("androidCapabilities")
                : (JSONObject) json.get("iosCapabilities");

        if (capsJson == null) {
            throw new RuntimeException(
                    "❌ Missing capability block for platform: " + platform
            );
        }

        DesiredCapabilities caps = new DesiredCapabilities();
        capsJson.forEach((k, v) -> caps.setCapability(k.toString(), v));

        // ---------------------------------------------------
        // Environment-specific injection
        // ---------------------------------------------------
        String bsApp = ConfigReader.get("bs_app");
        if (isEmpty(bsApp)) {
            throw new RuntimeException("❌ bs_app is missing in env config");
        }

        caps.setCapability("app", bsApp);
        caps.setCapability(
                "name",
                System.getProperty("testName", "Mobile Automation Test")
        );

        // ---------------------------------------------------
        // HARD GUARD — NEVER SET LOCAL PATHS ON BROWSERSTACK
        // ---------------------------------------------------
        if (caps.asMap().containsKey("appium:chromedriverExecutable")) {
            throw new RuntimeException(
                    "❌ chromedriverExecutable must NOT be set for BrowserStack"
            );
        }

        // ---------------------------------------------------
        // Driver creation
        // ---------------------------------------------------
        AppiumDriver driver =
                platform.equals("android")
                        ? new AndroidDriver(new URL(remoteUrl), caps)
                        : new IOSDriver(new URL(remoteUrl), caps);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        return driver;
    }

    /* =======================================================
       HELPERS
       ======================================================= */

    private static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String mask(String value) {
        if (value == null || value.length() < 4) return "****";
        return value.substring(0, 2)
                + "****"
                + value.substring(value.length() - 2);
    }
}
