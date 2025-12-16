package utils;

import hooks.Hooks;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.remote.SupportsContextSwitching;
import io.appium.java_client.remote.SupportsRotation;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebElement;

import java.io.File;
import java.time.Duration;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class MobileUtility {

    private MobileUtility() {
    }

    /* =======================================================
                     DRIVER + JS HELPERS
       ======================================================= */
    public static AppiumDriver driver() {
        return Hooks.getDriver();
    }

    private static JavascriptExecutor js() {
        return (JavascriptExecutor) driver();
    }

    private static String id(WebElement el) {
        return ((RemoteWebElement) el).getId();
    }

    /* =======================================================
                 PLATFORM DETECTION (ANDROID/IOS)
       ======================================================= */
    // Detects Android platform
    private static boolean isAndroid() {
        String p = driver().getCapabilities().getPlatformName().toString().toLowerCase();
        return p.contains("android");
    }

    // Detects iOS platform
    private static boolean isIOS() {
        String p = driver().getCapabilities().getPlatformName().toString().toLowerCase();
        return p.contains("ios");
    }

    /* =======================================================
                 ANDROID SCROLL TO TEXT
       ======================================================= */
    // ANDROID ONLY
// Scrolls until the given text is visible using UiScrollable
    public static void scrollToText(String text) {
        if (!isAndroid()) {
            System.out.println("⚠ scrollToText ignored (Android only). Use scrollIOS() for iOS.");
            return;
        }

        String uiSelector = "new UiScrollable(new UiSelector().scrollable(true))"
                + ".scrollIntoView(new UiSelector().textContains(\"" + text + "\"));";

        driver().findElement(AppiumBy.androidUIAutomator(uiSelector));
    }


    /* =======================================================
                     iOS SCROLL TO LABEL
       ======================================================= */
    // iOS ONLY
// Scrolls down until label matches predicateString
    public static void scrollIOS(String label) {
        if (!isIOS()) return;

        Map<String, Object> params = new HashMap<>();
        params.put("predicateString", "label == '" + label + "'");
        params.put("direction", "down");

        js().executeScript("mobile: scroll", params);
    }

    // BOTH (Android + iOS)
    // Scrolls until a given element is visible
    public static void scrollToElement(WebElement element) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("elementId", id(element));

            if (isAndroid()) {
                params.put("direction", "down");
                js().executeScript("mobile: scrollGesture", params);
            } else {
                js().executeScript("mobile: scroll", params);
            }
        } catch (Exception e) {
            System.out.println("⚠ scrollToElement failed");
        }
    }


    /* =======================================================
                     CROSS-PLATFORM SCROLL DOWN
       ======================================================= */
    // BOTH
    // Scrolls DOWN on the screen
    public static void scrollDown() {
        try {
            if (isAndroid()) {
                Map<String, Object> params = new HashMap<>();
                params.put("direction", "down");
                params.put("percent", 0.8);
                js().executeScript("mobile: scrollGesture", params);
            } else if (isIOS()) {
                Map<String, Object> params = new HashMap<>();
                params.put("direction", "down");
                params.put("velocity", 2500);
                js().executeScript("mobile: swipe", params);
            }
        } catch (Exception e) {
            System.out.println("⚠ scrollDown failed: " + e.getMessage());
        }
    }

    // BOTH
    // Scrolls UP on the screen
    public static void scrollUp() {
        try {
            if (isAndroid()) {
                Map<String, Object> params = new HashMap<>();
                params.put("direction", "up");
                params.put("percent", 0.8);

                js().executeScript("mobile: scrollGesture", params);
            } else if (isIOS()) {
                Map<String, Object> params = new HashMap<>();
                params.put("direction", "up");
                params.put("velocity", 2500);

                js().executeScript("mobile: swipe", params);
            }
        } catch (Exception e) {
            System.out.println("⚠ scrollUp failed: " + e.getMessage());
        }
    }


    /* =======================================================
                     SWIPE (Direction Based)
       ======================================================= */

    // BOTH
