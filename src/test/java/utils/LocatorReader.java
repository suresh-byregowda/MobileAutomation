package utils;

import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 *  LocatorReader (Cross-platform)
 * -------------------------------
 * Supports JSON like:
 *
 *  "login": {
 *      "android": { "xpath": "..."},
 *      "ios": { "accessibilityId": "..."}
 *  }
 *
 * Automatically selects locator per platform.
 */
public final class LocatorReader {

    private static JSONObject rootLocators;

    static {
        rootLocators = safeLoad("locators/locators.json"); // unified Android + iOS JSON
    }

    private LocatorReader() {}

    /** Loads JSON safely with fallback */
    private static JSONObject safeLoad(String path) {
        try {
            return loadJson(path);
        } catch (Exception e) {
            System.out.println("[LocatorReader] WARNING: Failed to load " + path + " -> " + e.getMessage());
            return new JSONObject();
        }
    }

    /** Loads file from resources */
    private static JSONObject loadJson(String resourcePath) {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            InputStream is = cl.getResourceAsStream(resourcePath);

            if (is == null)
                throw new RuntimeException("Resource not found: " + resourcePath);

            byte[] bytes = is.readAllBytes();
            String jsonText = new String(bytes, StandardCharsets.UTF_8);
            return new JSONObject(jsonText);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load: " + resourcePath, e);
        }
    }

    /**
     * Returns a map (LocatorStrategy → value) for given page + element key.
     * Chooses Android or iOS sub-node automatically.
     */
    public static Map<LocatorStrategy, String> getLocatorSpec(PageName page, String elementKey) {

        String platform = ConfigReader.getOrDefault("platform", "android").toLowerCase();

        if (!rootLocators.has(page.jsonKey())) {
            System.out.println("[LocatorReader] WARNING: Page not found -> " + page.jsonKey());
            return Map.of();
        }

        JSONObject pageObj = rootLocators.getJSONObject(page.jsonKey());

        if (!pageObj.has(elementKey)) {
            System.out.println("[LocatorReader] WARNING: Missing locator key: " + elementKey +
                    " in page: " + page.jsonKey());
            return Map.of();
        }

        // element block -> should contain "android" & "ios"
        JSONObject elementObj = pageObj.getJSONObject(elementKey);

        // Pick correct platform
        JSONObject platformObj;
        if (elementObj.has(platform)) {
            platformObj = elementObj.getJSONObject(platform);
        } else {
            System.out.println("[LocatorReader] WARNING: Missing platform block '" + platform +
                    "' under " + page.jsonKey() + " -> " + elementKey +
                    ". Falling back to whatever exists.");

            // Fallback: get first available (android or ios)
            Iterator<String> keys = elementObj.keys();
            if (!keys.hasNext()) return Map.of();
            platformObj = elementObj.getJSONObject(keys.next());
        }

        Map<LocatorStrategy, String> spec = new HashMap<>();

        Iterator<String> keys = platformObj.keys();
        while (keys.hasNext()) {
            String jsonKey = keys.next();
            String value = platformObj.optString(jsonKey, "");

            if (value.isBlank()) continue;

            LocatorStrategy strategy = mapJsonKeyToStrategy(jsonKey);

            if (strategy != null) {
                spec.put(strategy, value);
            } else {
                System.out.println("[LocatorReader] WARNING: Unknown locator strategy: " +
                        jsonKey + " (page=" + page.jsonKey() + ", key=" + elementKey + ")");
            }
        }

        return spec;
    }

    /** Converts JSON key → LocatorStrategy enum */
    private static LocatorStrategy mapJsonKeyToStrategy(String jsonKey) {
        for (LocatorStrategy s : LocatorStrategy.values()) {
            if (s.jsonKey().equalsIgnoreCase(jsonKey)) {
                return s;
            }
        }
        return null;
    }
}
