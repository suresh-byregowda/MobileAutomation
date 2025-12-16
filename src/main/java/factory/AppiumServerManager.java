package factory;

import utils.ConfigReader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AppiumServerManager {

    private static Process appiumProcess;
    private static final String SERVER_URL = "http://127.0.0.1:4723";

    /* =======================================================
       START APPIUM SERVER (LOCAL ONLY)
       ======================================================= */
    public static void startServerIfRequired() {

        String runEnv = ConfigReader.getOrDefault("run_env", "local");

        if (!"local".equalsIgnoreCase(runEnv)) {
            System.out.println(">>> run_env=" + runEnv + " → Skipping Appium server start");
            return;
        }

        if (isServerRunning()) {
            System.out.println(">>> Appium server already running.");
            return;
        }

        try {
            System.out.println(">>> Starting Appium Server (local execution)");

            ProcessBuilder builder = new ProcessBuilder(
                    "appium",
                    "--address", "127.0.0.1",
                    "--port", "4723"
            );

            builder.redirectErrorStream(true);
            appiumProcess = builder.start();

            // Print Appium logs
            new Thread(() -> {
                try (BufferedReader reader =
                             new BufferedReader(
                                     new InputStreamReader(appiumProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[APPIUM] " + line);
                    }
                } catch (Exception ignored) {}
            }).start();

            waitUntilServerIsUp();

            System.out.println(">>> Appium Server started successfully.");

        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to start Appium server", e);
        }
    }

    /* =======================================================
       STOP APPIUM SERVER (LOCAL ONLY)
       ======================================================= */
    public static void stopServerIfRequired() {

        String runEnv = ConfigReader.getOrDefault("run_env", "local");

        if (!"local".equalsIgnoreCase(runEnv)) {
            return;
        }

        if (appiumProcess != null && appiumProcess.isAlive()) {
            appiumProcess.destroy();
            System.out.println(">>> Appium Server stopped.");
        }
    }

    /* =======================================================
       HEALTH CHECK
       ======================================================= */
    private static boolean isServerRunning() {
        try {
            HttpURLConnection conn =
                    (HttpURLConnection) new URL(SERVER_URL + "/status").openConnection();
            conn.setConnectTimeout(1000);
            conn.connect();
            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    private static void waitUntilServerIsUp() throws InterruptedException {
        int retries = 20;
        while (retries-- > 0) {
            if (isServerRunning()) {
                return;
            }
            Thread.sleep(1000);
        }
        throw new RuntimeException("❌ Appium server did not start in time");
    }
}
