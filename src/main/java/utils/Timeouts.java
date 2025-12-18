
package utils;

import java.time.Duration;

/**
 * Centralised timeouts for waits/actions.
 * Priority: System properties (-D...), then ConfigReader, then hard defaults.
 *
 * Runtime examples:
 *   mvn test -DglobalTimeoutSeconds=30
 *   mvn test -DclickTimeoutSeconds=20 -DtypeTimeoutSeconds=25
 */
public final class Timeouts {
    private Timeouts() {}

    // Hard defaults (used only if nothing is provided)
    private static final int DEFAULT_GLOBAL = 10;
    private static final int DEFAULT_CLICK  = DEFAULT_GLOBAL;
    private static final int DEFAULT_TYPE   = DEFAULT_GLOBAL;

    private static int readInt(String sysProp, String cfgKey, int defVal) {
        // 1) System property
        String sys = System.getProperty(sysProp);
        if (sys != null && !sys.isBlank()) {
            try { return Integer.parseInt(sys.trim()); } catch (Exception ignored) {}
        }
        // 2) ConfigReader (supports env & properties) -> your project’s helper
        try {
            String cfgVal = ConfigReader.getOrDefault(cfgKey, String.valueOf(defVal));
            if (cfgVal != null && !cfgVal.isBlank()) return Integer.parseInt(cfgVal.trim());
        } catch (Exception ignored) {}
        // 3) Fallback
        return defVal;
    }

    /** Global timeout (used everywhere unless a specific one overrides). */
    public static Duration global() {
        int secs = readInt("globalTimeoutSeconds", "timeouts.global", DEFAULT_GLOBAL);
        return Duration.ofSeconds(secs);
    }

    /** Click-specific timeout (falls back to global). */
    public static Duration click() {
        int secs = readInt("clickTimeoutSeconds", "timeouts.click",
                readInt("globalTimeoutSeconds", "timeouts.global", DEFAULT_GLOBAL));
        return Duration.ofSeconds(secs);
    }

    /** Type/sendKeys timeout (falls back to global). */
    public static Duration type() {
        int secs = readInt("typeTimeoutSeconds", "timeouts.type",
                readInt("globalTimeoutSeconds", "timeouts.global", DEFAULT_GLOBAL));
        return Duration.ofSeconds(secs);
    }
}
