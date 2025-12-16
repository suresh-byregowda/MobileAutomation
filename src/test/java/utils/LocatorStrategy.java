
package utils;

public enum LocatorStrategy {
    ID("id"),
    NAME("name"),
    CLASS_NAME("className"),
    TAG_NAME("tag"),
    CSS_SELECTOR("css"),
    XPATH("xpath"),
    ACCESSIBILITY_ID("accessibilityId"),
    ANDROID_UIAUTOMATOR("androidUIAutomator"),
    TEXT("text"),               // optional, for text-based matching
    IOS_CLASS_CHAIN("iosClassChain"),
    IOS_PREDICATE("iosPredicate");

    private final String jsonKey;

    LocatorStrategy(String jsonKey) {
        this.jsonKey = jsonKey;
    }

    public String jsonKey() {
        return jsonKey;
    }
}