// Performs swipe in given direction (up/down/left/right)
    public static void swipe(String direction, double percent) {
        Map<String, Object> params = new HashMap<>();
        params.put("direction", direction);
        params.put("percent", percent);

        if (isAndroid()) {
            js().executeScript("mobile: swipeGesture", params);
        } else {
            js().executeScript("mobile: swipe", params);
        }
    }


    /* =======================================================
                     DRAG AND DROP
       ======================================================= */

    // BOTH
    // Drags source element and drops on destination
    public static void dragAndDrop(WebElement source, WebElement destination) {
        Map<String, Object> params = new HashMap<>();
        params.put("sourceId", id(source));
        params.put("destinationId", id(destination));

        if (isAndroid()) {
            js().executeScript("mobile: dragGesture", params);
        } else {
            js().executeScript("mobile: dragFromToForDuration", params);
        }
    }


    /* =======================================================
                    SCREEN ORIENTATION
       ======================================================= */

   /* =======================================================
               SCREEN ORIENTATION (FIXED)
   ======================================================= */

    public static void setLandscape() {
        if (driver() instanceof SupportsRotation) {
            ((SupportsRotation) driver()).rotate(ScreenOrientation.LANDSCAPE);
        } else {
            throw new UnsupportedOperationException("Driver does not support rotation");
        }
    }

    public static void setPortrait() {
        if (driver() instanceof SupportsRotation) {
            ((SupportsRotation) driver()).rotate(ScreenOrientation.PORTRAIT);
        } else {
            throw new UnsupportedOperationException("Driver does not support rotation");
        }
    }


    /* =======================================================
                     CROSS-PLATFORM TAP
       ======================================================= */
    // BOTH
    // Single tap on element
    public static void tap(WebElement element) {
        Map<String, Object> params = new HashMap<>();
        params.put("elementId", id(element));

        if (isAndroid()) {
            js().executeScript("mobile: tapGesture", params);
        } else {
            // iOS equivalent
            params.put("duration", 0.1);
            js().executeScript("mobile: touchAndHold", params);
        }
    }

    // BOTH
    // Double tap on element
    public static void doubleTap(WebElement element) {
        Map<String, Object> params = new HashMap<>();
        params.put("elementId", id(element));

        if (isAndroid()) {
            js().executeScript("mobile: doubleClickGesture", params);
        } else {
            js().executeScript("mobile: doubleTap", params);
        }
    }


    /* =======================================================
                 CROSS-PLATFORM BACK ACTION
       ======================================================= */
    // BOTH
// Android → presses hardware BACK button
// iOS → navigate back or tap "Back" button


    public static void performAndroidBack() {
        try {
            // Preferred: hardware BACK key
            ((io.appium.java_client.android.AndroidDriver) driver())
                    .pressKey(new io.appium.java_client.android.nativekey.KeyEvent(
                            io.appium.java_client.android.nativekey.AndroidKey.BACK));
            System.out.println("✔ Android BACK key pressed");
        } catch (Exception e) {
            System.out.println("ℹ BACK key failed, fallback to navigate().back()");
            driver().navigate().back();
        }
    }


    public static void performIOSBack() {
        try {
            // Try common nav bar Back button
            try {
                driver().findElement(AppiumBy.accessibilityId("Back")).click();
                System.out.println("✔ iOS tapped Back button");
                return;
            } catch (Exception ignored) {
            }
            // Fallback: browser-like back for WebView
            driver().navigate().back();
            System.out.println("✔ iOS navigate().back() (fallback)");
        } catch (Exception e) {
            System.out.println("❌ iOS back action failed: " + e.getMessage());
        }
    }


        public static void performBack() {
        if (isAndroid()) {
            Map<String, Object> params = new HashMap<>();
            params.put("action", "back");
            driver().executeScript("mobile: pressButton", params);
        } else if (isIOS()) {
            try {
                driver().navigate().back();
            } catch (Exception e) {
                System.out.println("⚠ iOS back fallback, tapping navigation bar back button");
                try {
                    driver().findElement(AppiumBy.iOSClassChain("**/XCUIElementTypeButton[`label == \"Back\"`]")).click();
                } catch (Exception ignored) {
                }
            }
        }
    }

    /* =======================================================
                    SAFE CLICK
       ======================================================= */
    // BOTH
