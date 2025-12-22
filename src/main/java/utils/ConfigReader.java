package utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public final class ConfigReader {

    private static final Properties PROPS = new Properties();

    private ConfigReader() {}

    /* =======================================================
       STATIC INIT
       ======================================================= */
    static {
        try {
            loadFromClasspath("config/common.properties");
            System.out.println(">>> Loaded base config: common.properties");

            String env =
                    System.getProperty(
                            "env",
                            System.getenv().getOrDefault("env", "local")
                    ).toLowerCase();

            loadFromClasspathOptional("config/" + env + ".properties");
            System.out.println(">>> Loaded env config: " + env + ".properties");

        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to load configuration", e);
        }
    }

    /* =======================================================
       PROPERTIES LOADERS
       ======================================================= */
    private static void loadFromClasspath(String path) throws Exception {
        try (InputStream is =
                     ConfigReader.class
                             .getClassLoader()
                             .getResourceAsStream(path)) {

            if (is == null) {
                throw new RuntimeException("❌ Config file not found: " + path);
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
       PROPERTY GETTERS
       ======================================================= */
    public static String get(String key) {

        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) return sys;

        String env = System.getenv(key);
        if (env != null && !env.isBlank()) return env;

        String val = PROPS.getProperty(key);
        if (val == null) return null;

        return JasyptUtil.decryptIfNeeded(resolveEnv(val));
    }

    public static String getOrDefault(String key, String def) {
        String val = get(key);
        return (val == null || val.isBlank()) ? def : val;
    }

    /* =======================================================
       ${ENV_VAR} RESOLUTION
       ======================================================= */
    private static String resolveEnv(String val) {

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
       JSON HELPERS (THREAD-SAFE)
       ======================================================= */

    public static JSONObject loadJsonFromClasspath(String resourcePath) {

        try (InputStream is =
                     ConfigReader.class
                             .getClassLoader()
                             .getResourceAsStream(resourcePath)) {

            if (is == null) {
                throw new RuntimeException(
                        "❌ JSON resource not found: " + resourcePath
                );
            }

            Object parsed =
                    new JSONParser()
                            .parse(new InputStreamReader(is, StandardCharsets.UTF_8));

            if (!(parsed instanceof JSONObject)) {
                throw new RuntimeException(
                        "❌ Expected JSON object but found: "
                                + parsed.getClass().getSimpleName()
                );
            }

            return (JSONObject) parsed;

        } catch (Exception e) {
            throw new RuntimeException(
                    "❌ Failed to load JSON object: " + resourcePath,
                    e
            );
        }
    }

    public static JSONArray loadJsonArray(String resourcePath) {

        try (InputStream is =
                     ConfigReader.class
                             .getClassLoader()
                             .getResourceAsStream(resourcePath)) {

            if (is == null) {
                throw new RuntimeException(
                        "❌ JSON array resource not found: " + resourcePath
                );
            }

            Object parsed =
                    new JSONParser()
                            .parse(new InputStreamReader(is, StandardCharsets.UTF_8));

            if (!(parsed instanceof JSONArray)) {
                throw new RuntimeException(
                        "❌ Expected JSON array but found: "
                                + parsed.getClass().getSimpleName()
                );
            }

            return (JSONArray) parsed;

        } catch (Exception e) {
            throw new RuntimeException(
                    "❌ Failed to load JSON array: " + resourcePath,
                    e
            );
        }
    }

    /* =======================================================
       CONVENIENCE FLAGS
       ======================================================= */

    public static boolean isBrowserStack() {
        return "browserstack".equalsIgnoreCase(
                getOrDefault("run_env", "local")
        );
    }

    public static boolean isLocal() {
        return "local".equalsIgnoreCase(
                getOrDefault("run_env", "local")
        );
    }
}
