package listeners;

import org.testng.*;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DeviceExecutionSummaryListener
        implements ITestListener, ISuiteListener {

    private static final Set<String> PASSED =
            ConcurrentHashMap.newKeySet();

    private static final Set<String> SKIPPED =
            ConcurrentHashMap.newKeySet();

    private String label(ITestResult result) {
        Object l = result.getAttribute("deviceLabel");
        return l == null ? "UNKNOWN" : l.toString();
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        PASSED.add(label(result));
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        SKIPPED.add(label(result));
    }

    @Override
    public void onFinish(ISuite suite) {

        System.out.println(
                "\n================ DEVICE EXECUTION SUMMARY ================\n"
        );

        if (!SKIPPED.isEmpty()) {
            System.out.println("SKIPPED:");
            SKIPPED.forEach(d -> System.out.println("  - " + d));
        }

        if (!PASSED.isEmpty()) {
            System.out.println("\nPASSED:");
            PASSED.forEach(d -> System.out.println("  - " + d));
        }

        System.out.println(
                "\n==========================================================\n"
        );
    }
}
