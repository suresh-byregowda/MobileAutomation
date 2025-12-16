
package utils;

import org.openqa.selenium.By;

import java.lang.reflect.Method;

public final class ByFactory {
    private ByFactory() {}

    private static final boolean HAS_APPIUMBY = hasClass("io.appium.java_client.AppiumBy");

    private static boolean hasClass(String name) {
        try { Class.forName(name); return true; }
        catch (ClassNotFoundException e) { return false; }
    }

    private static By invokeAppiumBy(String method, String value) {
        try {
            Class<?> appiumBy = Class.forName("io.appium.java_client.AppiumBy");
            Method m = appiumBy.getMethod(method, String.class);
            Object by = m.invoke(null, value);
            return (By) by;
        } catch (Exception e) {
            throw new IllegalStateException("AppiumBy." + method + " not available", e);
        }
    }

    public static LocatorStrategy[] defaultPriority() {
        return new LocatorStrategy[] {
                LocatorStrategy.ID,
                LocatorStrategy.ACCESSIBILITY_ID,
                LocatorStrategy.NAME,
                LocatorStrategy.CLASS_NAME,

                LocatorStrategy.IOS_CLASS_CHAIN,
                LocatorStrategy.IOS_PREDICATE,

                LocatorStrategy.TAG_NAME,
                LocatorStrategy.CSS_SELECTOR,

                LocatorStrategy.ANDROID_UIAUTOMATOR,

                LocatorStrategy.XPATH,
                LocatorStrategy.TEXT
        };
    }

    public static By from(LocatorStrategy s, String value) {
        switch (s) {
            case ID:           return By.id(value);
            case NAME:         return By.name(value);
            case CLASS_NAME:   return By.className(value);
            case TAG_NAME:     return By.tagName(value);
            case CSS_SELECTOR: return By.cssSelector(value);
            case XPATH:        return By.xpath(value);

            case ACCESSIBILITY_ID:
                if (HAS_APPIUMBY) return invokeAppiumBy("accessibilityId", value);
                return By.xpath("//*[@content-desc='" + value + "' or @name='" + value + "' or @label='" + value + "']");

            case ANDROID_UIAUTOMATOR:
                if (HAS_APPIUMBY) return invokeAppiumBy("androidUIAutomator", value);
                throw new IllegalArgumentException("ANDROID_UIAUTOMATOR requires Appium Java Client");

            case IOS_CLASS_CHAIN:
                if (HAS_APPIUMBY) return invokeAppiumBy("iOSClassChain", value);
                throw new IllegalArgumentException("IOS_CLASS_CHAIN requires Appium Java Client");

            case IOS_PREDICATE:
                if (HAS_APPIUMBY) return invokeAppiumBy("iOSNsPredicateString", value);
                return By.xpath("//*[@name='" + value + "' or @label='" + value + "']");

            case TEXT:
                return By.xpath("//*[@text='" + value + "' or @name='" + value + "' or @label='" + value + "']");

            default:
                throw new IllegalArgumentException("Unsupported strategy: " + s);
        }
    }
}