// Clicks element safely within timeout
    public static void safeClick(WebElement element, int timeoutSeconds) {
        long end = System.currentTimeMillis() + timeoutSeconds * 1000;

        while (System.currentTimeMillis() < end) {
            try {
                if (element.isDisplayed() && element.isEnabled()) {
                    element.click();
                    return;
                }
            } catch (Exception ignored) {
            }

            sleep(300);
        }

        throw new RuntimeException("❌ safeClick FAILED: element never interactable");
    }

    // BOTH
    // Wrapper for safeClick
    public static void waitAndClick(WebElement element, int t) {
        safeClick(element, t);
    }

    /* =======================================================
                    CONTEXT SWITCHING
       ======================================================= */

    // BOTH
    // Waits until WEBVIEW context appears and switches
    public static void waitForWebView(int timeoutSeconds) {

        SupportsContextSwitching ctx = (SupportsContextSwitching) driver();
        long end = System.currentTimeMillis() + timeoutSeconds * 1000;

        while (System.currentTimeMillis() < end) {

            Set<String> contexts = ctx.getContextHandles();
            System.out.println("Available Contexts: " + contexts);

            for (String c : contexts) {
                if (c.toLowerCase().contains("webview")) {
                    ctx.context(c);
                    System.out.println("✔ Switched to WEBVIEW: " + c);
                    return;
                }
            }
            sleep(500);
        }

        throw new RuntimeException("❌ No WEBVIEW context available after " + timeoutSeconds + "s");
    }

    // BOTH
    // Switches to WEBVIEW (default 30s)
    public static void switchToWebView() {
        waitForWebView(30);
    }

    // BOTH
    // Switches back to NATIVE_APP
    public static void switchToNative() {
        ((SupportsContextSwitching) driver()).context("NATIVE_APP");
        System.out.println("✔ Switched to NATIVE_APP");
    }

    /* =======================================================
                    SCREENSHOTS
       ======================================================= */
    // BOTH
    // Returns screenshot as byte[]
    public static byte[] screenshotBytes() {
        try {
            return ((TakesScreenshot) driver())
                    .getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            return new byte[0];
        }
    }


    // BOTH
// Saves screenshot to test-output/screenshots
    public static byte[] saveScreenshot(String name) {
        try {
            File src = ((TakesScreenshot) driver()).getScreenshotAs(OutputType.FILE);

            File dest = new File("test-output/screenshots/"
                    + name + "_" + System.currentTimeMillis() + ".png");

            dest.getParentFile().mkdirs();
            FileUtils.copyFile(src, dest);
            return FileUtils.readFileToByteArray(dest);
        } catch (Exception e) {
            return new byte[0];
        }
    }


    /* =======================================================
                    ALERTS
       ======================================================= */

    // BOTH
// Accepts system alert if present
    public static void acceptAlert() {
        try {
            driver().switchTo().alert().accept();
        } catch (Exception e) {
            System.out.println("ℹ No alert present");
        }
    }

    // BOTH
// Dismisses system alert if present
    public static void dismissAlert() {
        try {
            driver().switchTo().alert().dismiss();
        } catch (Exception e) {
            System.out.println("ℹ No alert present");
        }
    }



    /* =======================================================
                       DROPDOWN HANDLING
       ======================================================= */

    // ANDROID ONLY
// Scrolls and selects dropdown option by visible text
    public static void selectFromAndroidDropdown(String visibleText) {
        scrollToText(visibleText);
        driver().findElement(By.xpath("//*[@text='" + visibleText + "']")).click();
    }

    // iOS ONLY
// Selects value from iOS PickerWheel
    public static void selectIOSDropdownValue(String value) {

        if (!isIOS()) {
            throw new RuntimeException("❌ selectIOSDropdownValue is iOS only");
        }

        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(20));

        WebElement picker =
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                        AppiumBy.className("XCUIElementTypePickerWheel")));

        picker.sendKeys(value);

        System.out.println("✔ iOS Picker value selected: " + value);
    }


    /* =======================================================
                     ANDROID PERMISSION POPUP
       ======================================================= */

    // ANDROID ONLY
    // Handles runtime permission popups automatically
    public static void handlePermissionPopup() {

        if (!isAndroid()) {
            System.out.println("ℹ handlePermissionPopup is Android-only.");
            return;
        }

        try {
            AppiumDriver driver = Hooks.getDriver();

            String[] androidIDs = {
                    "com.android.permissioncontroller:id/permission_allow_button",
                    "com.android.permissioncontroller:id/permission_allow_one_time_button",
                    "com.android.permissioncontroller:id/permission_allow_foreground_only_button",
                    "com.android.permissioncontroller:id/permission_allow_always_button"
            };

            for (String id : androidIDs) {
                try {
                    WebElement btn = driver.findElement(By.id(id));
                    btn.click();
                    Thread.sleep(500);
                    return;
                } catch (Exception ignored) {
                }
            }

            String[] allowTexts = {
                    "Allow", "ALLOW",
                    "Allow all the time",
                    "Allow this time",
                    "Allow only while using the app",
                    "OK", "Yes"
            };

            for (String txt : allowTexts) {
                try {
                    WebElement btn = driver.findElement(
                            AppiumBy.androidUIAutomator("new UiSelector().textContains(\"" + txt + "\")"));
                    btn.click();
                    Thread.sleep(500);
                    return;
                } catch (Exception ignored) {
                }
            }

        } catch (Exception ignored) {
        }

        System.out.println("ℹ No permission popup found.");
    }

    /* =======================================================
                           HELPERS
       ======================================================= */
    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception ignored) {
        }
    }


    /* =======================================================
       ASSERTIONS (WebElement-based) — uses Timeouts.global()
       ======================================================= */
