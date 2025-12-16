package hooks;

import factory.AppiumServerManager;
import factory.DriverFactory;
import listeners.ExtentManager;
import listeners.ExtentTestManager;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.Status;

import io.appium.java_client.AppiumDriver;
import io.cucumber.java.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import utils.ConfigReader;
import utils.Timeouts;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Hooks {

    private static AppiumDriver driver;

    /* =======================================================
   APPIUM SERVER LIFECYCLE (LOCAL ONLY, ONCE PER RUN)
   ======================================================= */
    @BeforeAll
    public static void beforeAll() {
        String runEnv = ConfigReader.getOrDefault("run_env", "local");

        if (!"local".equalsIgnoreCase(runEnv)) {
            System.out.println(
                    ">>> run_env=" + runEnv + " → Skipping Appium server startup"
            );
            return;
        }

        AppiumServerManager.startServerIfRequired();
    }

    /* =======================================================
       BEFORE SCENARIO
       ======================================================= */
    @Before
    public void beforeScenario(Scenario scenario) throws Exception {

        ExtentReports extent = ExtentManager.getInstance();
        ExtentTestManager.setTest(extent.createTest(scenario.getName()));

        DriverFactory.createDriver();
        driver = DriverFactory.getDriver();

        ExtentTestManager.getTest()
                .log(Status.INFO, "Starting Scenario: " + scenario.getName());

        System.out.println("Global timeout (seconds): " + Timeouts.global().getSeconds());

        startRecordingIfRequired();
    }

    /* =======================================================
       AFTER SCENARIO
       ======================================================= */
    @After
    public void afterScenario(Scenario scenario) {

        if (scenario.isFailed()) {
            attachFailureScreenshot();
            ExtentTestManager.getTest().fail("Scenario Failed");
        } else {
            ExtentTestManager.getTest().pass("Scenario Passed");
        }

        stopRecordingIfRequired(scenario);

        DriverFactory.quitDriver();
        ExtentTestManager.unload();
    }

    /* =======================================================
       RECORDING CONTROL (LOCAL ONLY)
       ======================================================= */
    private void startRecordingIfRequired() {

        String runEnv = ConfigReader.getOrDefault("run_env", "local");
        boolean record =
                Boolean.parseBoolean(ConfigReader.getOrDefault("record_video", "false"));

        if (!"local".equalsIgnoreCase(runEnv) || !record) {
            return; // 🚫 No recording
        }

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("maxDurationSec", 1800);
            params.put("bitRate", 800000);
            params.put("videoType", "mp4");
            params.put("resolution", "720x1280");

            driver.executeScript("mobile: startMediaProjectionRecording", params);
        } catch (Exception e) {
            System.out.println("⚠ Failed to start recording: " + e.getMessage());
        }
    }

    private void stopRecordingIfRequired(Scenario scenario) {

        String runEnv = ConfigReader.getOrDefault("run_env", "local");
        boolean record =
                Boolean.parseBoolean(ConfigReader.getOrDefault("record_video", "false"));

        if (!"local".equalsIgnoreCase(runEnv) || !record) {
            return; // 🚫 No recording
        }

        try {
            Object result = driver.executeScript("mobile: stopMediaProjectionRecording");
            if (result == null) return;

            byte[] videoBytes =
                    Base64.getDecoder().decode(result.toString());

            String dir =
                    ConfigReader.getOrDefault("recording_path", "test-output/videos");

            Files.createDirectories(Paths.get(dir));

            String safeName = sanitize(scenario.getName()) + ".mp4";
            Path videoPath = Paths.get(dir, safeName);

            Files.write(videoPath, videoBytes);

            ExtentTestManager.getTest()
                    .log(Status.INFO, "Video saved: " + videoPath);

        } catch (Exception e) {
            System.out.println("⚠ Failed to stop/save recording: " + e.getMessage());
        }
    }

    /* =======================================================
       SCREENSHOT ON FAILURE
       ======================================================= */
    private void attachFailureScreenshot() {
        try {
            String base64 =
                    ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
            ExtentTestManager.getTest()
                    .addScreenCaptureFromBase64String(base64, "Failure Screenshot");
        } catch (Exception ignored) {}
    }

    /* =======================================================
       HELPERS
       ======================================================= */
    private String sanitize(String name) {
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    public static AppiumDriver getDriver() {
        return driver;
    }

    @AfterAll
    public static void afterAll() {
        String runEnv = ConfigReader.getOrDefault("run_env", "local");

        if (!"local".equalsIgnoreCase(runEnv)) {
            return;
        }

        AppiumServerManager.stopServerIfRequired();
    }
}
