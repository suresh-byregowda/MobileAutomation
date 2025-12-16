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
       STATIC INIT – LOAD CONFIG (BASE + OPTIONAL ENV)
       ======================================================= */
    static {
        try {
            // 1️⃣ Always load base config
            loadFromClasspath("config/common.properties");

            // 2️⃣ Optional env override (qa / uat / staging)
            String env = System.getProperty("env");

            if (env != null && !env.isBlank()) {
                loadFromClasspathOptional("config/" + env.toLowerCase() + ".properties");
                System.out.println(">>> Loaded env config: " + env + ".properties");
            } else {
                System.out.println(">>> No env specified. Using base config only.");
            }

        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to load configuration", e);
        }
    }

    /* =======================================================
       PROPERTY FILE LOADERS
       ======================================================= */
    private static void loadFromClasspath(String path) throws Exception {
        try (InputStream is =
                     ConfigReader.class.getClassLoader().getResourceAsStream(path)) {

            if (is == null) {
                throw new RuntimeException("Config file not found: " + path);
            }
            PROPS.load(is);
        }
    }

    private static void loadFromClasspathOptional(String path) throws Exception {
        try (InputStream is =
                     ConfigReader.class.getClassLoader().getResourceAsStream(path)) {

            if (is != null) {
                PROPS.load(is);
            }
        }
    }

    /* =======================================================
       MAIN GETTER (SYSTEM > ENV > FILE)
       ======================================================= */
    public static String get(String key) {

        // 1️⃣ JVM property (-Dkey=value)
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) {
            return sys;
        }

        // 2️⃣ OS environment variable
        String env = System.getenv(key);
        if (env != null && !env.isBlank()) {
            return env;
        }

        // 3️⃣ Properties file
        String val = PROPS.getProperty(key);
        if (val == null) return null;

        // 4️⃣ ${ENV_VAR} substitution
        val = resolveEnv(val);

        // 5️⃣ Jasypt decrypt if needed
        return JasyptUtil.decryptIfNeeded(val);
    }

    public static String getOrDefault(String key, String def) {
        String val = get(key);
        return (val == null || val.isBlank()) ? def : val;
    }

    /* =======================================================
       VARIABLE SUBSTITUTION ${VAR}
       ======================================================= */
    private static String resolveEnv(String val) {
        if (val != null && val.startsWith("${") && val.endsWith("}")) {
            String envKey = val.substring(2, val.length() - 1);
            return System.getenv(envKey);
        }
        return val;
    }

    /* =======================================================
       JSON SUPPORT (CLASSPATH SAFE)
       ======================================================= */
    public static JSONObject loadJson(String path) {
        try (InputStream is =
                     ConfigReader.class.getClassLoader().getResourceAsStream(path)) {

            if (is == null) {
                throw new RuntimeException("JSON file not found: " + path);
            }

            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(new InputStreamReader(is));

        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to load JSON file: " + path, e);
        }
    }

    // utils/ConfigReader.java
    public static JSONObject loadJsonFromClasspath(String resourcePath) {
        try (InputStream is =
                     ConfigReader.class
                             .getClassLoader()
                             .getResourceAsStream(resourcePath)) {

            if (is == null) {
                throw new RuntimeException("❌ Resource not found on classpath: " + resourcePath);
            }

            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(new InputStreamReader(is));

        } catch (Exception e) {
            throw new RuntimeException(
                    "❌ Failed to load JSON from classpath: " + resourcePath, e
            );
        }
    }

}