// Assertion helpers with screenshots on failure
    private static WebDriverWait waitGlobal() {
        return new WebDriverWait(driver(), Timeouts.global());
    }

    private static void assertFail(String message) {
        saveScreenshot("assert_fail_" + System.currentTimeMillis()); // your existing helper
        throw new AssertionError(message);
    }

    /* --- Basic boolean/equality --- */
    public static void assertTrue(boolean condition, String messageIfFail) {
        if (!condition) assertFail("Expected TRUE but was FALSE. " + messageIfFail);
    }

    public static void assertFalse(boolean condition, String messageIfFail) {
        if (condition) assertFail("Expected FALSE but was TRUE. " + messageIfFail);
    }

    public static void assertEquals(Object actual, Object expected, String context) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            assertFail(String.format("Assertion FAILED [%s]: expected=%s, actual=%s", context, expected, actual));
        }
    }

    public static void assertNotEquals(Object actual, Object unexpected, String context) {
        if (unexpected == null ? actual == null : unexpected.equals(actual)) {
            assertFail(String.format("Assertion FAILED [%s]: value should NOT equal %s", context, unexpected));
        }
    }

    /* --- Element state --- */
    public static void assertVisible(WebElement element, String label) {
        try {
            waitGlobal().until(ExpectedConditions.visibilityOf(element));
        } catch (Exception e) {
            assertFail("Element NOT visible: " + label);
        }
    }

    public static void assertNotVisible(WebElement element, String label) {
        boolean invisible;
        try {
            invisible = waitGlobal().until(ExpectedConditions.invisibilityOf(element));
        } catch (Exception e) {
            invisible = false;
        }
        if (!invisible) assertFail("Element STILL visible: " + label);
    }

    public static void assertClickable(WebElement element, String label) {
        try {
            waitGlobal().until(ExpectedConditions.elementToBeClickable(element));
        } catch (Exception e) {
            assertFail("Element NOT clickable: " + label);
        }
    }

    public static void assertEnabled(WebElement element, String label) {
        // Poll with global timeout
        long end = System.currentTimeMillis() + Timeouts.global().toMillis();
        while (System.currentTimeMillis() < end) {
            try {
                if (element.isEnabled()) return;
            } catch (Exception ignored) {
            }
            sleep(200);
        }
        assertFail("Element is DISABLED: " + label);
    }

    public static void assertDisabled(WebElement element, String label) {
        // Poll with global timeout
        long end = System.currentTimeMillis() + Timeouts.global().toMillis();
        while (System.currentTimeMillis() < end) {
            try {
                if (!element.isEnabled()) return;
            } catch (Exception ignored) {
            }
            sleep(200);
        }
        assertFail("Element is ENABLED (expected disabled): " + label);
    }

    public static void assertSelected(WebElement element, String label) {
        if (element == null || !element.isSelected()) assertFail("Element NOT selected: " + label);
    }



