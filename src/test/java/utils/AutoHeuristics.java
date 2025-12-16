
package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class AutoHeuristics {

    private AutoHeuristics() {}

    public static List<AutoCandidate> generate(PageName page, String key,
                                               Map<LocatorStrategy, String> spec,
                                               String platform) {
        List<AutoCandidate> out = new ArrayList<>();
        boolean isAndroid = platform.toLowerCase().contains("android");
        boolean isIos     = platform.toLowerCase().contains("ios");

        if (isAndroid && spec.containsKey(LocatorStrategy.ID)) {
            String id = spec.get(LocatorStrategy.ID);
            out.add(new AutoCandidate(LocatorStrategy.XPATH, "//*[@resource-id='" + id + "']"));
        }

        if (isIos && spec.containsKey(LocatorStrategy.ACCESSIBILITY_ID)) {
            String acc = spec.get(LocatorStrategy.ACCESSIBILITY_ID);
            out.add(new AutoCandidate(LocatorStrategy.IOS_PREDICATE,
                    "name == '" + acc + "' OR label == '" + acc + "'"));
        }

        if (!spec.containsKey(LocatorStrategy.CLASS_NAME)) {
            if (isAndroid && key.toLowerCase().contains("button")) {
                out.add(new AutoCandidate(LocatorStrategy.CLASS_NAME, "android.widget.Button"));
            }
        }
        if (isIos && !spec.containsKey(LocatorStrategy.IOS_CLASS_CHAIN) &&
                key.toLowerCase().contains("button")) {
            out.add(new AutoCandidate(LocatorStrategy.IOS_CLASS_CHAIN, "**/XCUIElementTypeButton"));
        }

        if (spec.containsKey(LocatorStrategy.TEXT)) {
            String v = spec.get(LocatorStrategy.TEXT);
            if (isAndroid) {
                out.add(new AutoCandidate(LocatorStrategy.XPATH, "//*[@text='" + v + "']"));
            } else if (isIos) {
                out.add(new AutoCandidate(LocatorStrategy.IOS_PREDICATE,
                        "(name == '" + v + "' OR label == '" + v + "')"));
            } else {
                out.add(new AutoCandidate(LocatorStrategy.XPATH,
                        "//*[@text='" + v + "' or @name='" + v + "' or @label='" + v + "']"));
            }
        }

        return out;
    }

    public static final class AutoCandidate {
        private final LocatorStrategy strategy;
        private final String value;
        public AutoCandidate(LocatorStrategy s, String v) { this.strategy = s; this.value = v; }
        public LocatorStrategy strategy() { return strategy; }
        public String value() { return value; }
    }
}
