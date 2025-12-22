package providers;

import models.DeviceConfig;
import org.testng.annotations.DataProvider;
import utils.ConfigReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class DeviceDataProvider {

    private DeviceDataProvider() {}

    /* =======================================================
       PUBLIC LOADER (USED BY DEVICE POOL)
       ======================================================= */
    public static List<DeviceConfig> loadDevices() {

        JSONArray devicesJson =
                ConfigReader.loadJsonArray("devices/android_devices.json");

        List<DeviceConfig> devices = new ArrayList<>();

        for (Object obj : devicesJson) {
            JSONObject d = (JSONObject) obj;

            String device = d.get("device").toString();
            String os     = d.get("os").toString();

            devices.add(new DeviceConfig(device, os));
        }

        System.out.println(
                ">>> Loaded " + devices.size() + " devices for execution"
        );

        return devices;
    }

    /* =======================================================
       TESTNG FACTORY PROVIDER (OPTIONAL, NOT USED IN POOL MODE)
       ======================================================= */
    @DataProvider(name = "devices", parallel = true)
    public static Object[][] devices() {

        List<DeviceConfig> devices = loadDevices();

        Object[][] data = new Object[devices.size()][1];
        for (int i = 0; i < devices.size(); i++) {
            data[i][0] = devices.get(i);
        }
        return data;
    }
}
