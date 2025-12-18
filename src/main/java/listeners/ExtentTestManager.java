package listeners;

import com.aventstack.extentreports.ExtentTest;

/**
 * Thread-safe manager for ExtentTest instances.
 * One ExtentTest per thread.
 */
public final class ExtentTestManager {

    private static final ThreadLocal<ExtentTest> EXTENT_TEST =
            new ThreadLocal<>();

    private ExtentTestManager() {}

    public static ExtentTest getTest() {
        return EXTENT_TEST.get();
    }

    public static void setTest(ExtentTest test) {
        EXTENT_TEST.set(test);
    }

    public static void unload() {
        EXTENT_TEST.remove();
    }
}
