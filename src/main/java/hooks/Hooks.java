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
import utils.DeviceContext;
import utils.DevicePool;
import models.DeviceConfig;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class Hooks {


    private static final Logger log = Log.getLogger(Hooks.class);

    /* =======================================================
       APPIUM SERVER LIFECYCLE (LOCAL ONLY)
       ======================================================= */
    @BeforeAll
    public static void beforeAll() {
        log.info("Before All....");

        if (!ConfigReader.isLocal()) {
            System.out.println(
                    ">>> run_env=browserstack → Skipping Appium server startup"
            );
            return;
        }
        AppiumServerManager.startServerIfRequired();
    }

    /* =======================================================
       BEFORE SCENARIO → ACQUIRE DEVICE
       ======================================================= */
    @Before
    public void beforeScenario(Scenario scenario) throws Exception {

        // 🔐 Acquire device for THIS scenario
        DeviceConfig device = DevicePool.acquire();
        DeviceContext.set(device);

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

    /* =======================================================
       STEP VISIBILITY (CONSOLE + REPORT)
       ======================================================= */
    @BeforeStep
    public void beforeStep(Scenario scenario) {

        DeviceConfig device = DeviceContext.get();

        System.out.println(
                "➡ STEP START | THREAD=" + Thread.currentThread().getId()
                        + " | DEVICE=" + device.device()
                        + " | SCENARIO=" + scenario.getName()
        );
    }

    @AfterStep
    public void afterStep(Scenario scenario) {

        DeviceConfig device = DeviceContext.get();

        System.out.println(
                "⬅ STEP END   | THREAD=" + Thread.currentThread().getId()
                        + " | DEVICE=" + device.device()
                        + " | STATUS=" + scenario.getStatus()
        );
    }

    /* =======================================================
       AFTER SCENARIO → RELEASE DEVICE
       ======================================================= */
    @After
    public void afterScenario(Scenario scenario) {

        DeviceConfig device = DeviceContext.get();

        if (scenario.isFailed()) {
            attachFailureScreenshot();
            ExtentTestManager.getTest().fail("Scenario Failed");
        } else {
            ExtentTestManager.getTest().pass("Scenario Passed");
        }

        DriverFactory.quitDriver();

        // 🔓 Release device back to pool
        DevicePool.release(device);
        DeviceContext.clear();

        System.out.println(
                "🏁 END SCENARIO | THREAD=" + Thread.currentThread().getId()
                        + " | DEVICE=" + device.device()
                        + " | STATUS=" + scenario.getStatus()
        );

        ExtentTestManager.unload();
    }

    /* =======================================================
       SCREENSHOT ON FAILURE
       ======================================================= */
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
        } catch (Exception ignored) {
        }
    }

    /* =======================================================
       APPIUM SERVER SHUTDOWN (LOCAL ONLY)
       ======================================================= */
    @AfterAll
    public static void afterAll() {
        if (ConfigReader.isLocal()) {
            AppiumServerManager.stopServerIfRequired();
        }
    }
}
