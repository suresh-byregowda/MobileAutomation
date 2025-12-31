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
import org.testng.Reporter;

import utils.ConfigReader;
import utils.DeviceContext;
import utils.DevicePool;
import models.DeviceConfig;

public class Hooks {

    @BeforeAll
    public static void beforeAll() {
        if (ConfigReader.isLocal()) {
            AppiumServerManager.startServerIfRequired();
        }
    }

    @Before(order = 0)
    public void beforeScenario(Scenario scenario) throws Exception {

        DeviceConfig device;

        if (ConfigReader.isBrowserStack()) {
            device = DevicePool.acquire();
        } else {
            device = new DeviceConfig(
                    ConfigReader.getOrDefault("device_name", "LOCAL_DEVICE"),
                    ConfigReader.getOrDefault("platform_version", "LOCAL_OS")
            );
        }

        DeviceContext.set(device);

        Reporter.getCurrentTestResult()
                .setAttribute(
                        "deviceLabel",
                        device.device() + " (Android " + device.os() + ")"
                );

        System.out.println(
                "🚀 START SCENARIO | THREAD=" + Thread.currentThread().getId()
                        + " | DEVICE=" + device.device()
                        + " | OS=" + device.os()
                        + " | SCENARIO=" + scenario.getName()
        );

        ExtentReports extent = ExtentManager.getInstance();
        ExtentTestManager.setTest(
                extent.createTest(
                        scenario.getName()
                                + " [" + device.device()
                                + " | Android " + device.os() + "]"
                )
        );

        DriverFactory.createDriver();
        ExtentTestManager.getTest().log(Status.INFO, "Scenario Started");
    }

    @After(order = Integer.MAX_VALUE)
    public void afterScenario(Scenario scenario) {

        DeviceConfig device = DeviceContext.get();

        try {
            if (scenario.isFailed()) {
                attachFailureScreenshot();
                ExtentTestManager.getTest().fail("Scenario Failed");
            } else {
                ExtentTestManager.getTest().pass("Scenario Passed");
            }
        } finally {

            DriverFactory.quitDriver();

            if (ConfigReader.isBrowserStack()) {
                DevicePool.release(device);
            }

            System.out.println(
                    "🏁 END SCENARIO | THREAD=" + Thread.currentThread().getId()
                            + " | DEVICE=" + device.device()
                            + " | STATUS=" + scenario.getStatus()
            );

            ExtentTestManager.unload();
            DeviceContext.clear();
        }
    }

    private void attachFailureScreenshot() {
        try {
            AppiumDriver driver = DriverFactory.getDriver();

            String base64 =
                    ((TakesScreenshot) driver)
                            .getScreenshotAs(OutputType.BASE64);

            ExtentTestManager.getTest()
                    .addScreenCaptureFromBase64String(
                            base64, "Failure Screenshot"
                    );
        } catch (Exception ignored) {}
    }

    @AfterAll
    public static void afterAll() {
        if (ConfigReader.isLocal()) {
            AppiumServerManager.stopServerIfRequired();
        }
    }
}
