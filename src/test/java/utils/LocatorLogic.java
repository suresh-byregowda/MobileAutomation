package utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.keys.LocatorKey;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class LocatorLogic {

    private final AppiumDriver driver;
    private final String platform;

    // MAANG recommended timeout
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(45);

    public LocatorLogic(AppiumDriver driver) {
        this.driver = driver;
        this.platform = ConfigReader.getOrDefault("platform", "android").toLowerCase();
    }

    /* -------------------------------
            PUBLIC ENTRY POINTS
       ------------------------------- */

    public WebElement one_ele(LocatorKey key) {
        return get(key.page(), key.key());
    }

    public List<WebElement> mul_ele(LocatorKey key) {
        return getAll(key.page(), key.key());
    }

    /* -------------------------------
                CORE RESOLUTION
       ------------------------------- */

    public WebElement get(PageName page, String locatorKey) {

        // Use NEW LocatorReader (enum compatible)
        Map<LocatorStrategy, String> spec = LocatorReader.getLocatorSpec(page, locatorKey);

        // 1) Try direct strategies by priority
        for (LocatorStrategy strategy : ByFactory.defaultPriority()) {
            if (spec.containsKey(strategy)) {

                By by = ByFactory.from(strategy, spec.get(strategy));

                try {
                    WebElement el = waitForVisible(by);
                    if (el != null) return el;

                } catch (Exception ignored) {}
            }
        }

        // 2) Heuristic fallback
        var candidates = AutoHeuristics.generate(page, locatorKey, spec, platform);

        for (var candidate : candidates) {
            By by = ByFactory.from(candidate.strategy(), candidate.value());

            try {
                WebElement el = waitForVisible(by);
                if (el != null) return el;

            } catch (Exception ignored) {}
        }

        throw new NoSuchElementException(
                "❌ No element found for: page=" + page.jsonKey() +
                        ", key=" + locatorKey +
                        ", platform=" + platform
        );
    }

    public List<WebElement> getAll(PageName page, String locatorKey) {

        Map<LocatorStrategy, String> spec = LocatorReader.getLocatorSpec(page, locatorKey);

        // 1) Direct strategies
        for (LocatorStrategy strategy : ByFactory.defaultPriority()) {

            if (spec.containsKey(strategy)) {
                By by = ByFactory.from(strategy, spec.get(strategy));

                try {
                    List<WebElement> list = waitForAllVisible(by);
                    if (list != null && !list.isEmpty()) return list;

                } catch (Exception ignored) {}
            }
        }

        // 2) Heuristic fallback
        var candidates = AutoHeuristics.generate(page, locatorKey, spec, platform);

        for (var candidate : candidates) {
            By by = ByFactory.from(candidate.strategy(), candidate.value());

            try {
                List<WebElement> list = waitForAllVisible(by);
                if (list != null && !list.isEmpty()) return list;

            } catch (Exception ignored) {}
        }

        // return empty list as original behavior
        return List.of();
    }

    /* -------------------------------
            WAIT HELPERS
       ------------------------------- */

    private WebElement waitForVisible(By by) {
        return new WebDriverWait(driver, DEFAULT_TIMEOUT)
                .until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    private List<WebElement> waitForAllVisible(By by) {
        return new WebDriverWait(driver, DEFAULT_TIMEOUT)
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
    }

    private WebElement waitForPresent(By by) {
        return new WebDriverWait(driver, DEFAULT_TIMEOUT)
                .until(ExpectedConditions.presenceOfElementLocated(by));
    }

    private List<WebElement> waitForAllPresent(By by) {
        return new WebDriverWait(driver, DEFAULT_TIMEOUT)
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
    }
}
