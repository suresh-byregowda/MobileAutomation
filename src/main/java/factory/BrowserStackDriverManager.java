package factory;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import models.DeviceConfig;
import org.json.simple.JSONObject;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.remote.DesiredCapabilities;
import utils.ConfigReader;
import utils.DeviceContext;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;

public final class BrowserStackDriverManager {

    private BrowserStackDriverManager() {}

    public static AppiumDriver createBrowserStackDriver() throws Exception {

        System.out.println(">>> Starting BrowserStack Driver");

        JSONObject json =
                ConfigReader.loadJsonFromClasspath("capabilities/browserstack.json");

        String user = ConfigReader.get("browserstack.username");
        String key  = ConfigReader.get("browserstack.accessKey");

        if (isEmpty(user) || isEmpty(key)) {
            throw new RuntimeException("❌ BrowserStack credentials missing");
        }

        String remoteUrl = String.format(
                "https://%s:%s@hub-cloud.browserstack.com/wd/hub",
                user, key
        );

        String platform =
                ConfigReader.getOrDefault("platform", "android").toLowerCase();

        JSONObject commonCaps = (JSONObject) json.get("commonCapabilities");
        JSONObject platformCaps = (JSONObject) json.get(platform);

        DesiredCapabilities caps = new DesiredCapabilities();

        if (commonCaps != null) {
            commonCaps.forEach((k, v) -> caps.setCapability(k.toString(), v));
        }
        platformCaps.forEach((k, v) -> caps.setCapability(k.toString(), v));

        /* =======================================================
           DEVICE FROM THREAD-LOCAL CONTEXT (ONLY SOURCE)
           ======================================================= */
        DeviceConfig dc = DeviceContext.get();

        if (dc == null) {
            throw new RuntimeException("❌ DeviceContext not set for thread");
        }

        String device = dc.device();
        String osVersion = dc.os();

        caps.setCapability("deviceName", device);
        caps.setCapability("osVersion", osVersion);

        String bsApp = ConfigReader.get("bs_app");
        if (isEmpty(bsApp)) {
            throw new RuntimeException("❌ bs_app missing in env config");
        }

        caps.setCapability("app", bsApp);

        caps.setCapability(
                "build",
                System.getProperty(
                        "bs.build",
                        "Android-Parallel-" + LocalDate.now()
                )
        );

        caps.setCapability(
                "name",
                device + " | Android " + osVersion
        );

        System.out.println(
                ">>> Launching on: " + device + " (Android " + osVersion + ")"
        );

        try {
            AppiumDriver driver =
                    "android".equals(platform)
                            ? new AndroidDriver(new URL(remoteUrl), caps)
                            : new IOSDriver(new URL(remoteUrl), caps);

            driver.manage()
                    .timeouts()
                    .implicitlyWait(Duration.ofSeconds(5));

            return driver;

        } catch (SessionNotCreatedException e) {
            System.out.println(
                    "⚠ SKIPPED: " + device + " (Android " + osVersion + ") – unavailable"
            );
            throw e;
        }
    }

    private static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
