package utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int attempts = 0;
    private static final int MAX_RETRY_COUNT = 2;   // Retry 2 times (total attempts = 3)

    @Override
    public boolean retry(ITestResult result) {
        if (!result.isSuccess() && attempts < MAX_RETRY_COUNT) {
            attempts++;
            System.out.println(
                    "⚠ Retrying Test: " + result.getName() +
                            " | Attempt " + attempts + " of " + MAX_RETRY_COUNT
            );

            return true; // retry test
        }

        return false; // do not retry
    }
}
