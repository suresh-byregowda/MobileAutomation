package utils;

import models.DeviceConfig;

public final class DeviceContext {

    private static final ThreadLocal<DeviceConfig> DEVICE =
            new ThreadLocal<>();

    private DeviceContext() {}

    public static void set(DeviceConfig device) {
        DEVICE.set(device);
    }

    public static DeviceConfig get() {
        return DEVICE.get();
    }

    public static void clear() {
        DEVICE.remove();
    }
}