/* =======================================================
   GLOBALISED WAITS (WebElement parameters)
   ======================================================= */

    // WebDriverWait sources the single truth from Timeouts

    private static WebDriverWait waitClick() {
        return new WebDriverWait(driver(), Timeouts.click());
    }

    private static WebDriverWait waitType() {
        return new WebDriverWait(driver(), Timeouts.type());
    }

    // Centralized failure: shot + AssertionError
    private static void failWithShot(String message) {
        saveScreenshot("wait_or_action_fail_" + System.currentTimeMillis());
        throw new AssertionError(message);
    }

    // --- Frequently used waits ---
    public static WebElement waitForVisible(WebElement element, String label) {
        try {
            return waitGlobal().until(ExpectedConditions.visibilityOf(element));
        } catch (Exception e) {
            failWithShot("waitForVisible FAILED: " + label);
            return null;
        }
    }

    public static WebElement waitForClickable(WebElement element, String label) {
        try {
            return waitClick().until(ExpectedConditions.elementToBeClickable(element));
        } catch (Exception e) {
            failWithShot("waitForClickable FAILED: " + label);
            return null;
        }
    }

    public static boolean waitForInvisible(WebElement element, String label) {
        try {
            return waitGlobal().until(ExpectedConditions.invisibilityOf(element));
        } catch (Exception e) {
            failWithShot("waitForInvisible FAILED: " + label);
            return false;
        }
    }

    public static WebElement waitForEnabled(WebElement element, String label) {
        long end = System.currentTimeMillis() + Timeouts.global().toMillis();
        while (System.currentTimeMillis() < end) {
            try {
                if (element.isEnabled()) return element;
            } catch (Exception ignored) {
            }
            sleep(200);
        }
        failWithShot("waitForEnabled FAILED: " + label);
        return null;
    }

    public static void longPress(WebElement element, int durationMs) {
        Map<String, Object> params = new HashMap<>();
        params.put("elementId", id(element));
        params.put("duration", durationMs);

        if (isAndroid()) {
            js().executeScript("mobile: longClickGesture", params);
        } else {
            js().executeScript("mobile: touchAndHold", params);
        }
    }

    /* =======================================================
   ANDROID KEY EVENTS
   ======================================================= */
    // Android BACK key (keycode 4)
    public static void pressBack() {
        if (isAndroid()) {
            driver().executeScript("mobile: pressKey", Map.of("keycode", 4));
        }
    }

    // Android HOME key (keycode 3)
    public static void pressHome() {
        if (isAndroid()) {
            driver().executeScript("mobile: pressKey", Map.of("keycode", 3));
        }
    }

    // Android ENTER key (keycode 66)
    public static void pressEnter() {
        if (isAndroid()) {
            driver().executeScript("mobile: pressKey", Map.of("keycode", 66));
        }
    }

    // Android VOLUME UP (keycode 24)
    public static void pressVolumeUp() {
        if (isAndroid()) {
            driver().executeScript("mobile: pressKey", Map.of("keycode", 24));
        }
    }

    // Android VOLUME DOWN (keycode 25)
    public static void pressVolumeDown() {
        if (isAndroid()) {
            driver().executeScript("mobile: pressKey", Map.of("keycode", 25));
        }
    }

    // Android POWER button (keycode 26)
    public static void pressPower() {
        if (isAndroid()) {
            driver().executeScript("mobile: pressKey", Map.of("keycode", 26));
        }
    }

    // Android ESCAPE (mapped to BACK)
    public static void pressEscape() {
        pressBack(); // Escape == Back on Android
    }

    // Android MENU key (keycode 82)
    public static void pressMenu() {
        if (isAndroid()) {
            driver().executeScript(
                    "mobile: pressKey",
                    Map.of("keycode", 82)
            );
        }
    }

    // Android SEARCH key (keycode 84)
    public static void pressSearch() {
        if (isAndroid()) {
            driver().executeScript(
                    "mobile: pressKey",
                    Map.of("keycode", 84)
            );
        }
    }


    /* =======================================================
       COORDINATE TAPS (Android + iOS) — W3C Actions
       ======================================================= */
    public static void tapAt(int x, int y) {
        org.openqa.selenium.Dimension size = driver().manage().window().getSize();
        int safeX = Math.max(0, Math.min(x, size.getWidth() - 1));
        int safeY = Math.max(0, Math.min(y, size.getHeight() - 1));

        org.openqa.selenium.interactions.PointerInput finger =
                new org.openqa.selenium.interactions.PointerInput(org.openqa.selenium.interactions.PointerInput.Kind.TOUCH, "finger");
        org.openqa.selenium.interactions.Sequence tap = new org.openqa.selenium.interactions.Sequence(finger, 1);

        tap.addAction(finger.createPointerMove(java.time.Duration.ZERO,
                org.openqa.selenium.interactions.PointerInput.Origin.viewport(), safeX, safeY));
        tap.addAction(finger.createPointerDown(org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(finger.createPointerUp(org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()));

        driver().perform(java.util.Collections.singletonList(tap));


    }


}
