
package tools;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class KeyGenerator {

    public static void main(String[] args) throws Exception {
        // Adjust input/output paths for your project
        File json = new File("src/test/resources/locators/locators.json"); // or ios.json
        File outDir = new File("src/test/java/utils/keys");
        outDir.mkdirs();

        String jsonText = new String(java.nio.file.Files.readAllBytes(json.toPath()), StandardCharsets.UTF_8);
        JSONObject root = new JSONObject(jsonText);

        // For each page (top-level key) create an enum with constants from locator keys
        Iterator<String> pages = root.keys();
        while (pages.hasNext()) {
            String pageName = pages.next();         // e.g., "onTheGo"
            JSONObject pageObj = root.getJSONObject(pageName);

            String enumName = pageToEnum(pageName); // e.g., "OnTheGoKey"
            File outFile = new File(outDir, enumName + ".java");

            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile), StandardCharsets.UTF_8))) {
                pw.println("package utils.keys;");
                pw.println();
                pw.println("import utils.PageName;");
                pw.println();
                pw.println("public enum " + enumName + " implements LocatorKey {");

                int count = 0;
                Iterator<String> keys = pageObj.keys();
                while (keys.hasNext()) {
                    String key = keys.next();                   // e.g., "Email_Input"
                    String constName = keyToEnumConst(key);     // e.g., "EMAIL_INPUT"
                    pw.print("    " + constName + "(\"" + key + "\")");
                    count++;
                    pw.println(keys.hasNext() ? "," : ";");
                }
                pw.println();
                pw.println("    private final String key;");
                pw.println("    " + enumName + "(String key) { this.key = key; }");
                pw.println("    @Override public PageName page() { return PageName." + pageNameToPageEnum(pageName) + "; }");
                pw.println("    @Override public String key()    { return key; }");
                pw.println("}");
            }

            System.out.println("Generated: " + outFile.getAbsolutePath());
        }
    }

    private static String pageToEnum(String page) {
        // "onTheGo" -> "OnTheGoKey", "FormPage" -> "FormPageKey"
        String cap = Character.toUpperCase(page.charAt(0)) + page.substring(1);
        return cap + "Key";
    }

    private static String pageNameToPageEnum(String page) {
        // Must match utils.PageName constants
        switch (page) {
            case "FormPage":
                return "FORMPAGE";
            case "ProductPage":
                return "PRODUCTPAGE";
            case "CartPage":
                return "CARTPAGE";
            case "onTheGo":
                return "ONTHEGO";
            case "NewPage":
                return "NEWPAGE";

            default:
                throw new IllegalArgumentException("Unknown page: " + page);
        }
    }

    private static String keyToEnumConst(String key) {
        // "Email_Input" -> "EMAIL_INPUT", "Next_Button" -> "NEXT_BUTTON", "login" -> "LOGIN"
        return key.trim()
                .replaceAll("[^A-Za-z0-9]+", "_")
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toUpperCase();
    }
}