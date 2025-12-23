package listeners;

import org.testng.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DeviceExecutionSummaryListener implements ITestListener, ISuiteListener {

    private static final Set<String> PASSED_DEVICES =
            ConcurrentHashMap.newKeySet();

    private static final Set<String> SKIPPED_DEVICES =
            ConcurrentHashMap.newKeySet();

    private String deviceLabel() {
        String device = System.getProperty("device", "UNKNOWN_DEVICE");
        String os     = System.getProperty("os_version", "UNKNOWN_OS");
        return device + " (Android " + os + ")";
    }

    /* ================= TEST EVENTS ================= */

    @Override
    public void onTestSuccess(ITestResult result) {
        PASSED_DEVICES.add(deviceLabel());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        SKIPPED_DEVICES.add(deviceLabel());
    }

    /* ================= FINAL SUMMARY ================= */

    @Override
    public void onFinish(ISuite suite) {

        System.out.println("\n================ DEVICE EXECUTION SUMMARY ================\n");

        if (!SKIPPED_DEVICES.isEmpty()) {
            System.out.println("SKIPPED:");
            SKIPPED_DEVICES.forEach(
                    d -> System.out.println("  - " + d + " – unavailable")
            );
            System.out.println();
        }

        if (!PASSED_DEVICES.isEmpty()) {
            System.out.println("PASSED:");
            PASSED_DEVICES.forEach(
                    d -> System.out.println("  - " + d)
            );
        }

        System.out.println("\n==========================================================\n");
    }
}
