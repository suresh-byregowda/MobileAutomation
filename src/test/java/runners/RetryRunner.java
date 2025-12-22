package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = "@target/failed_scenarios.txt",
        glue = {"hooks", "steps"},
        monochrome = true,
        plugin = {
                "pretty",
                "json:target/cucumber-retry.json",
                "html:target/cucumber-html-report-retry"
        }
)
public class RetryRunner extends AbstractTestNGCucumberTests {

        @Override
        @DataProvider(parallel = true)   // ✅ SAME PARALLELISM
        public Object[][] scenarios() {
                return super.scenarios();
        }
}
