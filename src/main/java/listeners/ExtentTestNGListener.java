package listeners;

import com.aventstack.extentreports.ExtentReports;
import org.testng.ITestContext;
import org.testng.ITestListener;

/**
 * Minimal TestNG listener for ExtentReports.
 * Actual test lifecycle is handled by Cucumber Hooks.
 */
public class ExtentTestNGListener implements ITestListener {

    private static final ExtentReports extent =
            ExtentManager.getInstance();

    @Override
    public void onStart(ITestContext context) {
        // Optional: suite-level logging
        System.out.println(">>> TestNG suite started: " + context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        // Flush ONCE per suite
        extent.flush();
        System.out.println(">>> TestNG suite finished: " + context.getName());
    }
}
