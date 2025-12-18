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
        plugin = {
                "pretty",
                "json:target/cucumber.json",
                "html:target/cucumber-html-report",
                "rerun:target/failed_scenarios.txt"
        },
        monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {

    // DO NOT try to make this dynamic
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
