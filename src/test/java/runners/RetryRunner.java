package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "@target/failed_scenarios.txt",
        glue = {"hooks", "steps"},
        plugin = {
                "pretty",
                "json:target/cucumber-retry.json",
                "html:target/cucumber-retry-report"
        },
        monochrome = true
)
public class RetryRunner extends AbstractTestNGCucumberTests {
}