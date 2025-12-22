package utils;

import models.DeviceConfig;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public final class DevicePool {

    private static final BlockingQueue<DeviceConfig> POOL =
            new LinkedBlockingQueue<>();

    private static volatile boolean INITIALIZED = false;

    private static final int WAIT_SEC = 120;

    private DevicePool() {}

    /* =======================================================
       INITIALIZE ON FIRST USE
       ======================================================= */
    private static synchronized void initIfRequired() {

        if (INITIALIZED) return;

        System.out.println("🔧 Initializing DevicePool...");

        JSONArray devices =
                ConfigReader.loadJsonArray("devices/android_devices.json");

        if (devices.isEmpty()) {
            throw new RuntimeException("❌ No devices found in device pool");
        }

        for (Object o : devices) {
            JSONObject obj = (JSONObject) o;

            DeviceConfig device = new DeviceConfig(
                    obj.get("device").toString(),
                    obj.get("os").toString()
            );

            POOL.offer(device);

            System.out.println(
                    "✅ Device added: "
                            + device.device()
                            + " | Android " + device.os()
            );
        }

        INITIALIZED = true;

        System.out.println(
                "✅ DevicePool ready with " + POOL.size() + " devices"
        );
    }

    /* =======================================================
       ACQUIRE (BLOCKING)
       ======================================================= */
    public static DeviceConfig acquire() {

        initIfRequired();

        try {
            DeviceConfig device = POOL.poll(WAIT_SEC, TimeUnit.SECONDS);

            if (device == null) {
                throw new RuntimeException(
                        "❌ No device available after " + WAIT_SEC + " seconds"
                );
            }

            return device;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("❌ Device acquire interrupted", e);
        }
    }

    /* =======================================================
       RELEASE
       ======================================================= */
    public static void release(DeviceConfig device) {

        if (device == null) return;

        POOL.offer(device);

        System.out.println(
                "🔓 Device released: "
                        + device.device()
                        + " | Android " + device.os()
        );
    }
}
