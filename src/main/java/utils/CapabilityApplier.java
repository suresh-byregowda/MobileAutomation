package utils;

import com.fasterxml.jackson.databind.JsonNode;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.options.XCUITestOptions;

import java.time.Duration;
import java.util.Iterator;
import java.util.Map;

public class CapabilityApplier {

    /* =======================================================
       ANDROID CAPS
       ======================================================= */
    public static void applyAndroidCaps(
            UiAutomator2Options options,
            JsonNode capsNode
    ) {

        Iterator<Map.Entry<String, JsonNode>> fields = capsNode.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String key = entry.getKey();
            JsonNode value = entry.getValue();

            switch (key) {

                case "appium:newCommandTimeout" ->
                        options.setNewCommandTimeout(
                                Duration.ofSeconds(value.asLong())
                        );

                case "appium:uiautomator2ServerLaunchTimeout" ->
                        options.setUiautomator2ServerLaunchTimeout(
                                Duration.ofMillis(value.asLong())
                        );

                case "appium:adbExecTimeout" ->
                        options.setAdbExecTimeout(
                                Duration.ofMillis(value.asLong())
                        );

                case "appium:noReset" ->
                        options.setNoReset(value.asBoolean());

                case "appium:autoGrantPermissions" ->
                        options.setAutoGrantPermissions(value.asBoolean());

                case "appium:nativeWebScreenshot" ->
                        options.setNativeWebScreenshot(value.asBoolean());

                case "appium:autoWebview" ->
                        options.setAutoWebview(value.asBoolean());

                default ->
                        options.setCapability(
                                key,
                                value.isNumber()
                                        ? value.numberValue()
                                        : value.asText()
                        );
            }
        }
    }

    /* =======================================================
       IOS CAPS (FIXED)
       ======================================================= */
    public static void applyIosCaps(
            XCUITestOptions options,
            JsonNode capsNode
    ) {

        Iterator<Map.Entry<String, JsonNode>> fields = capsNode.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String key = entry.getKey();
            JsonNode value = entry.getValue();

            switch (key) {

                case "appium:newCommandTimeout" ->
                        options.setNewCommandTimeout(
                                Duration.ofSeconds(value.asLong())
                        );

                case "appium:wdaLaunchTimeout" ->
                        options.setWdaLaunchTimeout(
                                Duration.ofMillis(value.asLong())
                        );

                case "appium:wdaConnectionTimeout" ->
                        options.setWdaConnectionTimeout(
                                Duration.ofMillis(value.asLong())
                        );

                case "appium:noReset" ->
                        options.setNoReset(value.asBoolean());

                case "appium:autoAcceptAlerts" ->
                        options.setAutoAcceptAlerts(value.asBoolean());

                default ->
                        options.setCapability(
                                key,
                                value.isNumber()
                                        ? value.numberValue()
                                        : value.asText()
                        );
            }
        }
    }
}
