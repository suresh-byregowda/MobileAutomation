package listeners;


import com.aventstack.extentreports.ExtentTest;


import java.util.HashMap;
import java.util.Map;


/**
 * Thread-safe test manager for ExtentTest instances.
 */
public class ExtentTestManager {
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private static Map<Long, ExtentTest> allTests = new HashMap<>();


    public synchronized static ExtentTest getTest() {
        return extentTest.get();
    }


    public synchronized static void setTest(ExtentTest test) {
        extentTest.set(test);
        allTests.put(Thread.currentThread().getId(), test);
    }


    public synchronized static void unload() {
        extentTest.remove();
    }


    public synchronized static Map<Long, ExtentTest> getAllTests() {
        return allTests;
    }
}