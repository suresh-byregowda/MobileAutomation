package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

public class JsonCapabilityLoader {

    private static JsonNode rootNode;

    static {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = JsonCapabilityLoader.class
                    .getClassLoader()
                    .getResourceAsStream("local.json");

            if (is == null) {
                throw new RuntimeException("❌ local.json not found in resources");
            }

            rootNode = mapper.readTree(is);

        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to load local.json", e);
        }
    }

    public static JsonNode getPlatformCaps(String platform) {
        JsonNode node = rootNode.get(platform.toLowerCase());
        if (node == null) {
            throw new RuntimeException(
                    "❌ No capabilities found for platform: " + platform
            );
        }
        return node;
    }
}
