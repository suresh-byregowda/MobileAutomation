package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import listeners.ExtentTestNGListener;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;

@Listeners({ExtentTestNGListener.class})
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"hooks", "steps"},
        tags = "@general",
        monochrome = true,
        plugin = {
                "pretty",
                "summary",
                "html:target/cucumber-html-report",
                "json:target/cucumber.json",
                "rerun:target/failed_scenarios.txt"
        }
)
public class TestRunner extends AbstractTestNGCucumberTests {

    /**
     * ✅ KEY POINT:
     * - Scenario parallelism handled by TestNG
     * - Device parallelism handled by DevicePool
     * - DO NOT use @Factory here
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
