package utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public final class ConfigReader {

    private static final Properties PROPS = new Properties();

    private ConfigReader() {}

    /* =======================================================
       STATIC INIT
       ======================================================= */
    static {
        try {
            // 1️⃣ Always load common config
            loadFromClasspath("config/common.properties");
            System.out.println(">>> Loaded base config: common.properties");

            // 2️⃣ Load env-specific config (default = local)
            String env = System.getProperty("env", "local").toLowerCase();

            loadFromClasspathOptional("config/" + env + ".properties");
            System.out.println(">>> Loaded env config: " + env + ".properties");

        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to load configuration", e);
        }
    }

    /* =======================================================
       LOADERS
       ======================================================= */
    private static void loadFromClasspath(String path) throws Exception {
        try (InputStream is =
                     ConfigReader.class
                             .getClassLoader()
                             .getResourceAsStream(path)) {

            if (is == null) {
                throw new RuntimeException("Config file not found: " + path);
            }
            PROPS.load(is);
        }
    }

    private static void loadFromClasspathOptional(String path) throws Exception {
        try (InputStream is =
                     ConfigReader.class
                             .getClassLoader()
                             .getResourceAsStream(path)) {

            if (is != null) {
                PROPS.load(is);
            }
        }
    }

    /* =======================================================
       MAIN GETTERS
       ======================================================= */
    public static String get(String key) {

        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) {
            return sys;
        }

        String env = System.getenv(key);
        if (env != null && !env.isBlank()) {
            return env;
        }

        String val = PROPS.getProperty(key);
        if (val == null) return null;

        val = resolveEnv(val);
        return JasyptUtil.decryptIfNeeded(val);
    }

    public static String getOrDefault(String key, String def) {
        String val = get(key);
        return (val == null || val.isBlank()) ? def : val;
    }

    /* =======================================================
       INLINE ${ENV_VAR} RESOLUTION (FIXED)
       ======================================================= */
    private static String resolveEnv(String val) {
        if (val == null) return null;

        String result = val;

        while (result.contains("${")) {
            int start = result.indexOf("${");
            int end = result.indexOf("}", start);

            if (end == -1) break;

            String envKey = result.substring(start + 2, end);
            String envValue = System.getenv(envKey);

            if (envValue == null || envValue.isBlank()) {
                throw new RuntimeException(
                        "❌ Environment variable not found: " + envKey
                );
            }

            result = result.substring(0, start)
                    + envValue
                    + result.substring(end + 1);
        }

        return result;
    }

    /* =======================================================
       JSON HELPERS
       ======================================================= */
    public static JSONObject loadJsonFromClasspath(String resourcePath) {
        try (InputStream is =
                     ConfigReader.class
                             .getClassLoader()
                             .getResourceAsStream(resourcePath)) {

            if (is == null) {
                throw new RuntimeException(
                        "❌ Resource not found: " + resourcePath
                );
            }

            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(new InputStreamReader(is));

        } catch (Exception e) {
            throw new RuntimeException(
                    "❌ Failed to load JSON: " + resourcePath, e
            );
        }
    }
}
