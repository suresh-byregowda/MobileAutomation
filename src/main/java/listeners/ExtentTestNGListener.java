package listeners;


import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.ScreenshotUtil;

/**
 * TestNG listener that writes test results to ExtentReports.
 */
public class ExtentTestNGListener implements ITestListener {


    private static ExtentReports extent = ExtentManager.getInstance();


    @Override
    public void onStart(ITestContext context) {
// no-op
    }


    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }


    @Override
    public void onTestStart(ITestResult result) {
        String testName = getTestName(result);
        ExtentTest test = extent.createTest(testName);
        ExtentTestManager.setTest(test);
        test.log(Status.INFO, "Test started: " + testName);
    }


    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = ExtentTestManager.getTest();
        if (test != null) test.log(Status.PASS, "Test passed");
        ExtentTestManager.unload();
    }


    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = ExtentTestManager.getTest();
        if (test != null) {
            test.log(Status.FAIL, result.getThrowable());
            try {
                String path = ScreenshotUtil.takeScreenshotOnFailure(result.getMethod().getMethodName());
                if (path != null) test.addScreenCaptureFromPath(path);
            } catch (Exception e) {
                test.log(Status.WARNING, "Failed to attach screenshot: " + e.getMessage());
            }
        }
        ExtentTestManager.unload();
    }


    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = ExtentTestManager.getTest();
        if (test != null) test.log(Status.SKIP, "Test skipped");
        ExtentTestManager.unload();
    }


    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
// no-op
    }


    private String getTestName(ITestResult result) {
        if (result.getMethod().getDescription() != null && !result.getMethod().getDescription().isEmpty()) {
            return result.getMethod().getDescription();
        }
        return result.getMethod().getMethodName();
    }
}